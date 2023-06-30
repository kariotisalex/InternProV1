package com.itsaur.internship.user;
import com.itsaur.internship.comment.CommentStore;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.post.PostStore;
import io.vertx.core.Future;

public class UserService {

    private PostStore postStore;
    private UsersStore usersStore;
    private CommentStore commentStore;
    public UserService( PostStore postStore, UsersStore usersStore, CommentStore commentStore) {
        this.postStore = postStore;
        this.usersStore = usersStore;
        this.commentStore = commentStore;
    }


    public Future<Void> register(String username, String password){
        return usersStore.findUserByUsername(username)
                .onFailure(e -> {
                    System.out.println(e);
                })
                .otherwiseEmpty()
                .compose(user -> {
                    if (user == null){
                        return usersStore.insert(new User(username,password));
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("User exists!"));
                    }
                });
    }


    public Future<User> login(String username, String password){
        return usersStore.findUserByUsername(username)
                .onFailure(e -> {
                    System.out.println(e);
                })
                .compose(user -> {
                    if (user.isPasswordEqual(password)){
                        return Future.succeededFuture(user);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("Invalid Password"));
                    }
                });
    }

    public Future<Void> deleteByUsername(String username){
        return usersStore.findUserByUsername(username)
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(user -> {
                    if(user.isUsernameEqual(username)){
                        return new PostService(postStore,usersStore,commentStore).deleteAllPosts(username)
                                .compose(q -> {
                                    return usersStore.delete(username);
                                });
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("User doesn't exist."));
                    }
                });
    }


    public Future<Void> changePassword(String username, String currentPassword, String newPassword){
        return usersStore.findUserByUsername(username)
                .onFailure(e -> {
                    System.out.println(e);
                })
                .compose(user -> {
                    if (user.isPasswordEqual(currentPassword)){
                        return usersStore.changePassword(username, newPassword);
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("The password is wrong."));
                    }
                });
    }
}