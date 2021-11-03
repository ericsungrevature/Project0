package com.sung.customer;

import com.sung.user.User;

public class Customer extends User {

	private double balance;
	private String status;

	public Customer() {}

	public double getBalance() {return balance;}

	public void setBalance(double balance) {this.balance = balance;}

	public String getStatus() {return status;}

	public void setStatus(String status) {this.status = status;}

}
