package com.itsaur.internship.query;

import io.vertx.core.Future;

import java.util.List;
import java.util.UUID;

public interface PostQueryModelStore {
    Future<PostQueryModel> findById(UUID postId);

    Future<List<PostQueryModel>> findByUserId(UUID uuid);
}
