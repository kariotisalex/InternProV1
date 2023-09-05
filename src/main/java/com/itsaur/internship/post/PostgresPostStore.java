package com.itsaur.internship.post;

import com.itsaur.internship.post.Post;
import com.itsaur.internship.post.PostStore;
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

public class PostgresPostStore implements PostStore {

    private final Vertx vertx;
    private final PgConnectOptions connectOptions;

    final PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);


    public PostgresPostStore(Vertx vertx, PgConnectOptions connectOptions) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;

    }

    @Override
    public Future<Void> insert(Post post) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
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
    }



    @Override
    public Future<Post> findPostByPostid(UUID postid) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT postid, createdate, updatedate, filename, description, userid " +
                        "FROM posts " +
                        "WHERE postid=($1)")
                .execute(Tuple.of(postid))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){
                        Row row = rows.iterator().next();

                        LocalDateTime createdate = row.getLocalDateTime(1);
                        LocalDateTime updatedate = row.getLocalDateTime(2);
                        String filename          = row.getString(3);
                        String description       = row.getString(4);
                        UUID userid              = row.getUUID(5);

                        final Post post = new Post(postid, createdate,
                                updatedate, filename, description, userid);

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
                .preparedQuery("UPDATE posts SET createdate=($2), updatedate=($3), filename=($4), description=($5)  WHERE postid=($1)")
                .execute(Tuple.of(post.getPostid(), post.getCreatedDate(), post.getUpdatedDate(),post.getFilename(), post.getDescription()))
                .compose(q -> {
                    return client.close();
                });
    }

    @Override
    public Future<Void> delete(UUID postid){
        SqlClient client = PgPool.client(vertx, connectOptions,poolOptions);
        return client
                .preparedQuery("DELETE FROM posts WHERE postid=($1)")
                .execute(Tuple.of(postid))
                .compose(w -> {
                    return client.close();
                });
    }

    @Override
    public Future<List<Post>> readAllByUserid(UUID userid){
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT postid, createdate, updatedate, filename, description, userid " +
                        "FROM posts " +
                        "WHERE userid=($1)")
                .execute(Tuple.of(userid))
                .compose(rows -> {
                    List<Post> allPostsByUser = new ArrayList<>();
                    if (rows.iterator().hasNext()) {
                        for (Row row : rows) {
                            UUID postid = row.getUUID(0);
                            LocalDateTime createdate = row.getLocalDateTime(1);
                            LocalDateTime updatedate = row.getLocalDateTime(2);
                            String filename = row.getString(3);
                            String description = row.getString(4);
                            allPostsByUser.add(new Post(postid, createdate, updatedate, filename, description, userid));
                        }

                        client.close();
                        return Future.succeededFuture(allPostsByUser);
                    }else {
                        client.close();
                        return Future.failedFuture(new NullPointerException());
                    }
                });
    }


}
