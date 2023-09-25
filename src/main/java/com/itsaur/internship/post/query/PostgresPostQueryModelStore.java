package com.itsaur.internship.post.query;

import com.itsaur.internship.comment.query.CommentQueryModel;
import com.itsaur.internship.follower.query.FollowerQueryModel;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PostgresPostQueryModelStore implements PostQueryModelStore{
    final private int MIN_VALUE = 0;
    final private int MAX_VALUE = 30;
    final private int MIN_STARTFROM_VALUE = 0;

    final private  int MAX_STARTFROM_VALUE = 100;
    private PgPool pool;

    public PostgresPostQueryModelStore(PgPool pool) {
        this.pool = pool;
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

                        UUID postid                 = row.getUUID("postid");
                        OffsetDateTime createdate   = row.getOffsetDateTime("createdate");
                        String filename             = row.getString("filename");
                        String description          = row.getString("description");
                        UUID userid                 = row.getUUID("userid");
                        String username             = row.getString("username");

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
        if (!(MIN_VALUE < size && size < MAX_VALUE)){
            throw new IllegalArgumentException("Comments : The size of comments is unacceptable");
        }
        if(!(startFrom % size == 0 && MIN_STARTFROM_VALUE <= startFrom && startFrom < MAX_STARTFROM_VALUE)){
            throw new IllegalArgumentException("Comments : The startFrom is not valid!");
        }
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
                    if (rows.iterator().hasNext()){

                        List<PostQueryModel> queryModelList = new ArrayList<>();

                        for (Row row : rows){

                            UUID postid                 = row.getUUID("postid");
                            OffsetDateTime createdate   = row.getOffsetDateTime("createdate");
                            String filename             = row.getString("filename");
                            String description          = row.getString("description");
                            UUID userid                 = row.getUUID("userid");
                            String username             = row.getString("username");


                            queryModelList.add(
                                    new PostQueryModel(
                                            postid,
                                            createdate,
                                            filename,
                                            description,
                                            userid,
                                            username));
                        };
                        return Future.succeededFuture(queryModelList);
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is no post!"));
                    }

                }).onFailure(e ->{
                    e.printStackTrace();
                });
    }


    @Override
    public Future<List<PostQueryModel>> customizeFeed(UUID userid, int startFrom, int size){
        if (!(MIN_VALUE < size && size < MAX_VALUE)){
            throw new IllegalArgumentException("Comments : The size of comments is unacceptable");
        }
        if(!(startFrom % size == 0 && MIN_STARTFROM_VALUE <= startFrom && startFrom < MAX_STARTFROM_VALUE)){
            throw new IllegalArgumentException("Comments : The startFrom is not valid!");
        }
        return pool
                .preparedQuery(
                        "SELECT P.postid, P.createdate, P.filename, P.description, F.followerid, U.username " +
                                "FROM followers AS F, users AS U, posts AS P " +
                                "WHERE F.followerid = P.userid AND F.followerid = U.userid AND F.userid = ($1)" +
                                "ORDER BY (createdate) DESC " +
                                "OFFSET ($2) ROWS FETCH FIRST ($3) ROWS ONLY")
                .execute(Tuple.of(
                        String.valueOf(userid),
                        startFrom,
                        size
                ))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){

                        List<PostQueryModel> queryModelList = new ArrayList<>();

                        for (Row row : rows){

                            UUID postid                 = row.getUUID("postid");
                            OffsetDateTime createdate   = row.getOffsetDateTime("createdate");
                            String filename             = row.getString("filename");
                            String description          = row.getString("description");
                            UUID followerid             = row.getUUID("followerid");
                            String username             = row.getString("username");


                            queryModelList.add(
                                    new PostQueryModel(
                                            postid,
                                            createdate,
                                            filename,
                                            description,
                                            followerid,
                                            username));
                        };
                        return Future.succeededFuture(queryModelList);
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is no post!"));
                    }

                }).onFailure(err -> {
                    err.printStackTrace();
                });

    }

    @Override
    public Future<String> customizeFeedCount(UUID uid) {
        return pool
                .preparedQuery(
                        "SELECT COUNT(P.postid)  " +
                        "FROM followers AS F, users AS U, posts AS P " +
                                "WHERE F.followerid = P.userid AND F.followerid = U.userid AND F.userid = ($1)"
                                )
                .execute(Tuple.of(
                        String.valueOf(uid)

                ))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){




                        return Future.succeededFuture(
                                String.valueOf(rows.iterator().next().getLong(0))
                        );
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is no post!"));
                    }

                }).onFailure(err -> {
                    err.printStackTrace();
                });
    }


}
