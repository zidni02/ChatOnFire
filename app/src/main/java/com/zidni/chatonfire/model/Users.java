package com.zidni.chatonfire.model;

public class Users {
    private String id;
    private String username;
    private String imageURL;
    private String email;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users() {
    }

    public Users(String id, String username, String imageURL, String email, String status) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.email = email;
        this.status = status;
    }
}
