package com.itsaur.internship.user.query;

import io.vertx.core.Future;

import java.util.List;

public interface UserQueryModelStore {
    public Future<List<UserQueryModel>> findAllUsersByUsername(String username);
}
