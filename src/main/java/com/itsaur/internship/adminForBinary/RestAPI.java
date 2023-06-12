package com.itsaur.internship.adminForBinary;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import netscape.javascript.JSObject;

public class RestAPI {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        HttpServer server = vertx.createHttpServer();

        ReadClass readClass = new ReadClass(vertx);

        router
                .get("/showAll")
                .handler(v -> {
                    readClass.showAll("/home/kariotis@ad.itsaur.com/IdeaProjects/kariotis-internship/src/main/java/com/itsaur/internship/users.bin");
                    v.end();
                });

        router
                .get("/showAllSpare")
                .handler(BodyHandler.create())
                .handler(v -> {
                    JsonObject jsonObject = v.body().asJsonObject();
//                    "/home/kariotis@ad.itsaur.com/Downloads/u2.bin"
                    System.out.println(jsonObject.getString("path"));
                    readClass.showAllSpare(jsonObject.getString("path"));
                    v.end();
                });

        router
                .get("/createBinary/:records")
                .handler(v ->{
                    int records = Integer.valueOf(v.pathParam("records"));
                    new CreateUsersInBinary(vertx)
                            .generate("/home/kariotis@ad.itsaur.com/Downloads/u2.bin",records)
                            .onSuccess(ctx -> {
                                v.response().setStatusCode(200).end();
                            })
                            .onFailure(ctx -> {
                                v.response().setStatusCode(400).end();
                            });
                });

        server.requestHandler(router).listen(8081);
    }
}
