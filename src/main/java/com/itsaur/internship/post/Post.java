package com.itsaur.internship.post;


import java.time.LocalDateTime;
import java.util.UUID;

public class Post {

    private UUID postid;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String filename;
    private String description;
    private UUID userid;

    // For Storing
    public Post(UUID postid, LocalDateTime createdDate, LocalDateTime updatedDate, String filename, String description, UUID userid) {
        this.postid = postid;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.filename = filename;
        this.description = description;
        this.userid = userid;
    }

    // For Creating
    public Post(String filename, String description, UUID userid) {
        this.postid = UUID.randomUUID();
        this.filename = filename;
        this.description = description;
        this.userid = userid;
    }

    public UUID getPostid() {
        return postid;
    }

    public void setPostid(java.util.UUID postid) {
        this.postid = postid;
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

    public UUID getUserid() {
        return userid;
    }

    public void setUserid(UUID userid) {
        this.userid = userid;
    }

    public LocalDateTime initCreateDate(){
        this.createdDate = LocalDateTime.now();
        return this.createdDate;
    }
    public LocalDateTime initUpdateDate(){
        this.createdDate = LocalDateTime.now();
        return this.createdDate;
    }
}
