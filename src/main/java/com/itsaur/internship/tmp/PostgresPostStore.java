package com.itsaur.internship.tmp;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresPostStore implements PostStore{

    private final Vertx vertx;
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    public PostgresPostStore(Vertx vertx, PgConnectOptions connectOptions) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
    }




    public Future<Void> insert(String username, String filename, String description) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return Future.succeededFuture()
                .compose(q -> {
                    return client
                            .preparedQuery("INSERT INTO images(imageid, date, image, description, personid)\n" +
                                    "SELECT ($1) , now($2) , ($3), ($4), personid FROM users WHERE username =($5)")
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
    public Future<Void> find(String filename) {
        return null;
    }

    @Override
    public Future<Void> update(String description) {
        return null;
    }

    public Future<Void> delete(String filename){
        SqlClient client = PgPool.client(vertx, connectOptions,poolOptions);
        return client
                .preparedQuery("DELETE FROM images WHERE image=($1)")
                .execute(Tuple.of(filename))
                .compose(w -> {
                    return client
                            .close()
                            .compose(r-> {
                                return vertx.fileSystem().delete(String.valueOf(Paths.get("src/main/java/com/itsaur/internship/images",filename).toAbsolutePath()));
                            });
                });
    }

    @Override
    public Future<List<String>> retrieveAll(UUID userid){
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT image FROM images I INNER JOIN users U ON U.personid=I.personid\n" +
                        "                                   WHERE u.username=($1)")
                .execute(Tuple.of(userid))
                .compose(q->{
                    List<String> posts = new ArrayList<>();
                    if (q.iterator().hasNext()){
                        for (Row row: q)
                            posts.add(row.getString("image"));
                    }
                    return Future.succeededFuture(posts);

                });
    }



}
