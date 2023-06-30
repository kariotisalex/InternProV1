package com.itsaur.internship.post;


import com.itsaur.internship.comment.CommentStore;
import com.itsaur.internship.user.UsersStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostService {

    private Vertx vertx;
    private PostStore postStore;
    private UsersStore usersStore;
    private CommentStore commentStore;

    public PostService( PostStore postStore, UsersStore usersStore, CommentStore commentStore) {
        this.postStore = postStore;
        this.usersStore = usersStore;
        this.commentStore = commentStore;
    }


    public Future<Void> addPost(String username, String filename, String description) {
        return this.usersStore.findUserByUsername(username)
                .compose(user -> {
                    Post post = new Post(filename, description,user.getUserid());
                    return this.postStore.insert(post);
                });
    }

    public Future<Void> updatePost(String username, String filename, String description){
        return this.usersStore.findUserByUsername(username)
                .compose(user -> {
                    return this.postStore.updatePost(
                            new Post(filename, description, user.getUserid()));
                });
    }

    public Future<Void> deleteAllPosts(String username){
        return this.usersStore.findUserByUsername(username)
                .compose(user -> {
                    return this.postStore.retrieveAll(user.getUserid())
                        .compose(res -> {
                            return res.stream().map(o -> {
                                return this.postStore.findPostByFilename(o)
                                    .compose(w -> {
                                        return this.commentStore.deleteByPost(w.getPostid());
                                    });
                            }).collect(Collectors.toList()).get(0);
                        });
                });
    }


    public Future<Void> deletePost(String username, String filename){
        return this.usersStore.findUserByUsername(username)
                .compose(res -> {
                    return this.commentStore.deleteByPost(res.getUserid())
                            .compose(q ->{
                                return this.postStore.deleteByFilename(filename);
                            });
                });

    }


    public Future<List<String>> retrieveAllPosts(String username){
        return this.usersStore.findUserByUsername(username)
                .compose(user -> {
                    return postStore.retrieveAll(user.getUserid());
                });
    }
}
