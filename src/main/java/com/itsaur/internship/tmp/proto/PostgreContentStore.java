package com.itsaur.internship.tmp.proto;

import com.itsaur.internship.tmp.PostStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.UUID;

public class PostgreContentStore {

    private final Vertx vertx;
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions = new PoolOptions()
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
    public Future<Void> insertComment(String filename, String comment) {


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
                        return client.close().compose(w -> {
                            return Future.succeededFuture();
                        });
                    } else {
                        return client.close().compose(w -> {
                            return Future.failedFuture(new IllegalArgumentException());
                        });
                    }
                });
    }

    @Override
    public Future<Void> findImage(String filename) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT imageid FROM images WHERE image=($1)")
                .execute(Tuple.of(filename))
                .onFailure(e -> {
                    System.out.println(e);
                })
                .compose(res2 -> {
                    if (res2.iterator().hasNext()) {
                        return client.close().compose(q -> {
                            return Future.succeededFuture();
                        });

                    } else {
                        return client.close().compose(q -> {
                            return Future.failedFuture(new IllegalArgumentException());
                        });

                    }
                });
    }
    @Override
    public Future<Void> deleteComment(String commentid) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("DELETE FROM comments WHERE commentid=($1)")
                .execute(Tuple.of(commentid))
                .compose(q -> {
                    return client.close();
                });

    }
}