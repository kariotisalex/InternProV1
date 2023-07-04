package com.itsaur.internship.user;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class PostgresUsersStore implements UsersStore {

    private Vertx vertx;
    private PgConnectOptions connectOptions ;
    private PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    public PostgresUsersStore(Vertx vertx, PgConnectOptions postgresOptions) {
        this.vertx = vertx;
        this.connectOptions = postgresOptions;
    }


    @Override
    public Future<Void> insert(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                    .preparedQuery("INSERT INTO users (userid, createdate username,password) " +
                                       "VALUES ($1, $2, $3, $4)")
                    .execute(Tuple.of(UUID.randomUUID(), LocalDateTime.now(),
                                      user.getUsername(), user.getPassword()))
                    .compose(w -> {
                        client.close();
                        return Future.succeededFuture();
                    });

    }



    @Override
    public Future<User> findUserByUsername(String username) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT userid, createdate, username,password FROM users WHERE username=($1)")
                .execute(Tuple.of(username))
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(res2 ->{
                    if(res2.iterator().hasNext()){
                        return Future.succeededFuture(
                                new User(res2.iterator().next().getUUID(0),
                                         res2.iterator().next().getLocalDateTime(1),
                                         res2.iterator().next().getString(2),
                                         res2.iterator().next().getString(3)));
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
