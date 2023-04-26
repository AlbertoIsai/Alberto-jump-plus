package main.java.model.dao;

import main.java.model.Grade;
import main.java.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradesDAO {

    public GradesDAO() {
    }


    public static List<Grade> getGradesByStudent(int studentId) throws SQLException {
        String query = "SELECT * FROM grades WHERE student_id = ?";
        List<Grade> grades = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int classroomId = resultSet.getInt("classroom_id");
                double grade = resultSet.getDouble("grade");
                grades.add(new Grade(id, studentId, classroomId, grade));
            }
        }
        return grades;
    }

    public static Grade getGradeByStudentAndClassroomId(int studentId, int classroomId) throws SQLException {
        String query = "SELECT * FROM grades WHERE student_id = ? AND classroom_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(query)) {
            ps.setInt(1, studentId);
            ps.setInt(2, classroomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                double grade = rs.getDouble("grade");
                return new Grade(id, studentId, classroomId, grade);
            } else {
                return new Grade(0, studentId, classroomId, 0.0);
            }
        }
    }


    public double calculateAverageGrade(int classroomId) throws SQLException {
        String query = "SELECT AVG(grade) AS avg_grade FROM grades WHERE classroom_id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, classroomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_grade");
            }
        }
        return 0;
    }

    public double calculateMedianGrade(int classroomId) throws SQLException {
        String query = "SELECT grade FROM grades WHERE classroom_id = ? ORDER BY grade";
        try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, classroomId);
            ResultSet rs = ps.executeQuery();
            List<Double> grades = new ArrayList<>();
            while (rs.next()) {
                double grade = rs.getDouble("grade");
                grades.add(grade);
            }
            int size = grades.size();
            if (size == 0) {
                // handle empty list case
                return 0.0;
            } else if (size % 2 == 0) {
                return (grades.get(size / 2) + grades.get(size / 2 - 1)) / 2;
            } else {
                return grades.get(size / 2);
            }
        }
    }

    public void updateStudentGrade(int studentId, int classroomId, double grade) throws SQLException {
        String selectQuery = "SELECT id FROM grades WHERE student_id = ? AND classroom_id = ?";
        String updateQuery = "UPDATE grades SET grade = ? WHERE id = ?";
        String insertQuery = "INSERT INTO grades (student_id, classroom_id, grade) VALUES (?, ?, ?)";

        try (Connection connection = Database.getConnection()) {
            // Check if a grade for the student and classroom already exists
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, studentId);
            selectStatement.setInt(2, classroomId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Update the existing record
                int id = resultSet.getInt("id");
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setDouble(1, grade);
                updateStatement.setInt(2, id);
                updateStatement.executeUpdate();
            } else {
                // Insert a new record
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setInt(1, studentId);
                insertStatement.setInt(2, classroomId);
                insertStatement.setDouble(3, grade);
                insertStatement.executeUpdate();
            }
        }
    }


}

