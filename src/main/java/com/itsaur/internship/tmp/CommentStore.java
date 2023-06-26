package com.itsaur.internship.tmp;

import com.itsaur.internship.Comment;
import io.vertx.core.Future;

import java.util.UUID;

public interface CommentStore {

    public Future<Void> insert(Comment comment);

    public Future<Comment> find(UUID commentid);

    public Future<Void> update(UUID commentid);

    public Future<Void> delete(Comment comment);

}
