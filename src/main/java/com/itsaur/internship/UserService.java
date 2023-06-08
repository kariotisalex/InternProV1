package com.itsaur.internship;

import io.vertx.core.Future;

public class UserService {

    private UsersStore store;

    public UserService(UsersStore store) {
        this.store = store;
    }

    public Future<Void> register(String username, String password) {
        return store.findUser(username)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user == null) {
                        return store.insert(new User(username, password));
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    }
                });
    }

    public Future<User> login(String username, String password) {
        return store.findUser(username)
                .compose(user -> {
                    if (user.matches(password)) {
                        return Future.succeededFuture(user);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("Invalid password"));
                    }
                });
    }
}
