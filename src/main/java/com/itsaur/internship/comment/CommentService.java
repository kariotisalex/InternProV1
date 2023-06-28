package com.itsaur.internship.comment;

import io.vertx.core.Future;

public class CommentService {
    public CommentStore commentStore;

    public CommentService(CommentStore commentStore) {
        this.commentStore = commentStore;
    }

    public Future<Void> addComment(String filename, String comment) {
//        return this.contentStore.findImage(filename)
//                .compose(w -> {
//                    return this.contentStore.insertComment(filename, comment);
//                });
        return null;
    }





    public Future<Void> deleteCommment(String commentid){
//        return this.contentStore.deleteComment(commentid);
        return null;
    }

}
