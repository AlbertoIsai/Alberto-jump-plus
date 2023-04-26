package main.java.model;

import java.io.Serializable;

public class User implements Serializable {

    public enum UserType {
        TEACHER,
        STUDENT
    }

    private final String username;
    private final String password;
    private final UserType userType;

    public User(String username, String password, UserType userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }
}

