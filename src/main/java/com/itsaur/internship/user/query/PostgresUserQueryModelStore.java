package com.itsaur.internship.user.query;

import com.beust.ah.A;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;

public class PostgresUserQueryModelStore implements UserQueryModelStore{
    final private PgPool pool;
    final private int MIN_VALUE = 0;
    final private int MAX_VALUE = 30;
    final private int MIN_STARTFROM_VALUE = 0;

    final private  int MAX_STARTFROM_VALUE = 100;

    public PostgresUserQueryModelStore(PgPool pool) {
        this.pool = pool;
    }

    @Override
    public Future<List<UserQueryModel>> findUsersPageByUsername(String username, int startFrom, int size) {
        System.out.println(username.length());
        System.out.println(startFrom);
        System.out.println(size);
        System.out.println(1 > 0);

        if (!(MIN_VALUE < username.length() && username.length() < MAX_VALUE)){
            throw new IllegalArgumentException("Username : The length of username is unacceptable");
        }
        if (!( MIN_VALUE < size && size < MAX_VALUE )){
            throw new IllegalArgumentException("Comments : The size of comments is unacceptable");
        }
        if(startFrom % size != 0 && !(MIN_STARTFROM_VALUE < startFrom && startFrom <  MAX_STARTFROM_VALUE)){
            throw new IllegalArgumentException("Comments : The startFrom is not valid!");
        }

        return pool
                .preparedQuery(
                    "SELECT U.userid , U.username " +
                            "FROM users AS U " +
                            "WHERE U.username LIKE ($1)" +
                            "OFFSET ($2) ROWS FETCH FIRST ($3) ROWS ONLY")
                .execute(Tuple.of("%"+username+"%",
                                    startFrom,
                                    size)
                )
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(rows -> {
                    if (rows.iterator().hasNext()) {
                        List<UserQueryModel> usersQueryModel = new ArrayList<>();
                        for (Row row : rows){

                            usersQueryModel.add(new UserQueryModel(row.getUUID(0),
                                    row.getString(1)));
                        }
                        System.out.println(usersQueryModel);
                        return Future.succeededFuture(usersQueryModel);
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is no user!"));
                    }

    }).onFailure(e ->{
                    e.printStackTrace();
                });
    }

    @Override
    public Future<String> countAllUsersByUsername(String username) {
        if (!(MIN_VALUE < username.length() && username.length() < MAX_VALUE)){
            throw new IllegalArgumentException("Unacceptable username");
        }
        return pool
                .preparedQuery(
                        "SELECT COUNT(U.userid) " +
                                "FROM users AS U " +
                                "WHERE U.username LIKE ($1)")
                .execute(Tuple.of("%"+username+"%"))
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(rows -> {
                    if (rows.iterator().hasNext()) {
                        Row row = rows.iterator().next();
                        return Future.succeededFuture(String.valueOf(row.getLong(0)));
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("0"));
                    }

                }).onFailure(e ->{
                    e.printStackTrace();
                });
    }
}
