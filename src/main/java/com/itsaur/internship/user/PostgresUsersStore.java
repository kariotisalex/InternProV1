package com.itsaur.internship.user;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

public class PostgresUsersStore implements UsersStore {

    private Vertx vertx;
    private PgConnectOptions connectOptions ;
    private PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    public PostgresUsersStore(Vertx vertx, PgConnectOptions postgresOptions, PoolOptions poolOptions) {
        this.vertx = vertx;
        this.connectOptions = postgresOptions;
        this.poolOptions = poolOptions;
    }


    @Override
    public Future<Void> insert(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return this.findUserByUsername(user.getUsername())
                .recover(q -> {
                    return client
                            .preparedQuery("INSERT INTO users (personid, createdate username,password) " +
                                               "VALUES ($1, $2, $3, $4)")
                            .execute(Tuple.of(java.util.UUID.randomUUID(), user.initCreateDate(),
                                              user.getUsername(), user.getPassword()))
                            .compose(w -> {
                                client.close();
                                return Future.succeededFuture();
                            });
                }).mapEmpty();

    }



    @Override
    public Future<User> findUserByUsername(String username) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT personid, createdate, username,password FROM users WHERE username=($1)")
                .execute(Tuple.of(username))
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(res2 ->{
                    if(res2.iterator().hasNext()){
                        return Future.succeededFuture(
                                new User(res2.iterator().next().getUUID("personid"),
                                         res2.iterator().next().getLocalDateTime("createdate"),
                                         res2.iterator().next().getString("username"),
                                         res2.iterator().next().getString("password")));
                    }else {
                        return Future.failedFuture(new IllegalArgumentException());
                    }
                });
    }

    @Override
    public Future<Void> delete(String username) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("DELETE FROM users WHERE username=($1)")
                .execute(Tuple.of(username))
                .compose(res2 ->{
                    return client.close();
                });
    }

    @Override
    public Future<Void> changePassword(String username, String newPassword) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);

        return client
                .preparedQuery("UPDATE users SET password=($2) WHERE username=($1)")
                .execute(Tuple.of(username,newPassword))
                .compose(res2 ->{
                    return client.close();
        });


    }

}
