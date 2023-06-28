package com.itsaur.internship;


import com.itsaur.internship.user.UserService;
import com.itsaur.internship.content.ContentService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import java.nio.file.Paths;

public class VerticleApi extends AbstractVerticle {
    private final UserService service;
    private ContentService contentService;

    public VerticleApi(UserService service) {
        this.service = service;
    }
    public VerticleApi(UserService service, ContentService contentService) {
        this.service = service;
        this.contentService = contentService;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        WebClient client = WebClient.create(vertx);



        router
                .post("/login")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    final JsonObject body = ctx.body().asJsonObject();
                    this.service.login(body.getString("username"),
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
                .post("/register")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    final JsonObject body = ctx.body().asJsonObject();
                    this.service.register(body.getString("username"),
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
                    this.service.delete(ctx.pathParam("username"))
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
                .put("/users/:username/password")
                .handler(BodyHandler.create())
                .handler(ctx ->{
                    final JsonObject body = ctx.body().asJsonObject();

                    final String username = ctx.pathParam("username");
                    final String currentPassword = body.getString("currentPassword");
                    final String newPassword = body.getString("newPassword");

                    this.service.changePassword(username, currentPassword, newPassword)
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
                .post("/upload/post/:username")
                //.consumes("image/png")
                .handler(BodyHandler
                        .create()
                        .setBodyLimit(5000000)
                        .setUploadsDirectory(String.valueOf(Paths.get("src/main/java/com/itsaur/internship/images").toAbsolutePath())))
                .produces("application/json")
                .handler(ctx->{
                    FileUpload file = ctx.fileUploads().get(0);
                    System.out.println(file.contentType());
                    if(file.contentType().split("/")[0].equals("image")){
                        String fileExt = "." + file.fileName().split("[.]")[file.fileName().split("[.]").length-1];
                        String savedFileName = file.uploadedFileName().split("/")[file.uploadedFileName().split("/").length-1]+fileExt;
                        vertx.fileSystem().move(file.uploadedFileName(),
                                                file.uploadedFileName() + fileExt)
                                .compose(w -> {
                                    return contentService.addPost(ctx.pathParam("username"),savedFileName,"description")
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
                .post("/upload/comment/:filename")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    String filename = ctx.pathParam("filename");
                    String comment = ctx.body().asJsonObject().getString("comment");

                    contentService.addComment(filename,comment)
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            })
                            .onSuccess(q->{
                                ctx.response().setStatusCode(200).end();
                            });

                });

        router
                .delete("/delete/post/:filename")
                .handler(ctx -> {
                    String filename = ctx.pathParam("filename");
                    contentService.deletePost(filename)
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
                    contentService.retrieveAllPosts(username)
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
                    contentService.deleteCommment(cid)
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

