package com.itsaur.internship.post.query;

import com.itsaur.internship.comment.query.CommentQueryModel;
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

public class PostgresPostQueryModelStore implements PostQueryModelStore{

    private PgPool pool;

    public PostgresPostQueryModelStore(PgPool pool) {
        this.pool = pool;
    }

    @Override
    public Future<List<PostQueryModel>> findPostPageByUid(UUID uid){
        return pool
                .preparedQuery(
                "SELECT P.postid, P.createdate, P.filename, P.description, P.userid, U.username " +
                    "FROM posts AS P, users AS U " +
                    "WHERE U.userid = P.userid AND P.userid=($1)" +
                    "ORDER BY (createdate) DESC ")
                .execute(Tuple.of(String.valueOf(uid)))
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(rows -> {

                    List<PostQueryModel> listofposts = new ArrayList<>();
                    if (rows.iterator().hasNext()){
                        for (Row row : rows){
                            String postid                 = String.valueOf(row.getUUID(0));
                            String createdate             = String.valueOf(row.getOffsetDateTime(1));
                            String filename               = row.getString(2);
                            String description            = row.getString(3);
                            String userid                 = String.valueOf(row.getUUID(4));
                            String username               = row.getString("username");

                            listofposts.add(
                                    new PostQueryModel(
                                            postid,
                                            createdate,
                                            filename,
                                            description,
                                            userid,
                                            username
                                    )
                            );
                        }
                        return Future.succeededFuture(listofposts);
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is no post!"));
                    }

                }).onFailure(e ->{
                    e.printStackTrace();
                });
    }

    @Override
    public Future<String> countAllPostsbyUid(UUID uid) {
        return pool
                .preparedQuery("SELECT count(filename) " +
                        "FROM posts " +
                        "WHERE userid=($1)")
                .execute(Tuple.of(String.valueOf(uid)))
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(rows ->{
                    if(rows.iterator().hasNext()){
                        return Future.succeededFuture(String.valueOf(rows.iterator().next().getLong("count")));
                    }else{
                        return Future.failedFuture(new IllegalArgumentException("There is no posts!"));
                    }
                });
    }

    @Override
    public Future<PostQueryModel> findById(UUID postId) {

        return pool
                .preparedQuery(
                "SELECT P.postid, P.createdate, P.filename, P.description, P.userid, U.username  " +
                    "FROM posts AS P, users AS U " +
                    "WHERE U.userid = P.userid AND P.postid=($1)")
                .execute(Tuple.of(String.valueOf(postId)))
                .onFailure(err ->{
                    err.printStackTrace();
                })
                .compose(rows -> {

                    if (rows.iterator().hasNext()){

                        Row row = rows.iterator().next();

                        String postid                 = String.valueOf(row.getUUID("postid"));
                        String createdate             = String.valueOf(row.getOffsetDateTime("createdate"));
                        String filename               = row.getString("filename");
                        String description            = row.getString("description");
                        String userid                 = String.valueOf(row.getUUID("userid"));
                        String username               = row.getString("username");

                        PostQueryModel postQueryModel = new PostQueryModel(
                                postid,
                                createdate,
                                filename,
                                description,
                                userid,
                                username
                        );

                        System.out.println(postQueryModel);
                        return Future.succeededFuture(postQueryModel);

                    }else {

                        return Future.failedFuture(new IllegalArgumentException("There is no post!"));
                    }
                }).onFailure(err -> {
                    err.printStackTrace();
                });
    }
    @Override
    public Future<List<PostQueryModel>> findPostPageByUid(UUID uid, int startFrom, int size){
        return pool
                .preparedQuery(
                    "SELECT P.postid, P.createdate, P.filename, P.description, P.userid, U.username  " +
                        "FROM posts AS P, users AS U " +
                        "WHERE U.userid = P.userid AND P.userid=($1)" +
                        "ORDER BY (createdate) DESC " +
                        "OFFSET ($2) ROWS FETCH FIRST ($3) ROWS ONLY")
                .execute(Tuple.of(
                        String.valueOf(uid),
                        startFrom,
                        size
                ))
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(rows -> {

                    List<PostQueryModel> listofposts = new ArrayList<>();
                    if (rows.iterator().hasNext()){
                        for (Row row : rows){

                            String postid                 = String.valueOf(row.getUUID(0));
                            String createdate     = String.valueOf(row.getOffsetDateTime(1));
                            String filename               = row.getString(2);
                            String description            = row.getString(3);
                            String userid                 = String.valueOf(row.getUUID(4));
                            String username               = row.getString("username");

                            listofposts.add(
                                    new PostQueryModel(
                                            postid,
                                            createdate,
                                            filename,
                                            description,
                                            userid,
                                            username
                                    )
                            );
                        }
                        return Future.succeededFuture(listofposts);
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is no post!"));
                    }

                }).onFailure(e ->{
                    e.printStackTrace();
                });
    }



}
