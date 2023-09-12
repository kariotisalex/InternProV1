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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresPostStore implements PostStore {

    private final PgPool pool;


    public PostgresPostStore(PgPool pool) {
        this.pool = pool;

    }

    @Override
    public Future<Void> insert(Post post) {

        return pool
                    .preparedQuery("INSERT INTO posts(postid, createdate, filename, description, userid)\n" +
                            "SELECT ($1) , ($2) , ($3), ($4), userid " +
                            "FROM users WHERE userid=($5)")
                    .execute(Tuple.of(
                            UUID.randomUUID(),
                            OffsetDateTime.now(),
                            post.filename(),
                            post.description(),
                            post.userid())
                    )
                    .onFailure(e -> {
                        e.printStackTrace();
                    })
                .mapEmpty();
    }



    @Override
    public Future<Post> findPostByPostid(UUID postid) {

        return pool
                .preparedQuery("SELECT postid, createdate, updatedate, filename, description, userid " +
                        "FROM posts " +
                        "WHERE postid=($1)")
                .execute(Tuple.of(postid))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){
                        Row row = rows.iterator().next();

                        OffsetDateTime createdate = row.getOffsetDateTime(1);
                        OffsetDateTime updatedate = row.getOffsetDateTime(2);
                        String filename          = row.getString(3);
                        String description       = row.getString(4);
                        UUID userid              = row.getUUID(5);

                        return Future.succeededFuture(
                                new Post(
                                        postid,
                                        createdate,
                                        updatedate,
                                        filename,
                                        description,
                                        userid)
                        );
                    }else {
                        return Future.failedFuture(new NullPointerException());
                    }
                });
    }

    @Override
    public Future<Void> updatePost(Post post) {

        return pool
                .preparedQuery("UPDATE posts SET createdate=($2), updatedate=($3), filename=($4), description=($5)  " +
                        "WHERE postid=($1)")
                .execute(Tuple.of(
                        post.postid(),
                        post.createdDate(),
                        post.updatedDate(),
                        post.filename(),
                        post.description()
                        )
                ).mapEmpty();
    }

    @Override
    public Future<Void> delete(UUID postid){
        return pool
                .preparedQuery("DELETE FROM posts WHERE postid=($1)")
                .execute(Tuple.of(postid))
                .mapEmpty();
    }

    @Override
    public Future<List<Post>> readAllByUserid(UUID userid){

        return pool
                .preparedQuery("SELECT postid, createdate, updatedate, filename, description, userid " +
                        "FROM posts " +
                        "WHERE userid=($1)")
                .execute(Tuple.of(userid))
                .compose(rows -> {
                    List<Post> allPostsByUser = new ArrayList<>();
                    if (rows.iterator().hasNext()) {
                        for (Row row : rows) {
                            UUID postid = row.getUUID(0);
                            OffsetDateTime createdate = row.getOffsetDateTime(1);
                            OffsetDateTime updatedate = row.getOffsetDateTime(2);
                            String filename = row.getString(3);
                            String description = row.getString(4);
                            allPostsByUser.add(new Post(postid, createdate, updatedate, filename, description, userid));
                        }


                        return Future.succeededFuture(allPostsByUser);
                    }else {

                        return Future.failedFuture(new NullPointerException());
                    }
                });
    }


}
