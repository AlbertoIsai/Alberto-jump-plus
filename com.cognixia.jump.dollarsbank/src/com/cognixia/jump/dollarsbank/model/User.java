package com.cognixia.jump.dollarsbank.model;

import com.cognixia.jump.dollarsbank.utils.UserManager;

public class User {
    private String password;
	private String[] transactionHistory;
	private double balance;
	private String customerName;
	private String customerAddress;
	private String contactNumber;
	private String userId;

    public User(String customerName, String customerAddress, String contactNumber, String userId, String password, Double balance, String[] transactionHistory) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.contactNumber = contactNumber;
        this.userId = userId;
        this.password = password;
        this.balance = balance;
        this.transactionHistory = transactionHistory;
    }

    public String getPassword() {
        return password;
    }

    public boolean transfer(String recipientUserId, double amount) {
        User recipient = UserManager.getUser(recipientUserId);

        if (recipient == null) {
            System.out.println("Recipient not found.");
            return false;
        }

        if (this.getBalance() < amount) {
            System.out.println("Insufficient funds.");
            return false;
        }

        this.withdraw(amount);
        recipient.deposit(amount);
        this.addToTransactionHistory(String.format("Transferred $%.2f to %s.", amount, recipientUserId));
        recipient.addToTransactionHistory(String.format("Received $%.2f from %s.", amount, this.getUserId()));
        return true;
    }

    public void addToTransactionHistory(String transaction) {
        // Retrieve the existing transaction history
        String[] transactions = this.getTransactionHistory();

        // Shift the transaction history array to make room for the new transaction
        for (int i = transactions.length - 2; i >= 0; i--) {
            transactions[i + 1] = transactions[i];
        }

        // Add the new transaction to the first index of the transaction history array
        transactions[0] = transaction;

        // Set the updated transaction history in the user object
        this.setTransactionHistory(transactions);
    }


    public String[] getTransactionHistory() {
        return this.transactionHistory;
    }

    public boolean deposit(double amount) {
        // Increase the user's balance by the deposit amount
        this.balance += amount;

        // Add the deposit transaction to the transaction history
        String transaction = "Deposit: +" + amount + " USD";
        this.addToTransactionHistory(transaction);
		return true;
    }


    public boolean withdraw(double amount) {
        // Check if the user has enough balance to make the withdrawal
        if (this.balance < amount) {
            System.out.println("Insufficient funds.");
            return false;
        }

        // Decrease the user's balance by the withdrawal amount
        this.balance -= amount;

        // Add the withdrawal transaction to the transaction history
        String transaction = "Withdrawal: -" + amount + " USD";
        this.addToTransactionHistory(transaction);

        return true;
    }


    public double getBalance() {
        return this.balance;
    }


	public void setTransactionHistory(String[] transactions) {
		this.transactionHistory = transactions;
	}

	public char[] getCustomerInfo() {
	    // Concatenate the user's name, account number, and balance into a string
	    String customerInfo = "Customer Name: " + this.customerName + "\nCustomer Address: " + this.customerAddress + "\nContact Number: " + this.contactNumber + "\nUser Id: " + this.userId + "\tBalance: " + this.balance + "\n";

	    // Convert the string to a char array and return it
	    return customerInfo.toCharArray();
	}
	
	public static User fromString(String userString) {
	    // Split the user string into an array of fields
	    String[] fields = userString.split(",");

	    // Create a new user object and set its fields from the array
	    User user = new User(fields[0], fields[1], fields[2], fields[3], fields[4], Double.valueOf(fields[5]),fields[6].split(";"));

	    return user;
	}
	@Override
	public String toString() {
	    return this.customerName + "," + this.customerAddress + "," + this.contactNumber + ","  + this.userId + ","  + this.password + "," +  this.balance + "," + String.join(";", this.transactionHistory);
	}

	public String getUserId() {
		return this.userId;
	}

}

