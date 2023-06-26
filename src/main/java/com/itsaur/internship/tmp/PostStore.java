package com.itsaur.internship.tmp;

import io.vertx.core.Future;
import io.vertx.ext.web.FileUpload;

import java.util.List;
import java.util.UUID;

public interface PostStore {

    public Future<Void> insert(String username, String filename, String description);

    public Future<Void> find(String filename);

    public Future<Void> update(String description);
    public Future<Void> delete(String filename);

    public Future<List<String>> retrieveAll(UUID userid);

}
