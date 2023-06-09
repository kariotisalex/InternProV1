package com.itsaur.internship;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class UserVerticle extends AbstractVerticle {
    private final UserService service;

    public UserVerticle(UserService service) {
        this.service = service;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
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
                    System.out.println(ctx.pathParam("username"));
                    final JsonObject body = ctx.body().asJsonObject();

                    String username = ctx.pathParam("username");
                    String currentPassword = body.getString("currentPassword");
                    String newPassword = body.getString("newPassword");

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

        server.requestHandler(router).listen(8080);
    }
}

