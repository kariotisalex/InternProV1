package com.itsaur.internship.comment;

import com.itsaur.internship.post.Post;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresCommentStore implements CommentStore{
    private PgPool pool;
    public PostgresCommentStore(PgPool pool) {
        this.pool = pool;
    }


    @Override
    public Future<Void> insert(Comment comment) {

        return pool
                .preparedQuery("INSERT INTO comments(commentid, createdate, comment, userid, postid) " +
                                   "VALUES ($1, $2, $3, $4, $5)")
                .execute(Tuple.of(
                        UUID.randomUUID(),
                        OffsetDateTime.now(),
                        comment.comment(),
                        comment.userid(),
                        comment.postid()))
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .mapEmpty();
    }

    @Override
    public Future<Comment> findById(UUID commentid) {
        return pool
                .preparedQuery("SELECT commentid, createdate, updatedate, comment, userid, postid " +
                        "FROM comments WHERE commentid = ($1)")
                .execute(Tuple.of(commentid))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){
                        Row row = rows.iterator().next();
                        return Future.succeededFuture( new Comment(
                                row.getUUID(0),
                                row.getOffsetDateTime(1),
                                row.getOffsetDateTime(2),
                                row.getString(3),
                                row.getUUID(4),
                                row.getUUID(5)));
                    }else {
                        return Future.failedFuture(new NullPointerException("There is nothing in this commendid"));
                    }
                });
    }

    @Override
    public Future<Void> update(Comment comment) {

        return pool
                .preparedQuery("UPDATE comments SET createdate=($2), updatedate=($3), comment=($4), userid=($5), postid=($6)" +
                                   "WHERE commentid=($1)")
                .execute(Tuple.of(
                        comment.commentid(),
                        comment.createdate(),
                        comment.updatedate(),
                        comment.comment(),
                        comment.userid(),
                        comment.postid()))
                .onFailure(e -> {
                    System.out.println("update, Comment Store");
                    e.printStackTrace();
                })
                .mapEmpty();
    }

    @Override
    public Future<Void> deleteById(UUID commentid) {

        return pool
                .preparedQuery("DELETE FROM comments WHERE commentid=($1)")
                .execute(Tuple.of(commentid))
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .mapEmpty();
    }

    @Override
    public Future<Void> deleteByPostid(UUID postid) {

        return pool
                .preparedQuery("DELETE FROM comments WHERE postid=($1)")
                .execute(Tuple.of(postid))
                .mapEmpty();
    }
    @Override
    public Future<List<Comment>> readAllByPostid(UUID postid){

        return pool
                .preparedQuery("SELECT commentid, createdate, updatedate, comment, userid, postid" +
                                   "FROM comments WHERE postid=($1)")
                .execute(Tuple.of(postid))
                .compose(rows -> {
                    List<Comment> listComment = new ArrayList<>();
                    for (Row row : rows){
                        listComment.add(new Comment(row.getUUID(0),
                                row.getOffsetDateTime(1),
                                row.getOffsetDateTime(2),
                                row.getString(3),
                                row.getUUID(4),
                                row.getUUID(5))
                        );
                    }
                    return Future.succeededFuture(listComment);
                });
    }


}
