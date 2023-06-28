package com.itsaur.internship.Comment;

import com.itsaur.internship.Post.Post;
import io.vertx.core.Future;

import java.util.UUID;

public interface CommentStore {

    public Future<Void> insert(Comment comment);

    public Future<Comment> find(UUID commentid);

    public Future<Void> update(UUID commentid);

    public Future<Void> delete(UUID commentid);

    public Future<Void> deleteFromPost(Post post);

}
