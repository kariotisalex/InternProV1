package com.itsaur.internship.adminForBinary;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class RestAPI {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        HttpServer server = vertx.createHttpServer();

        ReadClass readClass = new ReadClass(vertx);

        router
                .get("/showAll")
                .handler(v -> {
                    readClass.showAll("/home/kariotis@ad.itsaur.com/IdeaProjects/RevisionV1/src/main/java/RestAPI/users.bin");
                    v.end();
                });

        router
                .get("/showAllResult")
                .handler(v -> {
                    readClass.showAllResult( "/home/kariotis@ad.itsaur.com/IdeaProjects/RevisionV1/src/main/java/RestAPI/users22.bin");
                    v.end();
                });

        router
                .get("/createBinary/:records")
                .handler(v ->{
                    int records = Integer.valueOf(v.pathParam("records"));
                    "/home/kariotis@ad.itsaur.com/Downloads/u2.bin"
                });

        server.requestHandler(router).listen(8081);
    }
}
