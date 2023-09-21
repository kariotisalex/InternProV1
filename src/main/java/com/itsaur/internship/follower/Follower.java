package com.itsaur.internship.follower;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Follower (
        UUID followid,
        UUID userid,
        OffsetDateTime createdate,
        UUID followerid
){}
