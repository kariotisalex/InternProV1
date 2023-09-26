package com.itsaur.internship.follower.query;

import io.vertx.core.Future;

import java.util.List;
import java.util.UUID;

public interface FollowerQueryModelStore {

    public Future<List<FollowerQueryModel>> followingUsers (UUID followerid);

    public Future<List<FollowerQueryModel>> followers (UUID userid);
}
