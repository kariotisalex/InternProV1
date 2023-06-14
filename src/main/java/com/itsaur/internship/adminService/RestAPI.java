package com.itsaur.internship.adminService;

import com.itsaur.internship.ReadResult;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RestAPI {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        HttpServer server = vertx.createHttpServer();
        ReadClass readClass = new ReadClass(vertx);

        router
                .get("/showAll")
                .handler(v -> {
                    readClass.showAll(ReadResult.getPathUser(0));
                    v.end();
                });

        router
                .get("/showAllFromPath")
                .handler(BodyHandler.create())
                .handler(v -> {
                    JsonObject jsonObject = v.body().asJsonObject();
//                    "/home/kariotis@ad.itsaur.com/Downloads/u2.bin"
                    System.out.println(jsonObject.getString("path"));
                    readClass.showAllFromPath(jsonObject.getString("path"));
                    v.end();
                });

        router
                .get("/showAllFromFile")
                .handler(BodyHandler.create())
                .handler(v -> {
                    JsonObject jsonObject = v.body().asJsonObject();
                    String path = String.valueOf(Paths.get("src/main/java/com/itsaur/internship/",
                                                           jsonObject.getString("path")).toAbsolutePath());

                    System.out.println(path);
                    readClass.showAllFromPath(path);
                    v.end();
                });

        router
                .get("/createBinary/:records")
                .handler(BodyHandler.create())
                .handler(v ->{

                    int records = Integer.valueOf(v.pathParam("records"));
                    String jsonPath = v.body().asJsonObject().getString("path");
                    String path = String.valueOf(Paths.get("src/main/java/com/itsaur/internship/u2.bin").toAbsolutePath());
                    System.out.println(jsonPath);
                    if (jsonPath != null || jsonPath != ""){
                        new CreateUsersInBinary(vertx)
                                .generate(path,records)
                                .onSuccess(ctx -> {
                                    v.response().setStatusCode(200).end();
                                })
                                .onFailure(ctx -> {
                                    v.response().setStatusCode(400).end();
                                });
                    }else {
                        new CreateUsersInBinary(vertx)
                                .generate(jsonPath , records)
                                .onSuccess(ctx -> {
                                    v.response().setStatusCode(200).end();
                                })
                                .onFailure(ctx -> {
                                    v.response().setStatusCode(400).end();
                                });
                    }

                });

        server.requestHandler(router).listen(8081);
    }
}
