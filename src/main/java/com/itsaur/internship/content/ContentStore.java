package com.itsaur.internship.content;

import io.vertx.core.Future;

import java.util.List;

public interface ContentStore {

    public Future<Void> insertImage(String username, String filename, String description);

    public Future<Void> insertComment(String filename, String comment);

    public Future<Void> findUser(String username);

    public Future<Void> findImage(String filename);

    public Future<Void> deleteImage(String filename);

    public Future<Void> deleteComment(String commentid);

    public Future<List<String>> retrieveAllImage(String username);

}
