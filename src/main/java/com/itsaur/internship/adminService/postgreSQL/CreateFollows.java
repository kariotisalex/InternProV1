package com.itsaur.internship.adminService.postgreSQL;

import com.itsaur.internship.PostgresOptions;
import com.itsaur.internship.follower.Follower;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import net.datafaker.Faker;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

public class CreateFollows {

    Vertx vertx;
    PgConnectOptions connectOptions;
    PoolOptions poolOptions;

    public CreateFollows(Vertx vertx, PgConnectOptions connectOptions, PoolOptions poolOptions) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
        this.poolOptions = poolOptions;
    }

    public static void main(String[] args) {
        CreateFollows createFollows = new CreateFollows(
                Vertx.vertx(),
                new PostgresOptions().getPgConnectOptions(),
                new PoolOptions().setMaxSize(5)
        );
        PgPool pool = PgPool.pool(
                createFollows.vertx,
                createFollows.connectOptions,
                createFollows.poolOptions
        );





        findAllUserid(pool)
                .compose(res -> {
                    return handlingFollowers(pool, res,0,res.size());
                });


    }




    private static Future<List<UUID>> findAllUserid(PgPool pool){
        return pool
                .preparedQuery("SELECT userid FROM users "
                )
                .execute()
                .compose(s -> {
                    List<UUID> uid = new ArrayList<>();
                    for(Row row : s){
                        uid.add(row.getUUID(0));
                    }
                    return Future.succeededFuture(uid);
                });

    }

    private static Future<Void> handlingFollowers(PgPool pool, List<UUID> uuids, int position, int positionTwo){
        List<UUID> followerid = new ArrayList<>();
        IntStream.range(1,5).forEach(er -> {
            if(uuids.get(position) != uuids.get( positionTwo - er )){
                followerid.add( uuids.get( positionTwo - er ) );
            }

        });

        return insertFollowers(pool, uuids.get(position), followerid)
                .compose(res -> {
                    if(position + 1 < uuids.size()){
                        if(positionTwo - 5 < 0){
                            return handlingFollowers(pool, uuids,position+1, uuids.size() );
                        }else{
                            return handlingFollowers(pool, uuids,position+1, positionTwo-5 );
                        }
                    }else {
                        return Future.succeededFuture();
                    }
                });
    }

    private static Future<Void> insertFollowers(PgPool pool, UUID uuid, List<UUID> uuids){
        return pool
                .preparedQuery("INSERT INTO followers (followid, userid, createdate, followerid ) " +
                        "VALUES ($1, $2, $3, $4)")
                .executeBatch(tupleFollows( uuid , uuids ))
                .mapEmpty();
    }


    private static List<Tuple> tupleFollows( UUID userid , List<UUID> followerids ){
        List<Tuple> batch = new ArrayList<>();
            followerids.forEach(er -> {
                batch.add(Tuple.of(
                        UUID.randomUUID(),
                        userid,
                        OffsetDateTime.now(),
                        er));
        });
        return batch;
    }
}
