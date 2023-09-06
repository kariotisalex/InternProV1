package com.itsaur.internship.post.query;

import com.itsaur.internship.post.Post;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresPostQueryModelStore implements PostQueryModelStore{

    private final Vertx vertx;
    private final PgConnectOptions connectOptions;

    final PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    public PostgresPostQueryModelStore(Vertx vertx, PgConnectOptions connectOptions) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
    }

    @Override
    public Future<List<PostQueryModel>> findAllByUid(UUID uid){
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT postid, createdate, filename,description,userid  " +
                                  "FROM posts " +
                                  "WHERE userid=($1)" +
                        "ORDER BY (createdate) DESC ")
                .execute(Tuple.of(String.valueOf(uid)))
                .onFailure(e -> {
                    client.close();
                    e.printStackTrace();
                })
                .compose(rows -> {

                    List<PostQueryModel> listofposts = new ArrayList<>();
                    if (rows.iterator().hasNext()){
                        for (Row row : rows){
                            UUID postid                 = row.getUUID(0);
                            String createdate           = String.valueOf(row.getLocalDateTime(1));
                            String filename             = row.getString(2);
                            String description          = row.getString(3);
                            UUID userid                 = row.getUUID(4);
                            listofposts.add(
                                    new PostQueryModel(postid, createdate,
                                            filename,description, userid)
                            );
                        }
                        client.close();
                        return Future.succeededFuture(listofposts);
                    }else {
                        client.close();
                        return Future.failedFuture(new IllegalArgumentException("There is no post!"));
                    }


                }).onFailure(e ->{
                    client.close();
                    e.printStackTrace();
                });
    }
    @Override
    public Future<PostQueryModel> findById(UUID postId) {
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);

        return client
                .preparedQuery("SELECT postid, createdate, filename,description,userid  " +
                        "FROM posts " +
                        "WHERE postid=($1)")
                .execute(Tuple.of(String.valueOf(postId)))
                .onFailure(err ->{
                    client.close();
                    err.printStackTrace();
                })
                .compose(rows -> {

                    if (rows.iterator().hasNext()){

                        Row row = rows.iterator().next();

                        UUID postid = row.getUUID(0);
                        String createdate = String.valueOf(row.getLocalDateTime(1));
                        String filename = row.getString(2);
                        String description = row.getString(3);
                        UUID userid = row.getUUID(4);

                        PostQueryModel postQueryModel = new PostQueryModel(postid, createdate,
                                filename,description, userid);
                        System.out.println(postQueryModel);
                        client.close();
                        return Future.succeededFuture(postQueryModel);

                    }else {

                        client.close();
                        return Future.failedFuture(new IllegalArgumentException("There is no post!"));
                    }
                }).onFailure(err -> {
                    client.close();
                    //err.printStackTrace();
                });
    }

    @Override
    public Future<List<PostQueryModel>> findByUserId(UUID uid) {
        return null;
    }

    @Override
    public Future<Post> findPostByFilename(String filename) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT postid, createdate, updatedate," +
                                    "description, userid " +
                                    "FROM posts " +
                                    "WHERE filename=($1)")
                .execute(Tuple.of(filename))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){

                        UUID postid = rows.iterator().next().getUUID(0);
                        LocalDateTime createdate = rows.iterator().next().getLocalDateTime(1);
                        LocalDateTime updatedate = rows.iterator().next().getLocalDateTime(2);
                        String description = rows.iterator().next().getString(3);
                        UUID userid = rows.iterator().next().getUUID(4);
                        final Post post = new Post(postid, createdate,
                                                   updatedate, filename,
                                                   description, userid);

                        client.close();
                        return Future.succeededFuture(post);
                    }else {
                        client.close();
                        return Future.failedFuture(new NullPointerException());
                    }
                });
    }
}
