package main.java.model.dao;

import main.java.model.Grade;
import main.java.model.Student;
import main.java.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StudentDAO {

    private static final String UPDATE_STUDENT_ORDER = "UPDATE users SET student_order = ? WHERE id = ?";


    private Connection getConnection() throws SQLException {
        return Database.getConnection();
    }

    public int getStudentIdByUsername(String username) throws SQLException {
        String query = "SELECT id FROM users WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return -1;
            }
        }
    }

    public List<Student> getStudentsByClassroomId(int classroomId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.id, s.username, s.password FROM users s JOIN classroom_students cs ON s.id = cs.student_id WHERE cs.classroom_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, classroomId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                students.add(new Student(id, username, password));
            }
        }
        return students;
    }

    public static Student getStudentByUsername(String username) throws SQLException {
        String query = "SELECT id, username, password FROM users WHERE username = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String password = resultSet.getString("password");
                return new Student(id, username, password);
            } else {
                return null;
            }
        }
    }

    public void sortStudentsByName(int classroomId) throws SQLException {
        String query = "SELECT * FROM users WHERE id IN (SELECT student_id FROM grades WHERE classroom_id = ?) ORDER BY username ASC";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, classroomId);
            ResultSet rs = statement.executeQuery();
            List<Student> students = new ArrayList<>();
            while (rs.next()) {
                Student student = new Student(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
                students.add(student);
            }
            students.sort(Comparator.comparing(Student::getUsername));
            System.out.println("Sorted students:");
            for (Student student : students) {
                System.out.println(student.getUsername());
            }
        }
    }

    public void sortStudentsByGrade(int classroomId) throws SQLException {
        List<Student> students = getStudentsByClassroomId(classroomId);

        // Use a custom comparator to sort by grade
        Comparator<Student> gradeComparator = Comparator.comparingDouble(s -> {
            try {
                Grade grade = GradesDAO.getGradeByStudentAndClassroomId(s.getId(), classroomId);
                return grade.grade();
            } catch (SQLException e) {
                e.printStackTrace();
                return 0.0;
            }
        });
        students.sort(gradeComparator.reversed());

        // Update the order of the students in the database
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_STUDENT_ORDER)) {
            for (int i = 0; i < students.size(); i++) {
                statement.setInt(1, i + 1);
                statement.setInt(2, students.get(i).getId());
                statement.executeUpdate();
            }
        }
    }

}

