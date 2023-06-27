package com.itsaur.internship.Post;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

public class PostService {

    public PostStore postStore;

    public PostService(PostStore postStore) {
        this.postStore = postStore;
    }


    public Future<Void> addPost(String username, String filename, String description) {
//        return this.contentStore.findUser(username)
//                .compose(q -> {
//                    return this.contentStore.insertImage(username, filename, description);
//                })
        return null;
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
