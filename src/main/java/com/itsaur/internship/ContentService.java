package com.itsaur.internship;

import io.vertx.core.Future;

public class ContentService{

    public ContentStore contentStore;

    public ContentService(ContentStore contentStore){
        this.contentStore = contentStore;
    }


    public Future<Void> addPost(String username, String filename, String description) {
        return this.contentStore.findUser(username)
                .compose(q -> {
                    return this.contentStore.insertImage(username, filename, description);
                });
    }


    public Future<Void> insertComment() {
        return null;
    }
}
