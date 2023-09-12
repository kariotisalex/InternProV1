package com.itsaur.internship.post.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;

import java.time.OffsetDateTime;


public record PostQueryModel(String postid,
                             String createdDate,
                             String filename,
                             String description,
                             String userid,
                             String username)  {
}
