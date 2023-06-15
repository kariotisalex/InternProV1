package com.itsaur.internship.tmp;

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
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);

        client
                .preparedQuery("CREATE TABLE persons")
                .execute()
                .onComplete(ar -> {
                    System.out.println("1. ");
                    if(ar.succeeded()) {
                        System.out.println("2. ");
                        RowSet<Row> rows = ar.result();
                        for (Row row : rows){
                            System.out.println(row.getJson("lastname"));
                            System.out.println("User " + row.getInteger(0) + " " + row.getString(1));

                        }
                        System.out.println(rows.rowCount());
                    }else {
                        System.out.println("Failure : " + ar.cause().getMessage());
                    }
                    client.close();
                });
    }
}
