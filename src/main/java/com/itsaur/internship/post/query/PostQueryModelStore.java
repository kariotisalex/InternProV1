package com.itsaur.internship.post.query;

import com.itsaur.internship.post.Post;
import io.vertx.core.Future;

import java.util.List;
import java.util.UUID;

public interface PostQueryModelStore {
    Future<PostQueryModel> findById(UUID postId);

    Future<List<PostQueryModel>> findByUserId(UUID uid);

    public Future<Post> findPostByFilename(String filename);

    public Future<List<PostQueryModel>> findAllByUid(UUID uid);
}
