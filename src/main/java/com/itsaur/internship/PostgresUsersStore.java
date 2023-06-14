package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

public class PostgresUsersStore implements UsersStore{

    PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(5432)
            .setHost("the-host")
            .setDatabase("the-db")
            .setUser("admin")
            .setPassword("password");

    PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    SqlClient client = PgPool.client(connectOptions, poolOptions);



    @Override
    public Future<Void> insert(User user) {
        return null;
    }

    @Override
    public Future<User> findUser(String username) {
        return null;
    }

    @Override
    public Future<Void> delete(String username) {
        return null;
    }

    @Override
    public Future<Void> changePassword(String username, String currentPassword, String newPassword) {
        return null;
    }

}
