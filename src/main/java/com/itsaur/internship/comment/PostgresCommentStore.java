package com.itsaur.internship.comment;

import com.itsaur.internship.post.Post;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresCommentStore implements CommentStore{
    private final Vertx vertx;
    private final PgConnectOptions connectOptions;

    final PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);
    public PostgresCommentStore(Vertx vertx, PgConnectOptions connectOptions) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
    }


    @Override
    public Future<Void> insert(Comment comment) {
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);
        return client
                .preparedQuery("INSERT INTO comments(commentid, createdate, comment, userid, postid) " +
                                   "VALUES ($1, $2, $3, $4, $5)")
                .execute(Tuple.of(UUID.randomUUID(), LocalDateTime.now(), comment.getComment(),
                                  comment.getUserid(), comment.getPostid()))
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(q -> {
                    return client.close();
                });
    }

    @Override
    public Future<Comment> findById(UUID commentid) {
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT commentid, createdate, updatedate, comment, userid, postid " +
                        "FROM comments WHERE commentid = ($1)")
                .execute(Tuple.of(commentid))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){
                        Row row = rows.iterator().next();
                        return Future.succeededFuture( new Comment(
                                row.getUUID(0),
                                row.getLocalDateTime(1),
                                row.getLocalDateTime(2),
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
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);
        return client
                .preparedQuery("UPDATE comments SET createdate=($2), updatedate=($3), comment=($4), userid=($5), postid=($6)" +
                                   "WHERE commentid=($1)")
                .execute(Tuple.of(comment.getCommentid(),comment.getCreatedate(), comment.getUpdatedate(), comment.getComment(), comment.getUserid(), comment.getPostid()))
                .onFailure(e -> {
                    System.out.println("update, Comment Store");
                    e.printStackTrace();
                })
                .compose(q -> {
                    return client.close();
                });
    }

    @Override
    public Future<Void> deleteById(UUID commentid) {
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);
        return client
                .preparedQuery("DELETE FROM comments WHERE commentid=($1)")
                .execute(Tuple.of(commentid))
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(q -> {
                    return client.close();
                });
    }

    @Override
    public Future<Void> deleteByPostid(UUID postid) {
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);
        return client
                .preparedQuery("DELETE FROM comments WHERE postid=($1)")
                .execute(Tuple.of(postid))
                .compose(q -> {
                    return client.close();
                });
    }
    @Override
    public Future<List<Comment>> readAllById(UUID commentid){
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT commentid, createdate, updatedate, comment, userid, postid" +
                                   "FROM comments WHERE commentid=($1)")
                .execute(Tuple.of(commentid))
                .compose(rows -> {
                    List<Comment> listComment = new ArrayList<>();
                    for (Row row : rows){
                        listComment.add(new Comment(row.getUUID(0),
                                row.getLocalDateTime(1),
                                row.getLocalDateTime(2),
                                row.getString(3),
                                row.getUUID(4),
                                row.getUUID(5))
                        );
                    }
                    return Future.succeededFuture(listComment);
                });
    }


}
