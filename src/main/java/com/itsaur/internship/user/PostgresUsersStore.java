package com.itsaur.internship.user;

import com.itsaur.internship.user.User;
import com.itsaur.internship.user.UsersStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.UUID;

public class PostgresUsersStore implements UsersStore {

    private Vertx vertx;
    private PgConnectOptions connectOptions ;
    private PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    PgPool pool;
    public PostgresUsersStore(Vertx vertx, PgConnectOptions postgresOptions) {
        this.vertx = vertx;
        this.connectOptions = postgresOptions;
        this.pool = PgPool.pool(vertx,
                                connectOptions,
                                new PoolOptions()
                                    .setMaxSize(5));
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
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT userid, createdate, updatedate, username, password FROM users WHERE username=($1)")
                .execute(Tuple.of(username))
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(res2 ->{
                    if(res2.iterator().hasNext()){
                        Row row = res2.iterator().next();
                        client.close();
                        return Future.succeededFuture(
                                new User(row.getUUID(0),
                                        row.getLocalDateTime(1),
                                        row.getLocalDateTime(2),
                                        row.getString(3),
                                        row.getString(4)
                                )
                        );
                    }else {
                        client.close();
                        return Future.failedFuture(new IllegalArgumentException());
                    }
                });
    }

    @Override
    public Future<User> findUserByUserid(UUID userid) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT userid, createdate, updatedate, username, password FROM users WHERE userid=($1)")
                .execute(Tuple.of(userid))
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(res3 ->{
                    if(res3.iterator().hasNext()){
                        Row row = res3.iterator().next();
                        client.close();
                        return Future.succeededFuture(
                                new User(row.getUUID(0),
                                        row.getLocalDateTime(1),
                                        row.getLocalDateTime(2),
                                        row.getString(3),
                                        row.getString(4)
                                )
                        );
                    }else {
                        client.close();
                        return Future.failedFuture(new IllegalArgumentException("User doesn't exists!"));
                    }
                });
    }

    @Override
    public Future<Void> delete(UUID userid) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("DELETE FROM users WHERE userid=($1)")
                .execute(Tuple.of(userid))
                .compose(res2 ->{
                    return client.close();
                });
    }

    @Override
    public Future<Void> update(User user) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);

        return client
                .preparedQuery("UPDATE users SET createdate=($2) , updatedate = ($3), username= ($4),password=($5) WHERE userid=($1)")
                .execute(Tuple.of(user.getUserid(), user.getCreatedate(), user.getUpdatedate(),user.getUsername(), user.getPassword()))
                .compose(res2 ->{
                    return client.close();
        });


    }

}
