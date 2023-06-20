package com.itsaur.internship;

import io.vertx.core.Future;

public class ContentService{

    public ContentStore contentStore;

    public ContentService(ContentStore contentStore){
        this.contentStore = contentStore;
    }


    public Future<Void> addPost(String username, String filename) {
        this.contentStore.insertImage(username,filename)
                .onSuccess(w->{

                });

        return null;
    }


    public Future<Void> insertComment() {
        return null;
    }
}
