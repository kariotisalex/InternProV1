package com.itsaur.internship.adminService.postgreSQL;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;


public class CreateUserInPostgreSQL {
    Vertx vertx = Vertx.vertx();
    PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(5432)
            .setHost("localhost")
            .setDatabase("postgres")
            .setUser("postgres")
            .setPassword("password");
    PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    public static void main(String[] args) {
        args = new String[]{"a"};

        new CreateUserInPostgreSQL().generateRecords(10);
        //new CreateUserInPostgreSQL().addUsers(10);

    }
    public Future<Void> addUsers(int records){
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);

        return client
                .query("SELECT EXISTS( SELECT FROM users)")
                .execute()
                .compose(res -> {
                    return insertRandomUsers(client, records);
                });
    }

    public void generateRecords(int records){
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);

        client
                .query("SELECT EXISTS( SELECT FROM users)")
                .execute()
                .onComplete(res ->{
                    if (res.succeeded()){
                        RowSet<Row> rows = res.result();
                        System.out.println(rows.iterator().next().getBoolean(0));
                    client.close();
                }else {
                    generateUsers(records);
                }
                });
    }
    private Future<Void> generateUsers(int records){
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);

        return client
                .query("CREATE TABLE users (\n" +
                        "    personid uuid primary key not null,\n" +
                        "    username varchar(255),\n" +
                        "    password varchar(255)\n" +
                        "); ")
                .execute()
                .compose(eq -> {
                    addUsers(records);
                    return client.close();
                });
    }

    private static Future<Void> insertRandomUsers(SqlClient client, int records){
        return client
                .preparedQuery("INSERT INTO users (personid, username,password) VALUES ($1, $2, $3)")
                .executeBatch(tupleFiller(records))
                .mapEmpty();
    }
    private static List<Tuple> tupleFiller(int records){

        List<Tuple> batch = new ArrayList<>();
        IntStream.range(0, records).forEach(e ->{
            batch.add(Tuple.of(UUID.randomUUID(), generateRandom(1), generateRandom(0)));
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
