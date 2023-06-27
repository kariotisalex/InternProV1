package com.itsaur.internship.Comment;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

import java.util.UUID;

public class PostgresCommentStore implements CommentStore{
    private final Vertx vertx;
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    public PostgresCommentStore(Vertx vertx, PgConnectOptions connectOptions) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
    }


    @Override
    public Future<Void> insert(Comment comment) {
        SqlClient client = PgPool.client(vertx,connectOptions, poolOptions);
        return null;
//                this.findImage(filename).compose(q -> {
//            return client
//                    .preparedQuery("INSERT INTO comments(commentid, date, comment, imageid)" +
//                            "SELECT ($1), now(), ($2), imageid FROM images WHERE image=($3)")
//                    .execute(Tuple.of(UUID.randomUUID(), comment, filename))
//                    .onFailure(e -> {
//                        System.out.println(e);
//                        e.printStackTrace();
//                    })
//                    .compose(r -> {
//                        return client.close();
//                    });
//        });
    }

    @Override
    public Future<Comment> find(UUID commentid) {
        return null;
    }

    @Override
    public Future<Void> update(UUID commentid) {
        return null;
    }

    @Override
    public Future<Void> delete(Comment comment) {
        return null;
    }
}
