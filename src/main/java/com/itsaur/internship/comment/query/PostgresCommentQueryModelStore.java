package com.itsaur.internship.comment.query;

import com.itsaur.internship.post.query.PostQueryModel;
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
import java.util.UUID;

public class PostgresCommentQueryModelStore implements CommentQueryModelStore{
    final private int MIN_VALUE = 0;
    final private int MAX_VALUE = 30;
    final private int MIN_STARTFROM_VALUE = 0;

    final private  int MAX_STARTFROM_VALUE = 100;
    private PgPool pool;

    public PostgresCommentQueryModelStore(PgPool pool) {
        this.pool = pool;
    }

    @Override
    public Future<List<CommentQueryModel>> findAllByPostId(UUID postid){

        return pool
                .preparedQuery("SELECT C.commentid, C.createdate, C.comment, C.userid, U.username, C.postid " +
                        "FROM posts AS P , comments AS C, users AS U " +
                        "WHERE P.postid = C.postid AND U.userid = C.userid AND P.postid=($1)" +
                        "ORDER BY (createdate) DESC")
                .execute(Tuple.of(String.valueOf(postid)))
                .onFailure(err -> {
                    err.printStackTrace();
                })
                .compose(rows -> {
                    List<CommentQueryModel> commentQueryModelList = new ArrayList<>();

                    JsonArray jsonArray = new JsonArray();
                    if (rows.iterator().hasNext()){
                        for (Row row : rows){

                            UUID commentid              = row.getUUID("commentid");
                            OffsetDateTime createdate   = row.getOffsetDateTime("createdate");
                            String comment              = row.getString("comment");
                            UUID userid                 = row.getUUID("userid");
                            String username             = row.getString("username");


                            commentQueryModelList.add(
                                    new CommentQueryModel(
                                            commentid,
                                            createdate,
                                            comment,
                                            userid,
                                            username,
                                            postid
                                    )
                            );
                        }
                        return Future.succeededFuture(commentQueryModelList);
                    }else{
                        return Future.failedFuture(new IllegalArgumentException("There is no comments!"));
                    }

                }).onFailure(err ->{
                    err.printStackTrace();
                });
    }



    @Override
    public Future<List<CommentQueryModel>> findCommentPageByUid(UUID postid, int startFrom, int size) {
        System.out.println(startFrom % size);
        if (!(MIN_VALUE < size && size < MAX_VALUE)){
            throw new IllegalArgumentException("Comments : The size of comments is unacceptable");
        }
        if(!((startFrom % size == 0) && (MIN_STARTFROM_VALUE <= startFrom && startFrom < MAX_STARTFROM_VALUE))){
            throw new IllegalArgumentException("Comments : The startFrom is not valid!");
        }
        return pool
                .preparedQuery(
                        "SELECT C.commentid, C.createdate, C.comment, C.userid, U.username, C.postid " +
                                "FROM posts AS P , comments AS C, users AS U " +
                                "WHERE P.postid = C.postid AND U.userid = C.userid AND P.postid=($1)" +
                                "ORDER BY (createdate) DESC " +
                                "OFFSET ($2) ROWS FETCH FIRST ($3) ROWS ONLY")
                .execute(Tuple.of(
                        String.valueOf(postid),
                        startFrom,
                        size
                )).onFailure(err -> {
                    err.printStackTrace();
                })
                .compose(rows -> {


                    List<CommentQueryModel> commentQueryModelList = new ArrayList<>();

                    if (rows.iterator().hasNext()){
                        for (Row row : rows){

                            UUID commentid              = row.getUUID("commentid");
                            OffsetDateTime createdate   = row.getOffsetDateTime("createdate");
                            String comment              = row.getString("comment");
                            UUID userid                 = row.getUUID("userid");
                            String username             = row.getString("username");


                            commentQueryModelList.add(
                                    new CommentQueryModel(
                                            commentid,
                                            createdate,
                                            comment,
                                            userid,
                                            username,
                                            postid
                                    )
                            );
                        }
                        return Future.succeededFuture(commentQueryModelList);
                    }else{
                        return Future.failedFuture(new IllegalArgumentException("There is no comments!"));
                    }

                }).onFailure(err ->{
                    err.printStackTrace();
                });

    }

    @Override
    public Future<String> countAllCommentsByPid(UUID pid) {
        return pool
                .preparedQuery("SELECT count(commentid) " +
                        "FROM comments AS C, posts AS P " +
                        "WHERE C.postid = P.postid AND C.postid=($1)")
                .execute(Tuple.of(String.valueOf(pid)))
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
}
