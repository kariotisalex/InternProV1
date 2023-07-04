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
                    .preparedQuery("INSERT INTO users (userid, createdate, username, password) " +
                                       "VALUES ($1, $2, $3, $4)")
                    .execute(Tuple.of(user.getUserid(), user.getCreatedate(),
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
                .preparedQuery("SELECT userid, createdate, updatedate, username, password FROM users WHERE username=($1)")
                .execute(Tuple.of(username))
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(res2 ->{
                    if(res2.iterator().hasNext()){
                        Row row = res2.iterator().next();
                        return Future.succeededFuture(
                                new User(row.getUUID(0),
                                        row.getLocalDateTime(1),
                                        row.getLocalDateTime(2),
                                        row.getString(3),
                                        row.getString(4)
                                )
                        );
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
