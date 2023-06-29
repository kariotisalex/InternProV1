package com.itsaur.internship.post;

import com.itsaur.internship.comment.CommentStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresPostStore implements PostStore{

    private final Vertx vertx;
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);
    private CommentStore commentStore;

    public PostgresPostStore(Vertx vertx, PgConnectOptions connectOptions, CommentStore commentStore) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
        this.commentStore = commentStore;
    }

    @Override
    public Future<Void> insert(Post post) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return Future.succeededFuture()
                .compose(q -> {
                    return client
                            .preparedQuery("INSERT INTO posts(postid, createdate, filename, description, userid)\n" +
                                    "SELECT ($1) , ($2) , ($3), ($4), userid FROM users WHERE userid=($5)")
                            .execute(Tuple.of(UUID.randomUUID(), LocalDateTime.now(), post.getFilename(), post.getDescription(), post.getUserid()))
                            .onFailure(e -> {
                                e.printStackTrace();
                            })
                            .compose(w -> {
                                return client.close();
                            });
                });
    }

    @Override
    public Future<Post> findPostByFilename(String filename) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT postid, createdate, updatedate," +
                                          "description, personid " +
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
                        final Post post = new Post(postid, createdate,updatedate, filename, description, userid);

                        client.close();
                        return Future.succeededFuture(post);
                    }else {
                        client.close();
                        return Future.failedFuture(new NullPointerException());
                    }
                });
    }

    @Override
    public Future<Void> updatePost(Post post) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("UPDATE posts SET updatedate=($2), description=($3)  WHERE postid=($1)")
                .execute(Tuple.of(post.getPostid(), LocalDateTime.now(), post.getDescription()))
                .compose(q -> {
                    client.close();
                    return Future.succeededFuture();
                });
    }

    @Override
    public Future<Void> deleteByFilename(String filename){
        SqlClient client = PgPool.client(vertx, connectOptions,poolOptions);
        return client
                .preparedQuery("DELETE FROM posts WHERE filename=($1)")
                .execute(Tuple.of(filename))
                .compose(w -> {
                    return client
                            .close()
                            .compose(r-> {
                                return vertx.fileSystem().delete(String.valueOf(
                                        Paths.get("images",filename).toAbsolutePath()));
                            });
                });
    }

    @Override
    public Future<List<String>> retrieveAll(UUID user){
//        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
//        return client
//                .preparedQuery("SELECT image FROM images WHERE personid=($1)\n" +
//                                   "WHERE personid=($1)")
//                .execute(Tuple.of(user.getPersonid()))
//                .compose(rows -> {
//                    List<String> posts = new ArrayList<>();
//                    if (rows.iterator().hasNext()){
//                        for (Row row : rows)
//                            posts.add(row.getString("image"));
//                    }
//                    client.close();
//                    return Future.succeededFuture(posts);
//                });
        List<String> a = new ArrayList<String>();
        return Future.succeededFuture(a);
    }



}
