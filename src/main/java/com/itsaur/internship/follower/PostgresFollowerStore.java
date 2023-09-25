package com.itsaur.internship.follower;

import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.UUID;

public class PostgresFollowerStore implements FollowerStore{
    final private PgPool pool;

    public PostgresFollowerStore(PgPool pool) {
        this.pool = pool;
    }

    @Override
    public Future<Void> insert(Follower follower) {
        return pool
                .preparedQuery("INSERT INTO followers(followid, userid, createdate, followerid)" +
                                   "VALUES ($1 , $2 , $3 , $4)")
                .execute(Tuple.of(
                        follower.followid(),
                        follower.userid(),
                        follower.createdate(),
                        follower.followerid()
                ))
                .onFailure(err -> {
                    System.out.println("hxame");
                    err.printStackTrace();
                }).mapEmpty();
    }

    @Override
    public Future<Void> delete(Follower follower) {
        return pool
                .preparedQuery("DELETE FROM followers WHERE followid=($1)")
                .execute(Tuple.of(follower.followid()))
                .onFailure(e -> {
                    e.printStackTrace();
                }).mapEmpty();
    }

    @Override
    public Future<Void> deleteAll(UUID userid) {
        return pool
                .preparedQuery("DELETE FROM followers WHERE userid=($1)")
                .execute(Tuple.of(userid))
                .onFailure(e -> {
                    e.printStackTrace();
                }).onFailure(err -> {
                    err.printStackTrace();
                }).mapEmpty();
    }

    @Override
    public Future<Follower> findByUseridFollowerid(UUID userid, UUID followerid){
        return pool
                .preparedQuery(
                    "SELECT followid, userid, createdate, followerid " +
                        "FROM followers " +
                        "WHERE userid = ($1) AND followerid = ($2)")
                .execute(Tuple.of(userid,followerid))
                .compose(rows -> {
                    System.out.println(rows.iterator().next().getUUID(0));
                    if (rows.iterator().hasNext()){
                        Row row = rows.iterator().next();
                        return Future.succeededFuture(new Follower(
                                row.getUUID(0),
                                row.getUUID(1),
                                row.getOffsetDateTime(2),
                                row.getUUID(3)
                        ));
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is no follow relation"));
                    }
                });
    }




}
