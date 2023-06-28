package com.itsaur.internship.post;

import com.itsaur.internship.user.User;
import io.vertx.core.Future;
import java.util.List;

public interface PostStore {

    public Future<Void> insert(Post post);

    public Future<Post> findPost(String filename);

    public Future<Void> updatePost(User user, String description);

    public Future<Void> deletePost(String filename);

    public Future<Void> deleteFromUser(User user);

    public Future<List<String>> retrieveAll(User user);

}
