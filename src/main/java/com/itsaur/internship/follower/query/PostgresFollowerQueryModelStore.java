package com.itsaur.internship.follower.query;

import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class PostgresFollowerQueryModelStore implements FollowerQueryModelStore {

    final private PgPool pool;

    public PostgresFollowerQueryModelStore(PgPool pool) {
        this.pool = pool;
    }


    @Override
    public Future<List<FollowerQueryModel>> followingUsers(UUID followerid) {
        return pool
                .preparedQuery("SELECT F.followid, F.userid, U1.username, F.createdate, F.followerid, U2.username, COUNT(*) OVER () as TotalCount " +
                        "FROM followers AS F, users AS U1, users AS U2 " +
                        "WHERE F.userid = U1.userid AND F.followerid = U2.userid AND F.followerid = ($1)")
                .execute(Tuple.of(followerid))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){
                        List<FollowerQueryModel> followerQueryModels = new ArrayList<>();
                        followerQueryModels.add(
                                new FollowerQueryModel(
                                        null,
                                        null,
                                        String.valueOf(rows.iterator().next().getLong("totalcount")),
                                        null,
                                        null,
                                        null
                                ));

                        for (Row row : rows){
                            followerQueryModels.add(
                                    new FollowerQueryModel(
                                            row.getUUID(0),
                                            row.getUUID(1),
                                            row.getString(2),
                                            row.getOffsetDateTime(3),
                                            row.getUUID(4),
                                            row.getString(5)
                            ));
                        }
                        return Future.succeededFuture(followerQueryModels);
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is no followingUsers"));
                    }
                });
    }

    @Override
    public Future<List<FollowerQueryModel>> followers(UUID userid) {
        return pool
                .preparedQuery("SELECT F.followid, F.userid, U1.username, F.createdate, F.followerid, U2.username, COUNT(*) OVER () as TotalCount  " +
                "FROM followers AS F, users AS U1, users AS U2 " +
                "WHERE F.userid = U1.userid AND F.followerid = U2.userid AND F.userid = ($1)")
                .execute(Tuple.of(userid))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){
                        List<FollowerQueryModel> followerQueryModels = new ArrayList<>();

                        followerQueryModels.add(new FollowerQueryModel(
                                null,
                                null,
                                String.valueOf(rows.iterator().next().getLong("totalcount")),
                                null,
                                null,
                                null
                        ));

                        for (Row row : rows){
                            followerQueryModels.add(new FollowerQueryModel(
                                    row.getUUID(0),
                                    row.getUUID(1),
                                    row.getString(2),
                                    row.getOffsetDateTime(3),
                                    row.getUUID(4),
                                    row.getString(5)
                            ));
                        }
                        return Future.succeededFuture(followerQueryModels);
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is no followers"));
                    }
                });
    }









}
