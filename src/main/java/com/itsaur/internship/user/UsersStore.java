package com.itsaur.internship.user;

import io.vertx.core.Future;

import java.util.UUID;

public interface UsersStore {

    Future<Void> insert(User user);

    Future<User> findUserByUsername(String username);
    Future<User> findUserByUserid(UUID userid);

    Future<Void> delete(UUID userid);

    Future<Void> update(User user);
}