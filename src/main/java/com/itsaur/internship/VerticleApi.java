package com.itsaur.internship;


import com.itsaur.internship.comment.CommentService;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.post.PostStore;
import com.itsaur.internship.post.PostgresPostStore;
import com.itsaur.internship.user.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;

import java.nio.file.Paths;
import java.util.UUID;

public class VerticleApi extends AbstractVerticle {

    final private UserService userService;
    final private CommentService commentService;
    final private PostService postService;


    public VerticleApi(UserService userService, CommentService commentService, PostService postService) {
        this.userService = userService;
        this.commentService = commentService;
        this.postService = postService;
        System.out.println("mpika kai edw");
    }


    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

    router
            .post("/user/login")
            .handler(BodyHandler.create())
            .handler(ctx -> {
                final JsonObject body = ctx.body().asJsonObject();
                String username = body.getString("username");
                System.out.println(username);
                String password = body.getString("password");
                System.out.println(password);

                this.userService.login(username, password)
                        .onSuccess(v -> {
                            System.out.println("Successful Login");
                            ctx.response().setStatusCode(200).end();
                        })
                        .onFailure(v -> {
                            System.out.println("Login fails : " + v);
                            ctx.response().setStatusCode(400).end();
                        });
    });

        router
                .post("/user/register")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    final JsonObject body = ctx.body().asJsonObject();
                    String username = body.getString("username");
                    System.out.println(username);
                    String password = body.getString("password");
                    System.out.println(password);

                    this.userService.register(username, password)
                            .onSuccess(v -> {
                                System.out.println("Your registration is successful");
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(v -> {
                                v.printStackTrace();
                                ctx.response().setStatusCode(400).end();
                            });
                });


        router
                .delete("/user/:userid")
                .handler(ctx -> {
                    System.out.println(ctx.pathParam("username"));
                    this.userService.deleteByUsername(ctx.pathParam("username"))
                            .onSuccess(v -> {
                                System.out.println("User :" + ctx.pathParam("username") + " deleted successfully");
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(v -> {
                                System.out.println("The delete operation fails " + v);
                                ctx.response().setStatusCode(400).end();
                            });
                });



        server.requestHandler(router).listen(8080);
    }
}

