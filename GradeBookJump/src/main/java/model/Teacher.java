package main.java.model;

import java.io.Serializable;

public class Teacher extends User implements Serializable {
    public Teacher(String username, String password) {
        super(username, password, UserType.TEACHER);
    }
}


