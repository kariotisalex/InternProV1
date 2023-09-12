package com.itsaur.internship.post;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Post(
        UUID postid,
        OffsetDateTime createdDate,
        OffsetDateTime updatedDate,
        String filename,
        String description,
        UUID userid
) {}
