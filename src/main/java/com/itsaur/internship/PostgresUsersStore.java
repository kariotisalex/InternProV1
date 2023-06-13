package com.itsaur.internship;

import io.vertx.core.Future;

public class PostgresUsersStore implements UsersStore{
    @Override
    public Future<Void> insert(User user) {
        return null;
    }

    @Override
    public Future<User> findUser(String username) {
        return null;
    }

    @Override
    public Future<Void> delete(String username) {
        return null;
    }

    @Override
    public Future<Void> changePassword(String username, String currentPassword, String newPassword) {
        return null;
    }
}
