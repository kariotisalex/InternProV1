package com.itsaur.internship.comment.query;

import com.itsaur.internship.post.query.PostQueryModel;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.List;
import java.util.UUID;

public interface CommentQueryModelStore {

    public Future<JsonArray> findAllByPostId(UUID postid);

    public Future<String> countAllCommentsByPid(String pid);

    public Future<JsonArray> findCommentPageByUid(UUID uid, int startWith, int endTo);




}
