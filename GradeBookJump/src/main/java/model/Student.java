package main.java.model;

public class Student extends User {
    private int id;

    public Student(int id, String username, String password) {
        super(username, password, UserType.STUDENT);
        this.id = id;
    }

    public Student(String username, String password) {
        super(username, password, UserType.STUDENT);
    }

    public int getId() {
        return id;
    }

}
