package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.util.UUID;

public class PostgresUsersStore implements UsersStore{

    private Vertx vertx;
    private PgConnectOptions connectOptions ;
    private PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    public PostgresUsersStore(Vertx vertx, PostgresOptions postgresOptions) {
        this.vertx = vertx;
        this.connectOptions = postgresOptions.getPgConnectOptions();
    }


    @Override
    public Future<Void> insert(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return this.findUser(user.getUsername())
                .recover(q -> {
                    return client
                            .preparedQuery("INSERT INTO users (personid, username,password) VALUES ($1, $2, $3)")
                            .execute(Tuple.of(UUID.randomUUID(), user.getUsername(), user.getPassword()))
                            .compose(w -> {
                                client.close();
                                return Future.succeededFuture();
                            });
                }).mapEmpty();

    }



    @Override
    public Future<User> findUser(String username) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT username,password FROM users WHERE username=($1)")
                .execute(Tuple.of(username))
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(res2 ->{
                    if(res2.iterator().hasNext()){
                        return Future.succeededFuture(new User(res2.iterator().next().getString("username"), res2.iterator().next().getString("password")));
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
                    client.close();
                    return Future.succeededFuture();
                });
    }

    @Override
    public Future<Void> changePassword(String username, String newPassword) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);

        return client
                .preparedQuery("UPDATE users SET password=($2) WHERE username=($1)")
                .execute(Tuple.of(username,newPassword))
                .compose(res2 ->{
                    client.close();
                    return Future.succeededFuture();
        });


    }

}
