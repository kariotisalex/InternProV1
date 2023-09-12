package com.itsaur.internship.comment.query;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresCommentQueryModelStore implements CommentQueryModelStore{
    private PgPool pool;

    public PostgresCommentQueryModelStore(PgPool pool) {
        this.pool = pool;
    }

    @Override
    public Future<List<CommentQueryModel>> findAllByPostId(UUID postid){

        return pool
                .preparedQuery("SELECT C.commentid, C.createdate, C.comment, C.userid, U.username, C.postid " +
                        "FROM posts AS P , comments AS C, users AS U " +
                        "WHERE P.postid = C.postid AND U.userid = P.userid AND P.postid=($1)" +
                        "ORDER BY (createdate) DESC")
                .execute(Tuple.of(String.valueOf(postid)))
                .onFailure(err -> {
                    err.printStackTrace();
                })
                .compose(rows -> {
                    List<CommentQueryModel> commentQueryModelList = new ArrayList<>();

                    if (rows.iterator().hasNext()){
                        for (Row row : rows){

                            String commentid    = String.valueOf(row.getUUID("commentid"));
                            String createdate   = String.valueOf(row.getOffsetDateTime("createdate"));
                            String comment      = row.getString("comment");
                            String userid       = String.valueOf(row.getUUID("userid"));
                            String username     = row.getString("username");




                            commentQueryModelList.add(
                                    new CommentQueryModel(commentid, createdate,
                                            comment, userid, username, String.valueOf(postid)));
                        }
                        return Future.succeededFuture(commentQueryModelList);
                    }else{
                        return Future.failedFuture(new IllegalArgumentException("There is no comments!"));
                    }

                }).onFailure(err ->{
                    err.printStackTrace();
                });
    }






}
