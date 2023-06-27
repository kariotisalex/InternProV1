package com.itsaur.internship.Post;

import com.itsaur.internship.User.User;
import io.vertx.core.Future;
import java.util.List;
import java.util.UUID;

public interface PostStore {

    public Future<Void> insert(Post post);

    public Future<Post> find(String filename);

    public Future<Void> update(UUID personid, String description);
    public Future<Void> delete(String filename);

    public Future<List<String>> retrieveAll(User user);

}
