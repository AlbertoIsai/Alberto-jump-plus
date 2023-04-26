package main.java.view;

import main.java.controller.Controller;
import main.java.model.*;
import main.java.model.dao.ClassroomDAO;
import main.java.model.dao.GradesDAO;
import main.java.model.dao.StudentDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static main.java.model.dao.ClassroomDAO.getClassroomNameById;

public class Menu {
    private final Scanner scanner;
    private final Controller controller;

    public Menu(Scanner scanner, Controller controller) {
        this.scanner = scanner;
        this.controller = controller;
    }

    public void displayMainMenu() {
        while (true) {
            System.out.println("Welcome to the Student Grade Book!");
            System.out.println("1. Login");
            System.out.println("2. Register as a teacher");
            System.out.println("3. Register as a student");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> login();
                case 2 -> registerTeacher();
                case 3 -> registerStudent();
                case 4 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void login() {
        System.out.println("Login");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        try {
            User user = controller.login(username, password);
            if (user instanceof Teacher teacher) {
                displayTeacherMenu(teacher);
            } else if (user instanceof Student student) {
                displayStudentMenu(student);
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
    }

    public void registerTeacher() {
        System.out.println("Register a new teacher");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        Teacher teacher = new Teacher(username, password);
        try {
            boolean success = controller.register(teacher);
            if (success) {
                System.out.println("Registration successful.");
            } else {
                System.out.println("Registration failed.");
            }
        } catch (SQLException e) {
            System.err.println("Error registering teacher: " + e.getMessage());
        }
    }

    public void registerStudent() {
        System.out.println("Register a new student");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        Student student = new Student(username, password);
        try {
            boolean success = controller.register(student);
            if (success) {
                System.out.println("Registration successful.");
            } else {
                System.out.println("Registration failed.");
            }
        } catch (SQLException e) {
            System.err.println("Error registering student: " + e.getMessage());
        }
    }

    public void displayTeacherMenu(Teacher teacher) throws SQLException {
        while (true) {
            System.out.println("\nTeacher Menu:");
            System.out.println("1. View Classes");
            System.out.println("2. Create New Class");
            System.out.println("3. Logout");
            System.out.print("Please enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewClasses(teacher);
                case 2 -> createNewClass(teacher);
                case 3 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewClasses(Teacher teacher) throws SQLException {
        System.out.println("\nClasses:");
        List<String> classrooms = Controller.getClassroomNamesByTeacherId(Controller.getIdByUsername(teacher.getUsername()));
        for (int i = 0; i < classrooms.size(); i++) {
            System.out.println((i + 1) + ". " + classrooms.get(i));
        }

        System.out.println("Enter the number of the class to view students, or 0 to go back:");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= classrooms.size()) {
            viewStudentsInClass(ClassroomDAO.getClassroomIdByClassName(classrooms.get(choice - 1)));
        }
    }

    private void createNewClass(Teacher teacher) throws SQLException {
        System.out.println("Enter the name of the new class:");
        String className = scanner.nextLine();
        ClassroomDAO.addClassroom(className, Controller.getIdByUsername(teacher.getUsername()));
        System.out.println("New class created: " + className);
    }


    public void displayStudentMenu(Student student) throws SQLException {
        while (true) {
            System.out.println("\nStudent Menu:");
            System.out.println("1. View Grades");
            System.out.println("2. Logout");
            System.out.print("Please enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewGrades(Objects.requireNonNull(StudentDAO.getStudentByUsername(student.getUsername())).getId());
                case 2 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void viewGrades(int studentId) throws SQLException {
        List<Grade> grades = GradesDAO.getGradesByStudent(studentId);
        if (grades.isEmpty()) {
            System.out.println("No grades found for student " + studentId);
        } else {
            System.out.println("Grades for student " + studentId);
            for (Grade grade : grades) {
                System.out.println("Grade for "+getClassroomNameById(grade.classroomId())+" class : " + grade.grade());
            }
        }
    }


    private void addStudentToClass(int classroomId) {
        System.out.print("Enter the student's username: ");
        String addUsername = scanner.nextLine();
        try {
            controller.addStudentToClassroom(addUsername, classroomId);
            System.out.println("Student added to class.");
        } catch (SQLException e) {
            System.err.println("Error adding student to class: " + e.getMessage());
        }
    }

    private void removeStudentFromClass(int classroomId) {
        System.out.print("Enter the student's username: ");
        String removeUsername = scanner.nextLine();
        try {
            controller.removeStudentFromClassroom(removeUsername, classroomId);
            System.out.println("Student removed from class.");
        } catch (SQLException e) {
            System.err.println("Error removing student from class: " + e.getMessage());
        }
    }

    public void run() {
        displayMainMenu();
    }

    private void viewStudentsInClass(int classroomId) throws SQLException {
        while (true) {
            System.out.println("\nStudents:");

            List<Student> students = controller.getStudentsByClassroomId(classroomId);
            for (Student student : students) {
                Grade grade = controller.getGradeByStudentAndClassroomId(student.getId(), classroomId);
                System.out.println(student.getUsername() + " - Grade: " + grade.grade());
            }

            System.out.println("\nOptions:");
            System.out.println("1. Find average grade");
            System.out.println("2. Find median grade");
            System.out.println("3. Sort students by name");
            System.out.println("4. Sort students by grade");
            System.out.println("5. Update student grade");
            System.out.println("6. Add student to class");
            System.out.println("7. Remove student from class");
            System.out.println("8. Go back");
            System.out.print("Please enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> System.out.println("Average grade: " + controller.calculateAverageGrade(classroomId));
                case 2 -> System.out.println("Median grade: " + controller.calculateMedianGrade(classroomId));
                case 3 -> {
                    controller.sortStudentsByName(classroomId);
                    System.out.println("Students sorted by name.");
                }
                case 4 -> {
                    controller.sortStudentsByGrade(classroomId);
                    System.out.println("Students sorted by grade.");
                }
                case 5 -> {
                    System.out.print("Enter the student's username: ");
                    String username = scanner.nextLine();
                    int studentId = Controller.getStudentIdByUsername(username);
                    if (studentId != -1) {
                        System.out.print("Enter the new grade (0-100): ");
                        double newGrade = scanner.nextDouble();
                        scanner.nextLine();

                        if (newGrade >= 0 && newGrade <= 100) {
                            controller.updateStudentGrade(studentId, classroomId, newGrade);
                            System.out.println("Student grade updated.");
                        } else {
                            System.out.println("Invalid grade. Please enter a number between 0 and 100.");
                        }
                    } else {
                        System.out.println("Student not found.");
                    }
                }
                case 6 -> addStudentToClass(classroomId);
                case 7 -> removeStudentFromClass(classroomId);
                case 8 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

