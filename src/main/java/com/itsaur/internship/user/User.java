package com.itsaur.internship.user;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.UUID;

public record User(
        UUID userid,
        OffsetDateTime createdate,
        OffsetDateTime updatedate,
        String username,
        String password
) {



    public boolean isUsernameEqual(String username) {
        return this.username.equals(username);
    }

    public boolean isPasswordEqual(String password) {
        return (this.password.equals(password));
    }

    public boolean isUseridEqual(UUID userid){
        return this.userid.equals(userid);
    }

}

