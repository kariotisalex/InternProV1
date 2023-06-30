package com.itsaur.internship;


import com.itsaur.internship.comment.CommentService;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.post.PostStore;
import com.itsaur.internship.user.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
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
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        WebClient client = WebClient.create(vertx);





        router
                .post("/user/login")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    final JsonObject body = ctx.body().asJsonObject();
                    this.userService.login(body.getString("username"),
                                    body.getString("password"))
                            .onSuccess(v -> {
                                System.out.println("Successful Login");
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(v -> {
                                System.out.println("Login fails" + v);
                                ctx.response().setStatusCode(400).end();
                            });
                });
        router
                .post("/user/register")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    final JsonObject body = ctx.body().asJsonObject();
                    this.userService.register(body.getString("username"),
                                    body.getString("password"))
                            .onSuccess(v -> {
                                System.out.println("Your registration is successful");
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(v -> {
                                System.out.println("Your registration fails " + v);
                                ctx.response().setStatusCode(400).end();
                            });
                });
        router
                .delete("/users/:username")
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
        router
                .put("/user/:username/password")
                .handler(BodyHandler.create())
                .handler(ctx ->{
                    final JsonObject body = ctx.body().asJsonObject();

                    final String username = ctx.pathParam("username");
                    final String currentPassword = body.getString("currentPassword");
                    final String newPassword = body.getString("newPassword");

                    this.userService.changePassword(username, currentPassword, newPassword)
                            .onSuccess(v -> {
                                System.out.println("Password changes successfully from user " + ctx.pathParam("username"));
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(e -> {
                                System.out.println("Password changing operation fails from user " + ctx.pathParam("username"));
                                ctx.response().setStatusCode(400).end();
                            });
                });

        router
                .delete("/user/:username/")
                .handler(ctx -> {
                    this.userService.deleteByUsername(ctx.pathParam("username"))
                            .onSuccess(suc -> {
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            });
                });





        router
                .post("/test")
                        .handler(BodyHandler.create().setUploadsDirectory(String.valueOf(Paths.get("images").toAbsolutePath())))
                        .handler(ctx -> {
                            FileUpload file = ctx.fileUploads().get(0);
                            System.out.println(file.uploadedFileName());
                            System.out.println(file.contentType());
                            System.out.println(file);

                        });







        router
                .post("/user/:username/post/")
                .handler(BodyHandler
                        .create()
                        .setBodyLimit(5000000)
                        .setUploadsDirectory(String.valueOf(Paths.get("images").toAbsolutePath())))
                .handler(ctx->{
                    FileUpload file = ctx.fileUploads().get(0);
                    if(file.contentType().split("/")[0].equals("image")){
                        final String fileExt = "." + file.fileName().split("[.]")[file.fileName().split("[.]").length-1];
                        final String savedFileName = file.uploadedFileName().split("/")[file.uploadedFileName().split("/").length-1]+fileExt;


                        vertx.fileSystem().move(file.uploadedFileName(),
                                                file.uploadedFileName() + fileExt)
                                .compose(w -> {
                                    return postService.addPost(ctx.pathParam("username"),savedFileName,"description")
                                            .onSuccess(f ->{
                                                ctx.response().setStatusCode(200).end(savedFileName);
                                            })
                                            .onFailure(e -> {
                                                ctx.response().setStatusCode(400).end();
                                            });
                                });
                    } else {
                        vertx.fileSystem().delete(file.uploadedFileName());
                        ctx.response().setStatusCode(400).end();
                    }
                });


        router
                .post("/user/:username/comment/:filename")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    String filename = ctx.pathParam("filename");
                    String comment = ctx.body().asJsonObject().getString("comment");
                    String username = ctx.pathParam("username");

                    commentService.addComment(comment, username, filename)
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            })
                            .onSuccess(q->{
                                ctx.response().setStatusCode(200).end();
                            });

                });

        router
                .delete("user/:username/post/:filename")
                .handler(ctx -> {
                    String filename = ctx.pathParam("filename");
                    String username = ctx.pathParam("username");
                    postService.deletePost(username,filename)
                            .onSuccess(s -> {
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            });

                });

        router
                .get("/load/post/:username")
                .handler(ctx -> {
                    String username = ctx.pathParam("username");
                    postService.retrieveAllPosts(username)
                            .onSuccess(q -> {
                                System.out.println(q);
                                System.out.println();
                                for (String str : q){
                                    System.out.println(str);
                                }
                                ctx.end();
                            }).onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            });
                });

        router
                .delete("/delete/comment/:commentid")
                .handler(ctx -> {
                    String cid = ctx.pathParam("commentid");
                    commentService.deleteComment(UUID.fromString(cid))
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            })
                            .onSuccess(q -> {
                                ctx.response().setStatusCode(200).end();
                            });
                });




        server.requestHandler(router).listen(8080);
    }
}

