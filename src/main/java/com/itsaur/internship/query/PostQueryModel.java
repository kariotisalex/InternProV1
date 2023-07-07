package com.itsaur.internship.query;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostQueryModel(UUID postid, LocalDateTime createdDate,String description, UUID userid, String username, int numOfComments) {
}
