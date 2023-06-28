package com.itsaur.internship.post;

import com.itsaur.internship.comment.CommentStore;
import com.itsaur.internship.user.User;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class PostgresPostStore implements PostStore{

    private final Vertx vertx;
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);
    private CommentStore commentStore;

    public PostgresPostStore(Vertx vertx, PgConnectOptions connectOptions, CommentStore commentStore) {
        this.vertx = vertx;
        this.connectOptions = connectOptions;
        this.commentStore = commentStore;
    }


    public Future<Void> insert(Post post) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return Future.succeededFuture()
                .compose(q -> {
                    return client
                            .preparedQuery("INSERT INTO images(imageid, createdate, image, description, personid)\n" +
                                    "SELECT ($1) , ($2) , ($3), ($4), personid FROM users WHERE personid=($5)")
                            .execute(Tuple.of(post.getImageid(), post.initCreateDate(), post.getFilename(), post.getDescription(), post.getUser().getPersonid()))
                            .onFailure(e -> {
                                e.printStackTrace();
                            })
                            .compose(w -> {
                                return client.close();
                            });
                });
    }

    @Override
    public Future<Post> findPost(String filename) {
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT i.imageid, i.createdate, i.updatedate," +
                                          "i.description, u.personid, u.createdate u.username, u.password " +
                                    "FROM images i, users U " +
                                    "WHERE i.personid=u.personid AND i.image=($1)")
                .execute(Tuple.of(filename))
                .compose(rows -> {
                    final Post post;
                    if (rows.iterator().hasNext()){
                        UUID imageid = rows.iterator().next().getUUID(0);
                        LocalDateTime createdate = rows.iterator().next().getLocalDateTime(1);
                        LocalDateTime updatedate = rows.iterator().next().getLocalDateTime(2);
                        String description = rows.iterator().next().getString(3);
                        post = new Post(imageid, createdate,updatedate, filename, description,
                                new User(rows.iterator().next().getUUID(4),
                                        rows.iterator().next().getLocalDateTime(5),
                                        rows.iterator().next().getString(6),
                                        rows.iterator().next().getString(7))
                        );
                        client.close();
                        return Future.succeededFuture(post);
                    }else {
                        client.close();
                        return Future.failedFuture(new NullPointerException());
                    }
                });
    }

    @Override
    public Future<Void> updatePost(User user, String description) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("UPDATE images SET description=($2), updatedate=($3) WHERE personid=($1)")
                .execute(Tuple.of(user.getPersonid(), description, LocalDateTime.now()))
                .compose(q -> {
                    client.close();
                    return Future.succeededFuture();
                });
    }
    @Override
    public Future<Void> deletePost(String filename){
        SqlClient client = PgPool.client(vertx, connectOptions,poolOptions);
        return client
                .preparedQuery("DELETE FROM images WHERE image=($1)")
                .execute(Tuple.of(filename))
                .compose(w -> {
                    return client
                            .close()
                            .compose(r-> {
                                return vertx.fileSystem().delete(String.valueOf(Paths.get("src/main/java/com/itsaur/internship/images",filename).toAbsolutePath()));
                            });
                });
    }

    @Override
    public Future<Void> deleteFromUser(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions,poolOptions);
        retrieveAll(user)
                .compose(res -> {
                    return vertx.executeBlocking(o -> {
                        IntStream.range(0, res.size())
                                .forEach(i -> {
                                    findPost(res.get(i))
                                            .compose(w -> {
                                                return this.commentStore.deleteFromPost(w);
                                            });
                                });
                    });
                })
                .compose(r -> {

                });

        return client
                .preparedQuery("DELETE FROM images WHERE personid=($1)")
                .execute(Tuple.of(user.getPersonid()))
                .compose(w -> {
                    return client
                            .close()
                            .compose(r-> {
                                return vertx.fileSystem().delete(String.valueOf(Paths.get("src/main/java/com/itsaur/internship/images",filename).toAbsolutePath()));
                            });
                });
    }

    @Override
    public Future<List<String>> retrieveAll(User user){
        SqlClient client = PgPool.client(vertx,connectOptions,poolOptions);
        return client
                .preparedQuery("SELECT image FROM images WHERE personid=($1)\n" +
                                   "WHERE personid=($1)")
                .execute(Tuple.of(user.getPersonid()))
                .compose(rows -> {
                    List<String> posts = new ArrayList<>();
                    if (rows.iterator().hasNext()){
                        for (Row row : rows)
                            posts.add(row.getString("image"));
                    }
                    client.close();
                    return Future.succeededFuture(posts);
                });
    }



}
