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
    public Future<CommentQueryModel> findCommentPageByUid(UUID postid, int startFrom, int size) {
        System.out.println(startFrom % size);
        if (!(MIN_VALUE < size && size < MAX_VALUE)){
            throw new IllegalArgumentException("Comments : The size of comments is unacceptable");
        }
        if(!((startFrom % size == 0) && (MIN_STARTFROM_VALUE <= startFrom && startFrom < MAX_STARTFROM_VALUE))){
            throw new IllegalArgumentException("Comments : The startFrom is not valid!");
        }
        return pool
                .preparedQuery(
                        "SELECT C.commentid, C.createdate, C.comment, C.userid, U.username, C.postid, COUNT(*) OVER () as TotalCount " +
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

                    List<CommentQueryModel.CommentsQueryModel> commentQueryModelList = new ArrayList<>();
                    if (rows.iterator().hasNext()){

                        for (Row row : rows){

                            UUID commentid              = row.getUUID("commentid");
                            OffsetDateTime createdate   = row.getOffsetDateTime("createdate");
                            String comment              = row.getString("comment");
                            UUID userid                 = row.getUUID("userid");
                            String username             = row.getString("username");


                            commentQueryModelList.add(
                                    new CommentQueryModel.CommentsQueryModel(
                                            commentid,
                                            createdate,
                                            comment,
                                            userid,
                                            username,
                                            postid
                                    )
                            );
                        }


                        return Future.succeededFuture(
                                new CommentQueryModel(
                                        commentQueryModelList,
                                        (rows.iterator().next().getLong("totalcount")
                                        )
                        ));
                    }else{
                        return Future.failedFuture(new IllegalArgumentException("There is no comments!"));
                    }

                }).onFailure(err ->{
                    err.printStackTrace();
                });

    }


}
