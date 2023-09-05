package com.itsaur.internship.post.query;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostQueryModel(UUID postid,
                             LocalDateTime createdDate,
                             String filename,
                             String description,
                             UUID userid) {
}
