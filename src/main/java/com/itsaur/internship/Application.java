package com.itsaur.internship;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.itsaur.internship.comment.CommentService;
import com.itsaur.internship.comment.CommentStore;
import com.itsaur.internship.comment.PostgresCommentStore;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.post.PostStore;
import com.itsaur.internship.post.PostgresPostStore;
import com.itsaur.internship.user.PostgresUsersStore;
import com.itsaur.internship.user.UserService;
import com.itsaur.internship.user.UsersStore;
import io.vertx.core.Vertx;

public class Application {


    final UserService service;
    final CommentService commentService;
    final PostService postService;


    public Application(PostService postService,  UserService service, CommentService commentService) {
        this.service = service;
        this.commentService = commentService;
        this.postService = postService;
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        PostgresOptions postgresOptions = new PostgresOptions();

        PostgresPostStore postgresPostStore = new PostgresPostStore(vertx, postgresOptions.getPgConnectOptions());
        PostgresUsersStore postgresUsersStore = new PostgresUsersStore(vertx, postgresOptions.getPgConnectOptions());
        PostgresCommentStore postgresCommentStore = new PostgresCommentStore(vertx, postgresOptions.getPgConnectOptions());

        Application application = new Application(
                new PostService(vertx, postgresPostStore, postgresUsersStore, postgresCommentStore),
                new UserService(vertx, postgresPostStore, postgresUsersStore, postgresCommentStore),
                new CommentService(vertx, postgresCommentStore)
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

            System.out.println("hrtha edw");
            vertx.deployVerticle(
                    new VerticleApi(
                        application.service,
                        application.commentService,
                        application.postService
                    )
            );

        }else if (postgresOptions.getService().equals("console")) {
            new UserConsole(application.service).executeCommand(args)
                    .onComplete(v -> System.exit(0));
        } else {
            System.out.println("Something went wrong!");
        }




    }
}
