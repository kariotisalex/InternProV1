package com.itsaur.internship.post.query;

import io.vertx.core.Future;

import java.util.UUID;

public interface PostQueryModelStore {
    Future<PostQueryModel.PostsQueryModel> findById(UUID postId);


    public Future<PostQueryModel> customizeFeed(UUID userid, int startFrom, int size);


    public Future<PostQueryModel> findPostPageByUid(UUID uid, int startFrom, int size);
}
