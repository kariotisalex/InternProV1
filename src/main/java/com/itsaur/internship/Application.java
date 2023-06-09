package com.itsaur.internship;

import io.vertx.core.Vertx;

public class Application {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        final UserService service = new UserService(
                new UsersInBinary(vertx)
        );

        if (args[0].equals("--server")){
            vertx.deployVerticle(new UserVerticle(service));
        } else if (args[0].equals("--console")) {
            new UserConsole(service).executeCommand(args)
                    .onComplete(v -> System.exit(0));
        } else {
            System.out.println("Something went wrong!");
        }

    }
}
