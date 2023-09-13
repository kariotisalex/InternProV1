package com.itsaur.internship.adminService.postgreSQL;

import com.itsaur.internship.PostgresOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

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
        SqlClient client = PgPool.client(vertx,postgresOptions.getPgConnectOptions(),poolOptions);

        insertRandomUsers(client, 10);



    }
    public Future<Void> addUsers(int records){
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);

        return client
                .query("SELECT EXISTS( SELECT FROM users)")
                .execute()
                .onSuccess(a -> {
                    System.out.println(a + " show LoVe");
                })
                .compose(res -> {
                    return insertRandomUsers(client, records);
                });
    }


    private static Future<Void> insertRandomUsers(SqlClient client, int records){
        return client
                .preparedQuery("INSERT INTO users (userid, createdate, username, password) " +
                        "VALUES ($1, $2, $3, $4)")
                .executeBatch(tupleFiller(records))
                .compose(q -> {
                    return client.close();
                });
    }
    private static List<Tuple> tupleFiller(int records){

        List<Tuple> batch = new ArrayList<>();
        IntStream.range(0, records).forEach(e ->{
            batch.add(Tuple.of(UUID.randomUUID(), OffsetDateTime.now(), generateRandom(1), generateRandom(0)));
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
