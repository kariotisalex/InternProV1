package com.itsaur.internship.user;

import java.time.LocalDateTime;

public class UUID {

    private java.util.UUID personid;
    private LocalDateTime createdate;
    private String username;
    private String password;

    public UUID() {
    }

    public UUID(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UUID(java.util.UUID personid, LocalDateTime createdate, String username, String password) {
        this.personid = personid;
        this.createdate = createdate;
        this.username = username;
        this.password = password;
    }

    public UUID(java.util.UUID personid, String username, String password) {
        this.personid = personid;
        this.username = username;
        this.password = password;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }

    public java.util.UUID getUserid() {
        return personid;
    }

    public void setPersonid(java.util.UUID personid) {
        this.personid = personid;
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

    public LocalDateTime initCreateDate(){
        this.createdate = LocalDateTime.now();
        return this.createdate;
    }

    @Override
    public String toString() {
        return "User {" +
                "personid= " + personid + "\n" +
                " username= ' " + username + " \'\n" +
                " password= ' " + password + " \'\n" +
                '}';
    }
}

