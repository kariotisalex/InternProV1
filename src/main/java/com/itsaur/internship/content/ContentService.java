package com.itsaur.internship.content;

import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;

import java.util.List;

public class ContentService{

    public ContentStore contentStore;

    public ContentService(ContentStore contentStore){
        this.contentStore = contentStore;
    }


    public Future<Void> addPost(String username, String filename, String description) {
        return this.contentStore.findUser(username)
                .compose(q -> {
                    return this.contentStore.insertImage(username, filename, description);
                });
    }


    public Future<Void> addComment(String filename, String comment) {
        return this.contentStore.findImage(filename)
                .compose(w -> {
                    return this.contentStore.insertComment(filename, comment);
                });
    }

    public Future<Void> deletePost(String filename){
        return this.contentStore.findImage(filename)
                .compose(w -> {
                    return contentStore.deleteImage(filename);
                });
    }

    public Future<List<String>> retrieveAllPosts(String username){
        return this.contentStore.retrieveAllImage(username);
    }

    public Future<Void> deleteCommment(String commentid){
        return this.contentStore.deleteComment(commentid);
    }
}
