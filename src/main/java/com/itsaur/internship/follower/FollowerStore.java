package com.itsaur.internship.follower;

import io.vertx.core.Future;

import java.util.UUID;

public interface FollowerStore {

    public Future<Void> insert(Follower follower);

    public Future<Void> delete(Follower follower);
    public Future<Void> deleteAll (UUID userid);
    public Future<Follower> findByUseridFollowerid(UUID userid, UUID followerid);

}
