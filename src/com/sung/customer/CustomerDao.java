package com.sung.customer;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public interface CustomerDao {

	void addCustomer(Customer customer) throws SQLException;

	void updateCustomer(Customer customer) throws SQLException;

	void deleteCustomer(Customer customer) throws SQLException;

	List<Customer> getCustomers(String status) throws SQLException;

	Customer getCustomerById(int id) throws SQLException;

	Customer getCustomerByUsername(String username) throws SQLException;

	void withdraw(Customer customer, double amount) throws SQLException;

	void deposit(Customer customer, double amount) throws SQLException;

	void push(Customer sender, Customer receiver, double amount) throws SQLException;

	void pull(Customer receiver, Scanner scan) throws SQLException;

}
