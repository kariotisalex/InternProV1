package com.itsaur.internship.follower.query;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FollowerQueryModel (
        UUID followid,
        UUID userid,
        String usernameUserid,
        OffsetDateTime createdate,
        UUID followerid,
        String followeridUsername
){}
