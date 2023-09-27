package com.itsaur.internship.post.query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PostQueryModel(
        Long count,
        List<PostsQueryModel> postsQueryModels)  {
    public record PostsQueryModel(UUID postid,
                                 OffsetDateTime createdDate,
                                 String filename,
                                 String description,
                                 UUID userid,
                                 String username)  {
    }

}
