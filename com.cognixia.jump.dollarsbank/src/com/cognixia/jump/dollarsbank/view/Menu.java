package com.cognixia.jump.dollarsbank.view;

import java.util.Scanner;

import com.cognixia.jump.dollarsbank.controller.UserController;
import com.cognixia.jump.dollarsbank.model.User;
import com.cognixia.jump.dollarsbank.utils.UserManager;

public class Menu {
	
	@SuppressWarnings("unused")
	public static void runMenu() {
		
    // Color constants
    final String RESET = "\033[0m";  // Text Reset
    final String RED = "\033[0;31m";     // RED
    final String GREEN = "\033[0;32m";   // GREEN
    final String BLUE = "\033[0;34m";    // BLUE
    final String WHITE = "\033[0;37m";   // WHITE

	 Scanner scanner = new Scanner(System.in);
     UserController userController = new UserController();

     while (true) {
    	 System.out.println(BLUE + "+---------------------------------------+\n|\tDollars Bank Welcomes you\t|\n+---------------------------------------+" + RESET);
         System.out.println("1.Create New Account\n2.Login\n3.Exit.\n"+GREEN+"Enter choice (1,2 or 3)"+RESET);
         String choice = scanner.nextLine();

         if (choice.equals("2")) {
             System.out.println("Enter your user ID:");
             String userId = scanner.nextLine();

             System.out.println("Enter your password:");
             String password = scanner.nextLine();

             User currentUser = UserController.loginUser(userId, password);

             if (currentUser != null) {
                 while (currentUser != null) {
                	 System.out.println(BLUE + "+-------------------------------+\n|\tWelcome Customer!!!\t|\n+-------------------------------+" + RESET);
                     System.out.println("1.Deposit Amount\n2.Withdraw\n3.Transfer funds\n4.View 5 Recent Transactions\n5.Display Customer Information\n6.Sign Out");
                     System.out.println(GREEN+"Enter choice (1,2,3,4,5 or 6)"+RESET);
                     String userChoice = scanner.nextLine();

                     switch (userChoice) {
                         case "1":
                             System.out.println("Enter deposit amount:");
                             double depositAmount = Double.parseDouble(scanner.nextLine());
                             currentUser.deposit(depositAmount);
                             UserManager.saveUser(currentUser);
                             break;
                         case "2":
                             System.out.println("Enter withdrawal amount:");
                             double withdrawalAmount = Double.parseDouble(scanner.nextLine());
                             currentUser.withdraw(withdrawalAmount);
                             UserManager.saveUser(currentUser);
                             break;
                         case "3":
                             System.out.println("Enter recipient username:");
                             String recipientUsername = scanner.nextLine();
                             userController.transfer(currentUser.getUserId(), recipientUsername);
                             break;
                         case "4":
                             String[] transactionHistory = currentUser.getTransactionHistory();
                             System.out.println(BLUE + "+-------------------------------+\n|\tTransaction History\t|\n+-------------------------------+" + RESET);

                             for (String transaction : transactionHistory) {
                            	 if(!transaction.equalsIgnoreCase("null")) {
                                 System.out.println(transaction);
                            	 }
                             }
                             break;
                         case "5":
                             char[] customerInfo = currentUser.getCustomerInfo();
                             System.out.println("Customer info:");
                             for (char c : customerInfo) {
                                 System.out.print(c);
                             }
                             break;
                         case "6":
                        	 currentUser = null;
                             System.out.println("Logged out.");
                             break;
                         default:
                             System.out.println("Invalid input. Please enter a number from 1-6.");
                     }
                 }
             } else {
                 System.out.println(RED + "Invalid Credentials. Try Again!" + RESET);
             }
         } else if (choice.equals("1")) {
             System.out.println(BLUE + "+---------------------------------------+\n|\tEnter Details for New Account\t|\n+---------------------------------------+" + RESET);
             System.out.println("Customer Name:");
             String customerName = scanner.nextLine();
             
             System.out.println("Customer Address:");
             String customerAddress = scanner.nextLine();

             System.out.println("Contact Number:");
             String contactNumber = scanner.nextLine();
             
             System.out.println("UserId:");
             String userId = scanner.nextLine();

             System.out.println("Password:");
             String password = scanner.nextLine();
             
             System.out.println("Initial Deposit Amount:");
             String depositAmount = scanner.nextLine();


             boolean newUser = userController.registerUser(customerName, customerAddress, contactNumber, userId, password, depositAmount);

             if (newUser) {
                 System.out.println(GREEN+"Account created successfully!"+RESET);
             } else {
                 System.out.println(RED+"Account creation failed. Try again!"+RESET);
             }
         } else if (choice.equals("3")) {
             System.out.println("Goodbye!");
             break;
         } else {
             System.out.println("Invalid input. Please enter a number from 1-3.");
         }
     }

     scanner.close();
 }
}
