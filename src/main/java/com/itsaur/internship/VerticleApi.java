package com.itsaur.internship;


import com.itsaur.internship.comment.CommentService;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.query.PostQueryModelStore;
import com.itsaur.internship.user.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.nio.file.Paths;
import java.util.UUID;

public class VerticleApi extends AbstractVerticle {

    final private UserService userService;
    final private CommentService commentService;
    final private PostService postService;
//    final private PostQueryModelStore postQueryModelStore;


    public VerticleApi(UserService userService, CommentService commentService, PostService postService
//                       ,PostQueryModelStore postQueryModelStore
    ) {
        this.userService = userService;
        this.commentService = commentService;
        this.postService = postService;
//        this.postQueryModelStore = postQueryModelStore;
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

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.put("uid" , String.valueOf(v.getUserid()));
                            jsonObject.put("username" , v.getUsername());
                            System.out.println(jsonObject);

                            ctx.response().setStatusCode(200).end(jsonObject.encode());
                        })
                        .onFailure(v -> {
                            System.out.println("Login fails : " + v);
                            ctx.response().setStatusCode(400).end(v.getMessage());
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

                    if (username ==""){
                        ctx.response().setStatusCode(400).end("Empty username");
                    } else if (password == "") {
                        ctx.response().setStatusCode(400).end("Empty password");

                    }else {
                        this.userService.register(username, password)
                            .onSuccess(v -> {
                                System.out.println("Your registration is successful");
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(v -> {
                                ctx.response().setStatusCode(400).end(v.getMessage());
                            });
                    }

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
                                ctx.response().setStatusCode(400).end(e.getMessage());
                            });
                });


        router
                .delete("/user/:userid")
                .handler(ctx -> {
                    final JsonObject body = ctx.body().asJsonObject();
                    final String password = body.getString("password");
                    System.out.println(ctx.pathParam("userid"));

                    this.userService.deleteByUserid(UUID.fromString(ctx.pathParam("userid")))
                            .onSuccess(v -> {
                                System.out.println("User :" + ctx.pathParam("userid") + " deleted successfully");
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(v -> {
                                //v.printStackTrace();
                                ctx.response().setStatusCode(400).end(v.getMessage());
                            });
                });

        router
                .post("/user/:userid/post")
                .handler(BodyHandler
                        .create()
                        .setBodyLimit(5000000)
                        .setUploadsDirectory(String.valueOf(Paths.get("images").toAbsolutePath()))
                )
                .handler(ctx->{
                    FileUpload file = ctx.fileUploads().get(0);
                    String description = ctx.request().getParam("desc");
                    if(file.contentType().split("/")[0].equals("image")){
                        final String fileExt = "." + file.fileName()
                                                         .split("[.]")[file.fileName()
                                                         .split("[.]").length-1];
                        final String savedFileName = file.uploadedFileName()
                                                         .split("/")[file.uploadedFileName()
                                                         .split("/").length-1]+fileExt;


                        vertx.fileSystem().move(file.uploadedFileName(),
                                                file.uploadedFileName() + fileExt)
                                .compose(w -> {
                                    return postService.addPost(UUID.fromString(ctx.pathParam("userid")),savedFileName,description)
                                            .onSuccess(f ->{
                                                ctx.response().setStatusCode(200).end(savedFileName);
                                            })
                                            .onFailure(e -> {
                                                ctx.response().setStatusCode(400).end(e.getMessage());
                                            });
                                });
                    } else {
                        vertx.fileSystem().delete(file.uploadedFileName());
                        ctx.response().setStatusCode(400).end();
                    }
                });

        router
                .put("/user/:userid/post/:postid")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    UUID userid = UUID.fromString(ctx.pathParam("userid"));
                    UUID postid = UUID.fromString(ctx.pathParam("postid"));
                    String description = ctx.body().asJsonObject().getString("desc");

                    this.postService.updatePost(userid, postid,description)
                            .onSuccess(s -> {
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(e -> {
                                System.out.println(e);
                                ctx.response().setStatusCode(400).end(e.getMessage());
                            });
                });


        router
                .delete("/user/:userid/post/:postid")
                .handler(ctx -> {
                    UUID userid = UUID.fromString(ctx.pathParam("userid"));
                    UUID postid = UUID.fromString(ctx.pathParam("postid"));

                    this.postService.deletePost(userid,postid)
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            })
                            .onSuccess(s -> {
                                ctx.response().setStatusCode(200).end();
                            });

                });

        router
                .post("/user/:userid/comment/:postid")
                .handler(BodyHandler.create())
                .handler(ctx ->{
                    UUID userid = UUID.fromString(ctx.pathParam("userid"));
                    UUID postid = UUID.fromString(ctx.pathParam("postid"));
                    String comment = ctx.body().asJsonObject().getString("comment");

                    this.commentService.addComment(userid, postid,comment)
                            .onSuccess(s -> {
                                ctx.response().setStatusCode(200).end(comment);
                            })
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            });

                });

        router
                .put("/user/:userid/comment/:commentid")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    UUID userid = UUID.fromString(ctx.pathParam("userid"));
                    UUID commentid = UUID.fromString(ctx.pathParam("commentid"));
                    String comment = ctx.body().asJsonObject().getString("comment");
                    this.commentService.changeComment(userid, commentid, comment)
                            .onSuccess(s ->{
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(e -> {
                                e.printStackTrace();
                                ctx.response().setStatusCode(400).end();

                            });
                });
        router
                .delete("/user/:userid/comment/:commentid")
                .handler(ctx -> {
                    UUID userid = UUID.fromString(ctx.pathParam("userid"));
                    UUID commentid = UUID.fromString(ctx.pathParam("commentid"));

                    this.commentService.deleteComment(userid, commentid)
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            })
                            .onSuccess(s -> {
                                ctx.response().setStatusCode(200).end();
                            });
                });

        // Retrieve

//        router.get("/users/:userId/posts").handler(ctx -> {
//            postQueryModelStore.findByUserId(UUID.fromString(ctx.pathParam("userId")))
//                    .onSuccess(posts -> {
//                        ctx.response().end(Json.encode(posts));
//                    });
//        });

        router
                .get("/test")
                .handler(ctx -> {
                    ctx.response().sendFile("/home/kariotis@ad.itsaur.com/IdeaProjects/kariotis-internship/images/16c44987-ca8c-4ecb-99d1-ec26d7dadb5f.png");

                    ctx.end();
                });






























        server.requestHandler(router).listen(8080);


    }
}

