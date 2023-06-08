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
        router.post("/login")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    final JsonObject body = ctx.body().asJsonObject();
                    this.service.login(body.getString("username"), body.getString("password"))
                            .onSuccess(v -> ctx.response().setStatusCode(200).end())
                            .onFailure(v -> ctx.response().setStatusCode(400).end());
                });
        
        server.requestHandler(router).listen(8080);
    }
}
