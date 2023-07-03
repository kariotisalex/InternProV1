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
                .preparedQuery("SELECT commentid, createdate, updatedate, comment, userid, postid" +
                                   "FROM comments WHERE commentid=($1)")
                .execute(Tuple.of(commentid))
                .compose(rows -> {
                    if (rows.iterator().hasNext()){
                        return Future.succeededFuture( new Comment(
                                rows.iterator().next().getUUID(0),
                                rows.iterator().next().getLocalDateTime(1),
                                rows.iterator().next().getLocalDateTime(2),
                                rows.iterator().next().getString(3),
                                rows.iterator().next().getUUID(4),
                                rows.iterator().next().getUUID(5)));
                    }else {
                        return Future.failedFuture(new NullPointerException("There is nothing in this commendid"));
                    }
                });
    }

    @Override
    public Future<Void> update(Comment comment) {
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);
        return client
                .preparedQuery("UPDATE comments" +
                                   "SET comment=($2), updatedate=($3)" +
                                   "WHERE commentid=($1)")
                .execute(Tuple.of(comment.getCommentid(),comment.getComment(), LocalDateTime.now()))
                .onFailure(e -> {
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
                .preparedQuery("DELETE comments WHERE commentid=($1)")
                .execute(Tuple.of(commentid))
                .compose(q -> {
                    return client.close();
                });
    }

    @Override
    public Future<Void> deleteByPost(UUID postid) {
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
