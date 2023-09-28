package com.itsaur.internship.adminService.postgreSQL;

import com.beust.ah.A;
import com.itsaur.internship.PostgresOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;
import net.datafaker.Faker;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;


public class CreateUserInPostgreSQL {
    Vertx vertx;
    PgConnectOptions connectOptions;
    PoolOptions poolOptions;

    public CreateUserInPostgreSQL(Vertx vertx, PgConnectOptions connectOptions, PoolOptions poolOptions) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
        this.poolOptions = poolOptions;
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        PostgresOptions postgresOptions = new PostgresOptions();
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        PgPool pool = PgPool.pool(vertx,postgresOptions.getPgConnectOptions(),poolOptions);

        // insertRandomUsers(pool, 90000);

        findAllUserid(pool)
                .onSuccess(u -> {
                    insertRandomPostsPerUser(pool, u, 1000);
                });



    }
    private static void insertRandomPostsPerUser(PgPool pool, List<UUID> uuids, int records){
        uuids.forEach(res -> {
            pool
                .preparedQuery("INSERT INTO posts(postid, createdate, filename, description, userid) " +
                        "VALUES ($1 , $2 , $3 , $4, $5)")
                .executeBatch(tuplePosts(res,1000));
        });


    }

    private static List<Tuple> tuplePosts(UUID res, int records){
        Faker faker = new Faker();
        List<Tuple> batch = new ArrayList<>();
        IntStream.range(0, records).forEach(er -> {
            batch.add(Tuple.of(UUID.randomUUID(), OffsetDateTime.now(), "477985f6-86e8-4325-b393-e10269448861.png",faker.pokemon(), res));
        });
        return batch;
    }
    private static Future <List<UUID>> findAllUserid(PgPool pool){
        return pool
                .preparedQuery("SELECT userid FROM users " +
                        " OFFSET 0 ROWS FETCH FIRST 5000 ROWS ONLY ")
                .execute()
                .compose(s -> {
                    List<UUID> uid = new ArrayList<>();
                    for(Row row : s){
                        uid.add(row.getUUID(0));
                    }
                    return Future.succeededFuture(uid);
                });

    }




    private static Future<Void> insertRandomUsers(SqlClient client, int records){
        return client
                .preparedQuery("INSERT INTO users (userid, createdate, username, password) " +
                        "VALUES ($1, $2, $3, $4)")
                .executeBatch(tupleFiller(records))
                .mapEmpty();
    }
    private static List<Tuple> tupleFiller(int records){

        List<Tuple> batch = new ArrayList<>();
        Faker faker = new Faker();
        IntStream.range(0, records).forEach(e ->{
            batch.add(Tuple.of(UUID.randomUUID(), OffsetDateTime.now(), faker.name().username(), faker.internet().password()));
        });
        return batch;
    }

    private static String generateRandom(int choice) {
        String characters;
        if(choice == 1){
            characters = "abcdefghijklmnopqrstuvwxyz";
        }else{
            characters = "0123456789";
        }
        Random random = new Random();

        int size = random.nextInt(10, 20);
        StringBuilder builder = new StringBuilder();

        IntStream.range(0, size)
                .forEach(i -> {
                    int character = random.nextInt(0, characters.length());
                    builder.append(characters.charAt(character));
                });

        return builder.toString();
    }



}
