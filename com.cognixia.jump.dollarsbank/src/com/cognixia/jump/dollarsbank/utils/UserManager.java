package com.cognixia.jump.dollarsbank.utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cognixia.jump.dollarsbank.model.User;

public class UserManager {
    private static final String USER_DATA_FILE = "users.txt";

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                User user = new User(userData[0], userData[1], userData[2], userData[3], userData[4], Double.parseDouble(userData[5]),userData[6].split(";"));
                users.add(user);
            }
        } catch (IOException e) {
            System.out.println("Error reading user data file: " + e.getMessage());
        }

        return users;
    }

    public static User getUser(String userId) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public static void saveUser(User user) {
        List<User> users = loadUsers();
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                found = true;
                break;
            }
        }
        if (!found) {
            users.add(user);
        }
        try (FileWriter writer = new FileWriter(USER_DATA_FILE)) {
            for (User u : users) {
                writer.write(u.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving user data file: " + e.getMessage());
        }
    }


}

