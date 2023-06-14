package com.itsaur.internship.adminService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

public class PostgreSQL extends AbstractVerticle {


    public void start(){

    }
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

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
        SqlClient client = PgPool.client( vertx, connectOptions , poolOptions);
// A simple query
        client
                .query("SELECT * FROM persons")
                .execute()
                .onFailure(event -> {
                    System.out.println(event.getMessage());
                })
                .onComplete(ar -> {
                    System.out.println(ar.succeeded());
                    if (ar.succeeded()) {
                        RowSet<Row> result = ar.result();
                        System.out.println("Got " + result.value().toString() + " rows ");
                    } else {
                        System.out.println("Failure: " + ar.cause().getMessage());
                        ar.cause().printStackTrace();
                    }

                    // Now close the pool
                    client.close();
                });

    }
}
