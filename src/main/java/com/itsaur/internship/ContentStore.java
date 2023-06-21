package com.itsaur.internship;

import io.vertx.core.Future;

public interface ContentStore {

    public Future<Void> insertImage(String username, String filename, String description);

    public Future<Void> insertComment(String filename, String comment);

    public Future<Void> findUser(String username);

}
