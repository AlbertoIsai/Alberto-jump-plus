package main.java.controller;

import main.java.model.dao.ClassroomDAO;
import main.java.utils.Database;
import main.java.model.dao.GradesDAO;
import main.java.model.dao.StudentDAO;
import main.java.model.Grade;
import main.java.model.Student;
import main.java.model.Teacher;
import main.java.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private static StudentDAO studentDAO = new StudentDAO();
    private final ClassroomDAO classroomDAO;
    private final GradesDAO gradesDAO;

    public Controller(){
        this.classroomDAO = new ClassroomDAO();
        studentDAO = new StudentDAO();
        this.gradesDAO = new GradesDAO();
    }

    public static int getIdByUsername(String username) throws SQLException {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("No user found with the given username.");
                }
            }
        }
    }

    public static List<String> getClassroomNamesByTeacherId(int teacherId) throws SQLException {
        List<String> classroomNames = new ArrayList<>();
        String query = "SELECT class_name FROM classrooms WHERE teacher_id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, teacherId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("class_name");
                classroomNames.add(name);
            }
        }
        return classroomNames;
    }

    public static int getStudentIdByUsername(String username) throws SQLException {
        return studentDAO.getStudentIdByUsername(username);
    }

    public List<Student> getStudentsByClassroomId(int classroomId) throws SQLException {
        return studentDAO.getStudentsByClassroomId(classroomId);
    }

    public Grade getGradeByStudentAndClassroomId(int studentId, int classroomId) throws SQLException {
        return GradesDAO.getGradeByStudentAndClassroomId(studentId, classroomId);
    }

    public double calculateAverageGrade(int classroomId) throws SQLException {
        return gradesDAO.calculateAverageGrade(classroomId);
    }

    public double calculateMedianGrade(int classroomId) throws SQLException {
        return gradesDAO.calculateMedianGrade(classroomId);
    }

    public void sortStudentsByName(int classroomId) throws SQLException {
        studentDAO.sortStudentsByName(classroomId);
    }

    public void sortStudentsByGrade(int classroomId) throws SQLException {
        studentDAO.sortStudentsByGrade(classroomId);
    }

    public void updateStudentGrade(int studentId, int classroomId, double newGrade) throws SQLException {
        gradesDAO.updateStudentGrade(studentId, classroomId, newGrade);
    }

    public void removeStudentFromClassroom(String username, int classroomId) throws SQLException {
        String studentId = String.valueOf(getIdByUsername(username));
        if (!studentId.equals("-1")) {
            ClassroomDAO classroomDAO = new ClassroomDAO();
            classroomDAO.removeStudentFromClassroom(String.valueOf(Integer.parseInt(studentId)), classroomId);
        } else {
            System.out.println("Student not found.");
        }
    }

    public User login(String username, String password) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User.UserType userType = User.UserType.valueOf(resultSet.getString("user_type"));
                    return switch (userType) {
                        case TEACHER -> new Teacher(resultSet.getString("username"), resultSet.getString("password"));
                        case STUDENT -> new Student(resultSet.getString("username"), resultSet.getString("password"));
                    };
                }
            }
        }
        return null;
    }

    public boolean register(User user) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password, user_type) VALUES (?, ?, ?)")) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getUserType().name());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }


    public void addStudentToClassroom(String username, int classroomId) throws SQLException {
        int studentId = studentDAO.getStudentIdByUsername(username);
        if (studentId != -1) {
            classroomDAO.addStudentToClassroom(studentId, classroomId);
            System.out.println("Student added to class.");
        } else {
            System.out.println("Student not found.");
        }
    }

}

