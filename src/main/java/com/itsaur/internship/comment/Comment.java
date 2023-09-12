package com.itsaur.internship.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itsaur.internship.post.Post;

import java.time.OffsetDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import java.sql.Timestamp;

public record Comment (
        UUID commentid,
        OffsetDateTime createdate,
        OffsetDateTime updatedate,
        String comment,
        UUID userid,
        UUID postid
){}
