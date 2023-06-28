package com.itsaur.internship.post;

import com.itsaur.internship.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class Post {

    private UUID imageid;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String filename;
    private String description;
    private User user;

    // For Storing
    public Post(UUID imageid, LocalDateTime createdDate, LocalDateTime updatedDate, String filename, String description, User user) {
        this.imageid = imageid;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.filename = filename;
        this.description = description;
        this.user = user;
    }

    // For Creating
    public Post(String filename, String description, User user) {
        this.imageid = UUID.randomUUID();
        this.filename = filename;
        this.description = description;
        this.user = user;
    }

    public UUID getImageid() {
        return imageid;
    }

    public void setImageid(UUID imageid) {
        this.imageid = imageid;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime initCreateDate(){
        this.createdDate = LocalDateTime.now();
        return this.createdDate;
    }
}
