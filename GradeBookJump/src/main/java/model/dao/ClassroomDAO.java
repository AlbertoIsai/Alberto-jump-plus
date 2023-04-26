package main.java.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.controller.Controller;
import main.java.utils.Database;


public class ClassroomDAO {

    public static String getClassroomNameById(int classroomId) throws SQLException {
        String query = "SELECT class_name FROM classrooms WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, classroomId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("class_name");
            } else {
                return null;
            }
        }
    }

    public static void addClassroom(String className, int teacherId) throws SQLException {
        String query = "INSERT INTO classrooms (class_name, teacher_id) VALUES (?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, className);
            statement.setInt(2, teacherId);

            statement.executeUpdate();
        }
    }

    public void removeStudentFromClassroom(String username, int classroomId) throws SQLException {
        // Get the student ID by their username
        int studentId = Controller.getStudentIdByUsername(username);

        // If the student ID is valid, remove them from the classroom
        if (studentId != -1) {
            String query = "DELETE FROM classroom_students WHERE student_id = ? AND classroom_id = ?";
            try (PreparedStatement ps = Database.getConnection().prepareStatement(query)) {
                ps.setInt(1, studentId);
                ps.setInt(2, classroomId);
                ps.executeUpdate();
            }
        } else {
            throw new SQLException("Student not found.");
        }
    }

    public void addStudentToClassroom(int studentId, int classroomId) throws SQLException {
        String sql = "INSERT INTO classroom_students (classroom_id, student_id) VALUES (?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, classroomId);
            statement.setInt(2, studentId);
            statement.executeUpdate();
            System.out.println("Added Student");
        }
    }

    public static int getClassroomIdByClassName(String className) throws SQLException {
        String query = "SELECT id FROM classrooms WHERE class_name = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, className);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return -1;
            }
        }
    }

}
