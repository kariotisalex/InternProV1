package com.itsaur.internship.comment;

import com.itsaur.internship.post.Post;
import com.itsaur.internship.user.User;

import java.sql.Timestamp;
import java.util.UUID;

public class Comment {

    private UUID commentid;
    private Timestamp date;
    private String comment;
    private User user;
    private Post image;

    public UUID getCommentid() {
        return commentid;
    }

    public void setCommentid(UUID commentid) {
        this.commentid = commentid;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getImage() {
        return image;
    }

    public void setImage(Post image) {
        this.image = image;
    }
}
