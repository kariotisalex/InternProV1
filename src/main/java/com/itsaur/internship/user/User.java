package com.itsaur.internship.user;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID userid;
    private LocalDateTime createdate;
    private LocalDateTime updatedate;
    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(UUID userid, LocalDateTime createdate, LocalDateTime updatedate, String username, String password) {
        this.userid = userid;
        this.createdate = createdate;
        this.updatedate = updatedate;
        this.username = username;
        this.password = password;
    }

    public User(UUID userid, LocalDateTime createdate, String username, String password) {
        this.userid = userid;
        this.createdate = createdate;
        this.username = username;
        this.password = password;
    }

    public User(UUID userid, String username, String password) {
        this.userid = userid;
        this.username = username;
        this.password = password;
    }

    public LocalDateTime getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(LocalDateTime updatedate) {
        this.updatedate = updatedate;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }

    public UUID getUserid() {
        return userid;
    }

    public void setUserid(java.util.UUID userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUsernameEqual(String username) {
        return this.username.equals(username);
    }

    public boolean isPasswordEqual(String password) {
        return (this.password.equals(password));
    }


    @Override
    public String toString() {
        return "User {" +
                "userid= " + userid + "\n" +
                " username= ' " + username + " \'\n" +
                " password= ' " + password + " \'\n" +
                '}';
    }
}

