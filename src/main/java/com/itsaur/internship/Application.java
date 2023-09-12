package com.itsaur.internship;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itsaur.internship.comment.CommentService;
import com.itsaur.internship.comment.CommentStore;
import com.itsaur.internship.comment.PostgresCommentStore;
import com.itsaur.internship.comment.query.CommentQueryModelStore;
import com.itsaur.internship.comment.query.PostgresCommentQueryModelStore;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.post.PostStore;
import com.itsaur.internship.post.PostgresPostStore;
import com.itsaur.internship.post.query.PostQueryModelStore;
import com.itsaur.internship.post.query.PostgresPostQueryModelStore;
import com.itsaur.internship.user.PostgresUsersStore;
import com.itsaur.internship.user.UserService;
import com.itsaur.internship.user.UsersStore;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.time.OffsetDateTime;

public class Application {


    final UserService userService;
    final CommentService commentService;
    final PostService postService;
    final PostQueryModelStore postQueryModelStore;

    final CommentQueryModelStore commentQueryModelStore;


    public Application(
            PostService postService,
            UserService userService,
            CommentService commentService,
            PostQueryModelStore postQueryModelStore,
            CommentQueryModelStore commentQueryModelStore)
    {
        this.userService = userService;
        this.commentService = commentService;
        this.postService = postService;
        this.postQueryModelStore = postQueryModelStore;
        this.commentQueryModelStore = commentQueryModelStore;
    }








    public static void main(String[] args) {
        System.out.println(OffsetDateTime.now());
        Vertx vertx = Vertx.vertx();
        io.vertx.core.json.jackson.DatabindCodec codec = (io.vertx.core.json.jackson.DatabindCodec) io.vertx.core.json.Json.CODEC;


        PostgresOptions postgresOptions = new PostgresOptions();
        PgPool pool = PgPool.pool(
                vertx,
                postgresOptions
                        .getPgConnectOptions(),
                new PoolOptions()
                        .setMaxSize(5));

        PostStore postgresPostStore                   = new PostgresPostStore (pool);
        UsersStore postgresUsersStore                 = new PostgresUsersStore (pool);
        CommentStore postgresCommentStore             = new PostgresCommentStore (pool);
        PostQueryModelStore postQueryModelStore       = new PostgresPostQueryModelStore(pool);
        CommentQueryModelStore commentQueryModelStore = new PostgresCommentQueryModelStore(pool);

        Application application = new Application(
                new PostService(vertx, postgresPostStore, postgresUsersStore, postgresCommentStore),
                new UserService(vertx, postgresPostStore, postgresUsersStore, postgresCommentStore),
                new CommentService(postgresPostStore, postgresUsersStore, postgresCommentStore),
                new PostgresPostQueryModelStore(pool),
                new PostgresCommentQueryModelStore(pool)
        );

        try{
            JCommander.newBuilder()
                    .addObject(postgresOptions)
                    .build()
                    .parse(args);
        }catch (ParameterException e){
            System.out.println(e.getMessage());
        }



        if (postgresOptions.getService().equals("serverdb")){

            System.out.println("Application.java : deployVerticle()");
            vertx.deployVerticle(
                    new VerticleApi(
                        application.userService,
                        application.commentService,
                        application.postService ,
                        application.postQueryModelStore,
                        application.commentQueryModelStore
                    )
            ).onFailure(e -> {
                e.printStackTrace();
            });

        }else if (postgresOptions.getService().equals("console")) {
            new UserConsole(application.userService).executeCommand(args)
                    .onComplete(v -> System.exit(0));
        } else {
            System.out.println("Something went wrong!");
        }




    }
}
