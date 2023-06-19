package com.itsaur.internship;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import io.vertx.core.Vertx;

public class Application {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        PostgresOptions postgresOptions = new PostgresOptions();

        JCommander.newBuilder()
                .addObject(postgresOptions)
                .build()
                .parse(args);



        final UserService service = new UserService(
                new PostgresUsersStore(vertx, postgresOptions)
        );

        if (postgresOptions.getService().equals("server")){
            vertx.deployVerticle(new UserVerticle(service));
        } else if (postgresOptions.getService().equals("console")) {
            new UserConsole(service).executeCommand(args)
                    .onComplete(v -> System.exit(0));
        } else {
            System.out.println("Something went wrong!");
        }

    }
}
