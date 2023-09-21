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
import com.itsaur.internship.follower.FollowerService;
import com.itsaur.internship.follower.FollowerStore;
import com.itsaur.internship.follower.PostgresFollowerStore;
import com.itsaur.internship.follower.query.FollowerQueryModelStore;
import com.itsaur.internship.follower.query.PostgresFollowerQueryModelStore;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.post.PostStore;
import com.itsaur.internship.post.PostgresPostStore;
import com.itsaur.internship.post.query.PostQueryModelStore;
import com.itsaur.internship.post.query.PostgresPostQueryModelStore;
import com.itsaur.internship.user.PostgresUsersStore;
import com.itsaur.internship.user.UserService;
import com.itsaur.internship.user.UsersStore;
import com.itsaur.internship.user.query.PostgresUserQueryModelStore;
import com.itsaur.internship.user.query.UserQueryModel;
import com.itsaur.internship.user.query.UserQueryModelStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.time.OffsetDateTime;
import java.util.List;

public class Application {


    final UserService userService;
    final CommentService commentService;
    final PostService postService;
    final FollowerService followerService;
    final PostQueryModelStore postQueryModelStore;

    final CommentQueryModelStore commentQueryModelStore;

    final UserQueryModelStore userQueryModelStore;
    final FollowerQueryModelStore followerQueryModelStore;

    public Application(
            PostService postService,
            UserService userService,
            CommentService commentService,
            FollowerService followerService,
            PostQueryModelStore postQueryModelStore,
            CommentQueryModelStore commentQueryModelStore,
            UserQueryModelStore userQueryModelStore,
            FollowerQueryModelStore followerQueryModelStore
    ){
        this.userService = userService;
        this.commentService = commentService;
        this.postService = postService;
        this.followerService = followerService;
        this.postQueryModelStore = postQueryModelStore;
        this.commentQueryModelStore = commentQueryModelStore;
        this.userQueryModelStore = userQueryModelStore;
        this.followerQueryModelStore = followerQueryModelStore;
    }








    public static void main(String[] args) {
        System.out.println(OffsetDateTime.now());
        Vertx vertx = Vertx.vertx();


        PostgresOptions postgresOptions = new PostgresOptions();
        PgPool pool = PgPool.pool(
                vertx,
                postgresOptions
                        .getPgConnectOptions(),
                new PoolOptions()
                        .setMaxSize(5));

        PostStore postgresPostStore                     = new PostgresPostStore (pool);
        UsersStore postgresUsersStore                   = new PostgresUsersStore (pool);
        CommentStore postgresCommentStore               = new PostgresCommentStore (pool);
        FollowerStore postgresFollowerStore             = new PostgresFollowerStore(pool);

        PostQueryModelStore postQueryModelStore         = new PostgresPostQueryModelStore(pool);
        CommentQueryModelStore commentQueryModelStore   = new PostgresCommentQueryModelStore(pool);
        UserQueryModelStore userQueryModelStore         = new PostgresUserQueryModelStore(pool);
        FollowerQueryModelStore followerQueryModelStore = new PostgresFollowerQueryModelStore(pool);

        Application application = new Application(
                new PostService(vertx, postgresPostStore, postgresUsersStore, postgresCommentStore),
                new UserService(vertx, postgresPostStore, postgresUsersStore, postgresCommentStore, postgresFollowerStore),
                new CommentService(postgresPostStore, postgresUsersStore, postgresCommentStore),
                new FollowerService(postgresFollowerStore),
                postQueryModelStore,
                commentQueryModelStore,
                userQueryModelStore,
                followerQueryModelStore
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
                        application.followerService,
                        application.postQueryModelStore,
                        application.commentQueryModelStore,
                        application.userQueryModelStore,
                        application.followerQueryModelStore
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
