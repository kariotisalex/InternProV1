package com.itsaur.internship.user.query;

import com.beust.ah.A;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;

public class PostgresUserQueryModelStore implements UserQueryModelStore{
    private PgPool pool;

    public PostgresUserQueryModelStore(PgPool pool) {
        this.pool = pool;
    }

    @Override
    public Future<List<UserQueryModel>> findAllUsersByUsername(String username) {
//        if ((0 < username.length())){
//            throw new IllegalArgumentException("Unacceptable username");
//        }

        return pool
                .preparedQuery(
                    "SELECT U.userid , U.username " +
                            "FROM users AS U " +
                            "WHERE U.username LIKE ($1)")
                .execute(Tuple.of("%"+username+"%"))
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
}
