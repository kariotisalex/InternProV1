package com.itsaur.internship.comment.query;

import com.fasterxml.jackson.annotation.JsonFormat;

public record CommentQueryModel (

        String commentid,
        String createdate,
        String comment,
        String userid,
        String username,
        String postid
){
}
