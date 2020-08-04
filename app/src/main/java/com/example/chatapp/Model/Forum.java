package com.example.chatapp.Model;

public class Forum {

    private String id;
    private String userId;
    private String message;

    public Forum() {}

    public Forum(String id, String userId, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
