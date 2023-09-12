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


    private PgPool pool;

    public PostgresUsersStore(PgPool pool) {
        this.pool = pool;
    }


    @Override
    public Future<Void> insert(User user) {

        return pool
                    .preparedQuery("INSERT INTO users (userid, createdate, username, password) " +
                                       "VALUES ($1, $2, $3, $4)")
                    .execute(Tuple.of(user.userid(), user.createdate(),
                                      user.username(), user.password()))
                    .mapEmpty();

    }



    @Override
    public Future<User> findUserByUsername(String username) {

        return pool
                .preparedQuery(
                    "SELECT userid, createdate, updatedate, username, password " +
                        "FROM users " +
                        "WHERE username=($1)")
                .execute(Tuple.of(username))
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(res2 ->{
                    if(res2.iterator().hasNext()){
                        Row row = res2.iterator().next();

                        return Future.succeededFuture(
                                new User(row.getUUID(0),
                                        row.getOffsetDateTime(1),
                                        row.getOffsetDateTime(2),
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
    public Future<User> findUserByUserid(UUID userid) {

        return pool
                .preparedQuery(
                    "SELECT userid, createdate, updatedate, username, password " +
                        "FROM users " +
                        "WHERE userid=($1)")
                .execute(Tuple.of(userid))
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(res3 ->{
                    if(res3.iterator().hasNext()){
                        Row row = res3.iterator().next();

                        return Future.succeededFuture(
                                new User(row.getUUID(0),
                                        row.getOffsetDateTime(1),
                                        row.getOffsetDateTime(2),
                                        row.getString(3),
                                        row.getString(4)
                                )
                        );
                    }else {

                        return Future.failedFuture(new IllegalArgumentException("User doesn't exists!"));
                    }
                });
    }

    @Override
    public Future<Void> delete(UUID userid) {

        return pool
                .preparedQuery("DELETE FROM users WHERE userid=($1)")
                .execute(Tuple.of(userid))
                .mapEmpty();
    }

    @Override
    public Future<Void> update(User user) {

        return pool
                .preparedQuery(
                    "UPDATE users " +
                        "SET createdate=($2) , updatedate = ($3), username= ($4),password=($5) " +
                        "WHERE userid=($1)")
                .execute(Tuple.of(
                        user.userid(),
                        user.createdate(),
                        user.updatedate(),
                        user.username(),
                        user.password()
                )).mapEmpty();


    }

}
