package com.itsaur.internship.user;

import io.vertx.core.Future;

public interface UsersStore {

    Future<Void> insert(User user);

    Future<User> findUserByUsername(String username);

    Future<Void> delete(String username);

    Future<Void> changePassword(String username, String newPassword);
}