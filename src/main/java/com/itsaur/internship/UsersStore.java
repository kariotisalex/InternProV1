package com.itsaur.internship;

import io.vertx.core.Future;

public interface UsersStore {

    Future<Void> insert(User user);

    Future<User> findUser(String username);

    Future<Void> delete(String username);

    //todo Remove currentPassword, password is checked by the service
    Future<Void> changePassword(String username, String currentPassword, String newPassword);
}