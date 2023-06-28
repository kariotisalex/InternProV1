package com.itsaur.internship.Post;

import com.itsaur.internship.User.User;
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
