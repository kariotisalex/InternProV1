package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;

public class PostgresUsersStore implements UsersStore{

    private Vertx vertx;



    PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(5432)
            .setHost("the-host")
            .setDatabase("the-db")
            .setUser("admin")
            .setPassword("password");

    PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    public PostgresUsersStore(Vertx vertx) {
        this.vertx = vertx;
    }


    @Override
    public Future<Void> insert(User user) {
        SqlClient client = PgPool.client();
        client
                .preparedQuery("INSERT INTO users (personid, username,password) VALUES ($1, $2, $3)")
                .executeBatch(tupleFiller(startPosition, records))
                .onComplete(qwe -> {
                    if (qwe.succeeded()){
                        RowSet<Row> rows = qwe.result();


                    }else {
                        System.out.println("Batch failed " + qwe.cause());
                    }
                });
        return null;
    }

    @Override
    public Future<User> findUser(String username) {
        // SELECT EXISTS
        return null;
    }

    @Override
    public Future<Void> delete(String username) {
        // KALI EROTISI
        return null;
    }

    @Override
    public Future<Void> changePassword(String username, String newPassword) {

        // UPDATE
        return null;
    }

}
