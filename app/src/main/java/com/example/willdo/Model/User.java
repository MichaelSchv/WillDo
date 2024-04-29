package com.example.willdo.Model;

import java.io.Serializable;

public class User implements Serializable {
    private String userID;
    private String email;

    public User(String userID, String email) {
        this.userID = userID;
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public User setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
