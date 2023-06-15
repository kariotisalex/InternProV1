package com.itsaur.internship.adminService.postgreSQL;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

        //new CreateUserInPostgreSQL().generateRecords(10);
        new CreateUserInPostgreSQL().addUsers(10);

    }


    public void addUsers(int records){
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);

        client
                .query("SELECT EXISTS( SELECT FROM users)")
                .execute()
                .onComplete(res -> {
                    if (res.succeeded()){
                        client
                                .query("SELECT * FROM users")
                                .execute()
                                .onComplete(req -> {
                                    if (req.succeeded()){
                                        insertRandomUsers(client, req.result().size(), records);
                                    }
                                });
                    }

                });
    }
    public void generateRecords(int records){
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);

        client
                .query("SELECT EXISTS( SELECT FROM users)")
                .execute()

                .onComplete(res ->
                { if (res.succeeded()){
                    RowSet<Row> rows = res.result();
                    for (Row row:rows)
                        System.out.println(row.getBoolean(0));



                    client.close();
                }else {
                    generateUsers(0, records);
                }


                });

    }
    private void generateUsers(int start, int records){

        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);

        client

                .query("CREATE TABLE users (\n" +
                        "    personid int primary key not null,\n" +
                        "    username varchar(255),\n" +
                        "    password varchar(255)\n" +
                        "); ")
                        .execute()
                                .onComplete(eq -> {
                                    if(eq.succeeded()){
                                        insertRandomUsers(client,start,records);
                                        client.close();
                                    }else {
                                        System.out.println(eq.cause());
                                    }


                                });






    }

    private static void insertRandomUsers(SqlClient client, int startPosition, int records){
        client
                .preparedQuery("INSERT INTO users (personid, username,password) VALUES ($1, $2, $3)")
                .executeBatch(tupleFiller(startPosition, records))
                .onComplete(qwe -> {
                    if (qwe.succeeded()){
                        RowSet<Row> rows = qwe.result();


                    }else {
                        System.out.println("Batch failed " + qwe.cause());
                    }
                });
    }
    private static List<Tuple> tupleFiller(int start, int records){
        records += start;
        List<Tuple> batch = new ArrayList<>();
        IntStream.range(start,records).forEach(e ->{
            batch.add(Tuple.of(e+1, generateRandom(1), generateRandom(0)));
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
