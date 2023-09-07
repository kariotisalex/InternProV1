package com.itsaur.internship.comment.query;

public record CommentQueryModel (
        String commentid,
        String createdate,
        String comment,
        String userid,
        String username,
        String postid
){
}
