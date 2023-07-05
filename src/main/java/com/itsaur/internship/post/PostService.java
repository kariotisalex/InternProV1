package com.itsaur.internship.post;


import com.itsaur.internship.comment.CommentStore;
import com.itsaur.internship.user.UsersStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostService {

    private Vertx vertx;
    private PostStore postStore;
    private UsersStore usersStore;
    private CommentStore commentStore;

    public PostService(Vertx vertx, PostStore postStore, UsersStore usersStore, CommentStore commentStore) {
        this.vertx = vertx;
        this.postStore = postStore;
        this.usersStore = usersStore;
        this.commentStore = commentStore;
    }

    public Future<Void> addPost(UUID userid, String filename, String description) {
        return this.usersStore.findUserByUserid(userid)
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(user -> {
                    Post post = new Post(filename, description,user.getUserid());
                    return this.postStore.insert(post);
                });
    }

    public Future<Void> updatePost(UUID userid, UUID postid, String description){
        return this.usersStore.findUserByUserid(userid)
                .compose(user -> {
                    return this.postStore.findPostByPostid(postid)
                            .compose(post -> {
                                post.setUpdatedDate(LocalDateTime.now());
                                post.setDescription(description);
                                return this.postStore.updatePost(post);
                            });
                });
    }

    public Future<Void> deleteAllPosts(UUID userid){
        return this.postStore.retrieveAllByUserid(userid)
                .otherwiseEmpty()
                .compose(res -> {
                    if (res == null) {
                        System.out.println("There is no posts in this userid");
                        return Future.succeededFuture();
                    } else {
                        List<Future<Void>> futureList = res
                                .stream()
                                .map(w -> {
                                    System.out.println("asd "+w.getFilename());
                                    return deletePost(w.getUserid(),w.getPostid());
                                }).collect(Collectors.toList());
                    System.out.println(futureList);
                        return Future.all(futureList)
                                .onFailure(e -> {
                                    e.printStackTrace();
                                }).compose(q -> {
                                    System.out.println(q);
                                    return Future.succeededFuture();
                                });

                    }
                });
    }



    public Future<Void> deletePost(UUID userid, UUID postid){
        String filename;
        return this.usersStore.findUserByUserid(userid)
                .compose(res ->{
                    return this.commentStore.deleteByPostid(postid)
                            .compose(q -> {
                                return this.postStore.findPostByPostid(postid)
                                        .compose(w -> {
                                            return this.postStore.delete(postid)
                                                    .compose(re -> {
                                                        return vertx
                                                                .fileSystem()
                                                                .delete(String.valueOf(Paths.get("images", w.getFilename()).toAbsolutePath()))
                                                                .onFailure(e -> {
                                                                    e.printStackTrace();
                                                                });
                                            });
                                        });
                            });
                });

    }

}
