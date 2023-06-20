package com.itsaur.internship;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import io.vertx.core.Vertx;

public class Application {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        PostgresOptions postgresOptions = new PostgresOptions();

        try{
            JCommander.newBuilder()
                    .addObject(postgresOptions)
                    .build()
                    .parse(args);
        }catch (ParameterException e){
            System.out.println(e.getMessage());
        }

        final UserService service = new UserService(
                new PostgresUsersStore(vertx, postgresOptions)
        );

        if (postgresOptions.getService().equals("serverdb")){
            vertx.deployVerticle(new UserVerticle(new UserService(
                    new PostgresUsersStore(vertx, postgresOptions))));

        }else if (postgresOptions.getService().equals("serverl ocal")){
            vertx.deployVerticle(new UserVerticle(new UserService(
                    new UsersInBinary(vertx))));

        }else if (postgresOptions.getService().equals("console")) {
            new UserConsole(service).executeCommand(args)
                    .onComplete(v -> System.exit(0));
        } else {
            System.out.println("Something went wrong!");
        }




    }
}
