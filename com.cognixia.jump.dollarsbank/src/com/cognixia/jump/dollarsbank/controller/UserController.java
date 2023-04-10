package com.cognixia.jump.dollarsbank.controller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.cognixia.jump.dollarsbank.model.User;
import com.cognixia.jump.dollarsbank.utils.UserManager;

public class UserController {
    private static Scanner scanner = new Scanner(System.in);

    public static void deposit(User user) {
        System.out.println("How much would you like to deposit?");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume the newline character

        if (user.deposit(amount)) {
        	UserManager.saveUser(user);
            System.out.println("Deposit successful!");
        } else {
            System.out.println("Deposit failed. Please try again.");
        }
    }

    public static void withdraw(User user) {
        System.out.println("How much would you like to withdraw?");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume the newline character

        if (user.withdraw(amount)) {
        	UserManager.saveUser(user);
            System.out.println("Withdrawal successful!");
        } else {
            System.out.println("Withdrawal failed. Please try again.");
        }
    }

    public static void transfer(String username, String recipientUsername) {
    	
    	User recipient = UserManager.getUser(recipientUsername);
    	User user = UserManager.getUser(recipientUsername);

        System.out.println("How much would you like to transfer?");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume the newline character

        if (user.transfer(recipientUsername, amount)) {
        	recipient.deposit(amount);
            recipient.addToTransactionHistory(String.format("Received $%.2f from %s.", amount, user.getUserId()));
        	UserManager.saveUser(user);
        	UserManager.saveUser(recipient);
            System.out.println("Transfer successful!");
        } else {
            System.out.println("Transfer failed. Please try again.");
        }
    }
    
    public static User loginUser(String userId, String password) {
        try (// Create a BufferedReader to read from the user file
		BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            // Read the file line by line to find a match for the provided credentials
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String storedCustomerName = parts[0];
                String storedCustomerAddress = parts[1];
                String storedContactNumber = parts[2];
                String storedUserId = parts[3];
                String storedPassword = parts[4];
                String storedBalance = parts[5];
                String[] storedTransactions = parts[6].split(";");

                if (userId.equals(storedUserId) && password.equals(storedPassword)) {
                    // Return the user object if a match is found
                    return new User(storedCustomerName, storedCustomerAddress, storedContactNumber, storedUserId, storedPassword, Double.valueOf(storedBalance), storedTransactions);
                }
            }

            // Close the reader
            reader.close();
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
        } catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Return null if no match is found
        return null;
    }
    
    public boolean registerUser(String customerName, String customerAddress, String contactNumber, String userId, String password, String depositAmount) {
        try {
            // Create a BufferedWriter to write to the user file
            BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true));

            String[] transactionHistory = new String[5];
            transactionHistory[0] = "Account opened with " + depositAmount + "USD";
            // Create a new user object
            User user = new User(customerName, customerAddress, contactNumber, userId, password, Double.parseDouble(depositAmount), transactionHistory);

            // Write the user details to the file
            writer.write(user.toString());
            writer.newLine();

            // Close the writer
            writer.close();

            // Return true if registration is successful
            return true;
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
            return false;
        }
    }
}

