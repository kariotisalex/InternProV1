package com.itsaur.internship;


import com.itsaur.internship.comment.CommentService;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.user.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

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
                .put("/user/:userid/password")
                .handler(BodyHandler.create())
                .handler(ctx ->{
                    final JsonObject body = ctx.body().asJsonObject();

                    final UUID userid = UUID.fromString(ctx.pathParam("userid"));
                    final String currentPw = body.getString("current");
                    final String newPw = body.getString("new");

                    this.userService.changePassword(userid, currentPw, newPw)
                            .onSuccess(v -> {
                                System.out.println("Password changes successfully from user " + ctx.pathParam("userid"));
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(e -> {
                                System.out.println("Password changing operation fails from user " + ctx.pathParam("userid"));
                                ctx.response().setStatusCode(400).end();
                            });
                });


        router
                .delete("/user/:userid")
                .handler(ctx -> {
                    System.out.println(ctx.pathParam("userid"));
                    this.userService.deleteByUserid(ctx.pathParam("userid"))
                            .onSuccess(v -> {
                                System.out.println("User :" + ctx.pathParam("userid") + " deleted successfully");
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(v -> {
                                v.printStackTrace();
                                ctx.response().setStatusCode(400).end();
                            });
                });





        server.requestHandler(router).listen(8080);
    }
}

