package main.java.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/student_gradebook";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "pass";

    private static Connection connection;

    private Database() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("Could not find database driver: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Could not connect to database: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Unable to close database connection: " + e.getMessage());
        }
    }

}

