package com.itsaur.internship.post;

import io.vertx.core.Future;
import java.util.List;
import java.util.UUID;

public interface PostStore {

    public Future<Void> insert(Post post);

    public Future<Post> findPostByFilename(String filename);

    public Future<Void> updatePost(Post post);

    public Future<Void> deleteByFilename(String filename);

    public Future<List<String>> retrieveAll(UUID user);

}
