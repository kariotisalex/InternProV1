package com.itsaur.internship.user.query;

import io.vertx.core.Future;

import java.util.List;

public interface UserQueryModelStore {
    public Future<List<UserQueryModel>> findUsersPageByUsername(String username, int startFrom, int size);
}
