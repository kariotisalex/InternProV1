package com.itsaur.internship.Post;


import com.itsaur.internship.Comment.CommentStore;
import com.itsaur.internship.User.UsersStore;
import io.vertx.core.Future;

import java.util.List;

public class PostService {

    private PostStore postStore;
    private UsersStore usersStore;

    private CommentStore commentStore;

    public PostService(PostStore postStore, UsersStore usersStore, CommentStore commentStore) {
        this.postStore = postStore;
        this.usersStore = usersStore;
        this.commentStore = commentStore;
    }


    public Future<Void> addPost(String username, String filename, String description) {
        return this.usersStore.findUser(username)
                .compose(user -> {
                    Post post = new Post(filename, description,user);
                    return this.postStore.insert(post);
                });
    }

    public Future<Void> updatePost(String username, String description){
        return this.usersStore.findUser(username)
                .compose(user -> {
                    return this.postStore.updatePost(user, description);
                });
    }

    public Future<Void> deletePost(String username, String filename){
        return Future.all(this.usersStore.findUser(username),
                          this.postStore.findPost(filename))
                .compose(res -> {
                    return this.commentStore.deleteFromPost(res.resultAt(1))
                            .compose(q ->{
                                return this.postStore.deletePost(filename);
                            });
                });

    }


    public Future<List<String>> retrieveAllPosts(String username){
        return this.usersStore.findUser(username)
                .compose(user -> {
                    return postStore.retrieveAll(user);
                });
    }
}
