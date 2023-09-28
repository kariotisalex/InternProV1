package com.itsaur.internship.adminService.postgreSQL;

import com.itsaur.internship.PostgresOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import net.datafaker.Faker;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreatePost {
    Vertx vertx;
    PgConnectOptions connectOptions;
    PoolOptions poolOptions;

    public CreatePost(Vertx vertx, PgConnectOptions connectOptions, PoolOptions poolOptions) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
        this.poolOptions = poolOptions;
        vertx.deployVerticle("com/itsaur/internship/adminService/postgreSQL",new DeploymentOptions().setWorker(true));

    }
    public static void main(String[] args) {

        CreatePost post = new CreatePost(
                Vertx.vertx(),
                new PostgresOptions().getPgConnectOptions(),
                new PoolOptions().setMaxSize(5)
        );

        PgPool pool = PgPool.pool(
                post.vertx,
                post.connectOptions,
                post.poolOptions
        );
//        insertRandomPostsPerUser(pool,UUID.fromString("12cfaf83-6928-4bd6-9886-a17b77c5e626"),500);
//        insertRandomPostsPerUser(pool,uuid,10000);
        findAllUserid(pool)
                .compose(listOfUuids -> {
                    System.out.println(listOfUuids.size());

                    return handling(pool,listOfUuids,0);

                });



    }

    private static Future<Void> handling(PgPool pool, List<UUID> uuids, int position){
        return insertRandomPostsPerUser(pool,uuids.get(position),10000)
                .compose(res -> {
                    if(position +1 < uuids.size()){

                        return handling(pool,uuids,position+1);
                    }
                    return Future.succeededFuture();
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
    private static Future<Void> insertRandomPostsPerUser(PgPool pool, UUID uuids, int records){

            return pool
                    .preparedQuery("INSERT INTO posts(postid, createdate, filename, description, userid) " +
                            "VALUES ($1 , $2 , $3 , $4, $5)")
                    .executeBatch(tuplePosts(uuids,records))
                    .onFailure(event -> event.printStackTrace())
                    .compose(w -> {
                        System.out.println("a");
                       return Future.succeededFuture();
                    });
    }

    private static List<Tuple> tuplePosts(UUID res, int records){
        Faker faker = new Faker();
        List<Tuple> batch = new ArrayList<>();
        IntStream.range(0, records).forEach(er -> {
            batch.add(Tuple.of(
                    UUID.randomUUID(),
                    OffsetDateTime.now(),
                    "477985f6-86e8-4325-b393-e10269448861.png",
                    faker.pokemon().name(),
                    res));
        });
        return batch;
    }












}
