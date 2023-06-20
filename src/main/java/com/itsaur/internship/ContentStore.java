package com.itsaur.internship;

import io.vertx.core.Future;

public interface ContentStore {

    public Future<Void> insertImage(String username, String filename);

    public Future<Void> insertComment();


}
