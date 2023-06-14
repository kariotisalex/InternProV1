package com.itsaur.internship.adminService;

import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;

import java.util.Random;
import java.util.stream.IntStream;


public class InitializePostgreSql {

    public static void main(String[] args) {
        new InitializePostgreSql().generateUsers();
    }

    public void generateUsers(){
        String letters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setHost("localhost")
                .setDatabase("postgres")
                .setUser("postgres")
                .setPassword("password");

// Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

// Create the client pool
        SqlClient client = PgPool.client(connectOptions, poolOptions);
String pt="INSERT INTO persons VALUES ("
        + "9" + ","
        + generateRandom(letters)+ ","
        + generateRandom(letters)+ ","
        + generateRandom(letters)
        + ");";
System.out.println(pt);
// A simple query
        client
                .query(pt)
                .execute()
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        RowSet<Row> result = ar.result();
                        System.out.println("Got " + result.toString() + " rows ");
                    } else {
                        System.out.println("Failure: " + ar.cause().getMessage());
                    }

                    // Now close the pool
                    client.close();
                });
    }


    private  String generateRandom(String characters) {
        Random random = new Random();

        int size = random.nextInt(10, 20);
        StringBuilder builder = new StringBuilder();

        IntStream.range(0, size)
                .forEach(i -> {
                    int character = random.nextInt(0, characters.length());
                    builder.append(characters.charAt(character));
                });

        return builder.toString();
    }
}
