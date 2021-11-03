package com.sung.employee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.sung.bank.ConnectionFactory;
import com.sung.customer.Customer;
import com.sung.customer.CustomerDao;
import com.sung.customer.CustomerDaoFactory;

public class EmployeeDaoImpl implements EmployeeDao {

	private Connection connection;
	private CustomerDao cdao = CustomerDaoFactory.getCustomerDao();

	public EmployeeDaoImpl() {
		this.connection = ConnectionFactory.getConnection();
	}

	/*
	 * The method will accept a integer variable
	 * It will retrieve the information from the employee table in MySQL whose id value
	 * is equal to the integer variable and create an Employee from the information
	 * The method will return the Employee object
	 */
	@Override
	public Employee getEmployeeById(int id) throws SQLException {
		Employee employee = new Employee();
		String sql = "select * from employee where id = " + id;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		resultSet.next();
		if (resultSet != null) {
			employee.setId(resultSet.getInt(1));
			employee.setUsername(resultSet.getString(2));
			employee.setPassword(resultSet.getString(3));
		}
		return employee;
	}

	/*
	 * The method will accept a String object
	 * It will retrieve the information from the employee table in MySQL whose username
	 * is equal to the integer variable and create an Employee from the information
	 * The method will return the Employee object
	 */
	@Override
	public Employee getEmployeeByUsername(String username) throws SQLException {
		Employee employee = new Employee();
		String sql = "select * from employee where username = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, username);
		ResultSet resultSet = preparedStatement.executeQuery();
		resultSet.next();
		if (resultSet != null) {
			employee.setId(resultSet.getInt(1));
			employee.setUsername(resultSet.getString(2));
			employee.setPassword(resultSet.getString(3));
		}
		return employee;
	}

	/*
	 * The method will call the getCustomers method in order to acquire a list of
	 * Customer objects with a pending status from the customer table in MySQL
	 * Input will be used to approve or reject each Customer account
	 * If the account is approved the Customer status value will be updated to approved
	 * If the account is rejected the entry in the customer table will be removed
	 */
	@Override
	public void checkAccount() {
		Scanner scan = new Scanner(System.in);
		String input = null;
		try {
			List<Customer> list = cdao.getCustomers("pending");
			if (list.isEmpty()) {
				System.out.println("No pending accounts available at this time");
				scan.close();
				return;
			}
			for (int i = 0; i < list.size(); i++) {
				System.out.printf("Customer: %s, Balance: $%.2f\n", list.get(i).getUsername(), list.get(i).getBalance());
				while(true) {
					System.out.print("Would you like to approve this account? Y / N ");
					input = scan.nextLine();
					if (input.equalsIgnoreCase("Y")) {
						list.get(i).setStatus("approved");
						cdao.updateCustomer(list.get(i));
						break;
					} else if (input.equalsIgnoreCase("N")) {
						cdao.deleteCustomer(list.get(i));
						break;
					} else {
						System.out.println("Invalid Input: " + input);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("Invalid Input: " + input);
			scan.close();
		}
	}

	/*
	 * The method will input a String object and call the getCustomerByUsername method
	 * to acquire a Customer object whose username value corresponds to the input
	 * It will display the Customer information
	 */
	@Override
	public void viewAccount() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter the username: ");
		try {
			String input = sc.nextLine();
			Customer customer = cdao.getCustomerByUsername(input);
			System.out.printf("Username: %s, Balance: $%.2f, Status: %s\n", customer.getUsername(), customer.getBalance(), customer.getStatus());
		} catch (SQLException e) {
			System.out.println("Invalid Input: customer not registered");
			sc.close();
			return;
		} catch (NoSuchElementException e) {}
		sc.close();
	}

	/*
	 * The method will output the content of the log.txt file
	 */
	@Override
	public void viewLog() {
		File file = new File("log.txt");
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			for(String line; (line = br.readLine()) != null;) {
		        System.out.println(line);
		    }
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			System.out.println("log.txt file cannot be found");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
