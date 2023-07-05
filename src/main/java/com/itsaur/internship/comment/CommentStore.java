package com.itsaur.internship.comment;

import io.vertx.core.Future;

import java.util.List;
import java.util.UUID;

public interface CommentStore {

    public Future<Void> insert(Comment comment);

    public Future<Comment> findById(UUID commentid);

    public Future<Void> update(Comment comment);

    public Future<Void> deleteById(UUID commentid);

    public Future<Void> deleteByPostid(UUID postid);

    public Future<List<Comment>> readAllById(UUID commentid);

}
