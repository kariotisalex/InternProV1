package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.UUID;

public class PostgreContentStore implements ContentStore{

    private Vertx vertx;
    private PgConnectOptions connectOptions;
    private PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    public PostgreContentStore(Vertx vertx, PgConnectOptions connectOptions) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
    }

    public static void main(String[] args) {
        try {
            PgConnectOptions connectOptions = new PgConnectOptions()
                    .setPort(5432)
                    .setHost("localhost")
                    .setDatabase("postgres")
                    .setUser("postgres")
                    .setPassword("password");
            PostgreContentStore postgreContentStore = new PostgreContentStore(Vertx.vertx(),
                    connectOptions);

            postgreContentStore.insertImage("yfwzdxhahvfhiptljgc","testImage.png","This photo is awesome");
            postgreContentStore.insertComment("testImage.png", "The photo's first comment");
        }catch (Exception e){
            e.printStackTrace();
        }



    }
    @Override
    public Future<Void> insertImage(String username, String filename, String description) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return Future.succeededFuture()
                .compose(q -> {
                    return client
                            .preparedQuery("INSERT INTO images(imageid, date, image, description, personid)\n" +
                                    "SELECT ($1) , now() , ($2), ($3), personid FROM users WHERE username =($4)")
                            .execute(Tuple.of(UUID.randomUUID(), filename, description, username))
                            .onFailure(e -> {
                                e.printStackTrace();
                            })
                            .compose(w -> {
                                return client.close();
                            });
                });
    }



    @Override
    public Future<Void> insertComment(String filename, String comment) {
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);
        return Future.succeededFuture()
                .compose(q -> {
                    return client
                            .preparedQuery("INSERT INTO comments(commentid, date, comment, imageid)" +
                                    "SELECT ($1), now(), ($2), imageid FROM images WHERE image=($3)")
                            .execute(Tuple.of(UUID.randomUUID(), comment, filename))
                            .onFailure(e -> {
                                System.out.println(e);
                                e.printStackTrace();
                            })
                            .compose(r -> {
                                return client.close();
                            });
                });
    }

    @Override
    public Future<Void> findUser(String username) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT personid, username,password FROM users WHERE username=($1)")
                .execute(Tuple.of(username))
                .onFailure(e -> {
                    System.out.println(e);
                })
                .compose(res2 -> {
                    if (res2.iterator().hasNext()) {
                        return Future.succeededFuture();
                    } else {
                        return Future.failedFuture(new IllegalArgumentException());
                    }
                });
    }
}