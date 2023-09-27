package com.itsaur.internship.comment.query;


import io.vertx.core.Future;
import java.util.List;
import java.util.UUID;

public interface CommentQueryModelStore {

    public Future<CommentQueryModel> findCommentPageByUid(UUID uid, int startWith, int endTo);

}
