package com.itsaur.internship.user;
import com.itsaur.internship.comment.CommentStore;
import com.itsaur.internship.follower.FollowerService;
import com.itsaur.internship.follower.FollowerStore;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.post.PostStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserService {

    Vertx vertx;
    private PostStore postStore;
    private UsersStore usersStore;
    private CommentStore commentStore;
    private FollowerStore followerStore;

    public UserService( Vertx vertx, PostStore postStore, UsersStore usersStore, CommentStore commentStore,FollowerStore followerStore) {
        this.vertx         = vertx;
        this.postStore     = postStore;
        this.usersStore    = usersStore;
        this.commentStore  = commentStore;
        this.followerStore = followerStore;
    }


    public Future<Void> register(String username, String password) {
         return usersStore.findUserByUsername(username)
                 .otherwiseEmpty()
                 .compose(q -> {
                    if (q == null)
                        return usersStore.insert(new User(UUID.randomUUID(), OffsetDateTime.now(),null, username, password));
                    else
                        return Future.failedFuture(new IllegalArgumentException("User exists!"));
                 });
    }


    public Future<User> login(String username, String password){
        return usersStore.findUserByUsername(username)
                .onFailure(e -> {
                    System.out.println(e);
                }).otherwiseEmpty()
                .compose(user -> {
                    if (user == null){
                        return Future.failedFuture(new IllegalArgumentException("Invalid Username!"));
                    }else{
                        if (user.isPasswordEqual(password)){
                            return Future.succeededFuture(user);
                        } else {
                            return Future.failedFuture(new IllegalArgumentException("Invalid Password!"));
                        }
                    }

                });
    }

    public Future<Void> deleteByUserid(UUID userid){

        return usersStore.findUserByUserid(userid)
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(user -> {

                        return new PostService(vertx, postStore,usersStore,commentStore)
                                .deleteAllPosts(userid)
                                .compose(w -> {
                                    return new FollowerService(followerStore)
                                            .deleteAllFollows(userid)
                                            .compose(q -> {
                                                return usersStore.delete(userid);
                                            });
                                });

                    });
    }


    public Future<Void> changePassword(UUID userid, String currentPassword, String newPassword){
        return usersStore.findUserByUserid(userid)
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(user -> {
                    if (user.isPasswordEqual(currentPassword)){

                        return usersStore.update(
                                new User(
                                        user.userid(),
                                        user.createdate(),
                                        OffsetDateTime.now(),
                                        user.username(),
                                        newPassword
                                ));
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("Password is wrong."));
                    }
                });
    }
}