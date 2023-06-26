package com.itsaur.internship;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.itsaur.internship.content.ContentService;
import com.itsaur.internship.tmp.proto.PostgreContentStore;
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
                new PostgresUsersStore(vertx, postgresOptions.getPgConnectOptions())
        );

        if (postgresOptions.getService().equals("serverdb")){
            vertx.deployVerticle(new VerticleApi(
                    new UserService(
                            new PostgresUsersStore(vertx, postgresOptions.getPgConnectOptions())),
                    new ContentService(
                            new PostgreContentStore(vertx, postgresOptions.getPgConnectOptions()))));

        }else if (postgresOptions.getService().equals("serverlocal")){
            vertx.deployVerticle(new VerticleApi(new UserService(
                    new UsersInBinaryStore(vertx))));

        }else if (postgresOptions.getService().equals("console")) {
            new UserConsole(service).executeCommand(args)
                    .onComplete(v -> System.exit(0));
        } else {
            System.out.println("Something went wrong!");
        }




    }
}
