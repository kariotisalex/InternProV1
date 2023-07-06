package com.itsaur.internship.comment;

import com.itsaur.internship.post.Post;

import java.time.LocalDateTime;
import java.util.UUID;

import java.sql.Timestamp;

public class Comment {

    private UUID commentid;
    private LocalDateTime createdate;
    private LocalDateTime updatedate;
    private String comment;
    private UUID userid;
    private UUID postid;

    public UUID getCommentid() {
        return commentid;
    }

    public Comment(UUID commentid, LocalDateTime createdate, LocalDateTime updatedate, String comment, UUID userid, UUID postid) {
        this.commentid = commentid;
        this.createdate = createdate;
        this.updatedate = updatedate;
        this.comment = comment;
        this.userid = userid;
        this.postid = postid;
    }

    public Comment(UUID commentid, LocalDateTime createdate, String comment, UUID userid, UUID postid) {
        this.commentid = commentid;
        this.createdate = createdate;
        this.comment = comment;
        this.userid = userid;
        this.postid = postid;
    }

    public Comment(UUID commentid, String comment) {
        this.commentid = commentid;
        this.comment = comment;
    }

    public void setCommentid(UUID commentid) {
        this.commentid = commentid;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }

    public LocalDateTime getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(LocalDateTime updatedate) {
        this.updatedate = updatedate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UUID getUserid() {
        return userid;
    }

    public void setUserid(UUID userid) {
        this.userid = userid;
    }

    public UUID getPostid() {
        return postid;
    }

    public void setPostid(UUID postid) {
        this.postid = postid;
    }
}
