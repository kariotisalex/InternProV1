package com.itsaur.internship.post.query;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PostQueryModel(UUID postid,
                             OffsetDateTime createdDate,
                             String filename,
                             String description,
                             UUID userid,
                             String username)  {
}
