package com.itsaur.internship.user;
import io.vertx.core.Future;

public class UserService {

    public UsersStore usersStore;

    public UserService(UsersStore usersStore) {
        this.usersStore = usersStore;
    }


    public Future<Void> register(String username, String password){
        return usersStore.findUser(username)
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
        return usersStore.findUser(username)
                .onFailure(e -> {
                    System.out.println(e);
                })
                .compose(user -> {
                    if (user.isPasswordEqual(password)){
                        return Future.succeededFuture();
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("Invalid Password"));
                    }
                });
    }

    public Future<Void> delete(String username){
        return usersStore.findUser(username)
                .onFailure(e ->{
                    System.out.println(e);
                })
                .compose(user -> {
                    if(user.isUsernameEqual(username)){
                        usersStore.delete(username);
                        return Future.succeededFuture();
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("User doesn't exist."));
                    }
                });
    }


    public Future<Void> changePassword(String username, String currentPassword, String newPassword){
        return usersStore.findUser(username)
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