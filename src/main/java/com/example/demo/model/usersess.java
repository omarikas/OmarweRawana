package com.example.demo.model;


import java.io.Serializable;
import java.time.LocalDateTime;

public class usersess implements Serializable {
    private String token;
    private User userId;
    private LocalDateTime expiry;

    public usersess(String token, User userId, LocalDateTime expiry) {
        this.token = token;
        this.userId = userId;
        this.expiry = expiry;
    
    }
    public usersess (){

    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public User getUserId() {
        return userId;
    }
    public void setUserId(User userId) {
        this.userId = userId;
    }
    public LocalDateTime getExpiry() {
        return expiry;
    }
    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }
  
}
