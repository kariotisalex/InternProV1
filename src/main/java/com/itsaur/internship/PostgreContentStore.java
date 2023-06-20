package com.itsaur.internship;

import io.vertx.core.Future;

public class PostgreContentStore implements ContentStore{
    @Override
    public Future<Void> insertImage(String username, String filename) {
        return ;
    }

    @Override
    public Future<Void> insertComment() {
        return null;
    }
}
