package com.itsaur.internship;

import io.vertx.core.Future;

public class ContentService implements ContentStore{

    public ContentStore contentStore;

    public ContentService(ContentStore contentStore){
        this.contentStore = contentStore;
    }

    @Override
    public Future<Void> insertImage(String username, String filename) {


        return null;
    }

    @Override
    public Future<Void> insertComment() {
        return null;
    }
}
