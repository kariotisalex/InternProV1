package com.itsaur.internship.Post;


import com.itsaur.internship.User.PostgresUsersStore;
import com.itsaur.internship.User.UsersStore;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostService {

    private PostStore postStore;
    private UsersStore usersStore;

    public PostService(PostStore postStore, UsersStore usersStore) {
        this.postStore = postStore;
        this.usersStore = usersStore;
    }


    public Future<Void> addPost(String username, String filename, String description) {
        return this.usersStore.findUser(username)
                .compose(q -> {
                    Post post = new Post(filename, description,q);
                    return this.postStore.insert(post);
                });


    }

    public Future<List<String>> retrieveAllPosts(String username){
//        return this.contentStore.retrieveAllImage(username);
        List<String> a = new ArrayList<>();
        return Future.succeededFuture(a);
    }

    public Future<Void> deletePost(String filename){
//        return this.contentStore.findImage(filename)
//                .compose(w -> {
//                    return contentStore.deleteImage(filename);
//                });
        return null;
    }
}
