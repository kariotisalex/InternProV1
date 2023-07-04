package com.itsaur.internship;


import com.itsaur.internship.user.UserService;
import io.vertx.core.Future;

import java.util.UUID;

public class UserConsole {

    private final UserService userService;

    public UserConsole(UserService userService) {
        this.userService = userService;
    }

    public Future<Void> executeCommand(String[] args) {
        if (args[1].equals("--register")) {
            return this.userService.register(args[2], args[3])
                    .onSuccess(v -> System.out.println("User registered!"))
                    .onFailure(v -> {
                        System.out.println("Failed to register the user");
                    }).mapEmpty();
        } else if (args[1].equals("--login")) {
            return this.userService.login(args[2], args[3])
                    .onSuccess(v -> System.out.println("User logged in!"))
                    .onFailure(v -> {
                        System.out.println("Failed to log in user");
                        v.printStackTrace();
                    }).mapEmpty();
        } else if (args[1].equals("--delete")) {
            return this.userService.deleteByUserid(args[2])
                    .onSuccess(v -> System.out.println("User deleted!"))
                    .onFailure(v -> {
                        System.out.println("Failed to delete user");
                        v.printStackTrace();
                    }).mapEmpty();
        } else if (args[1].equals("--chpasswd")) {
            return this.userService.changePassword(UUID.fromString(args[2]), args[3],args[4])
                    .onSuccess(v -> System.out.println("User changed password!"))
                    .onFailure(v -> {
                        System.out.println("Failed to change password from user " + args[2]);
                        v.printStackTrace();
                    }).mapEmpty();
        } else {
            return Future.failedFuture(new IllegalArgumentException("Invalid argument"));
        }
    }
}
