package com.sung.user;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.sung.bank.ConnectionFactory;
import com.sung.customer.Customer;
import com.sung.customer.CustomerDao;
import com.sung.customer.CustomerDaoFactory;
import com.sung.employee.Employee;
import com.sung.employee.EmployeeDao;
import com.sung.employee.EmployeeDaoFactory;

public class UserDaoImpl implements UserDao {

	private Connection connection;
	private Scanner scan = new Scanner(System.in);
	private String input;
	private CustomerDao cdao = CustomerDaoFactory.getCustomerDao();
	private EmployeeDao edao = EmployeeDaoFactory.getEmployeeDao();

	public UserDaoImpl() {
		this.connection = ConnectionFactory.getConnection();
	}

	/*
	 * The method will accept input to decide whether to:
	 * 1) Login as a Customer
	 * 2) Login as an Employee
	 * 3) Register a Customer account
	 * 4) Exit the Program
	 */
	public void loginStart() {
		while (true) {
			System.out.println("You may: \n1) Login As a Customer\n2) Login As an Employee\n3) Register an Account\n4) Exit");
			System.out.print("What would you like to do? ");
			input = scan.nextLine();
			switch(input) {
			default:
				System.out.println("Invalid Input: " + input);
				break;
			case "1":
				loginCustomer();
				break;
			case "2":
				System.out.println("Employee Login");
				loginEmployee();
				break;
			case "3":
				System.out.println("Register Customer");
				registerCustomer();
				break;
			case "4":
				System.out.println("Exit");
				scan.close();
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

	/*
	 * The method will accept input to login and decide whether to:
	 * 1) Withdraw Funds
	 * 2) Deposit Funds
	 * 3) Send a Transfer of Funds
	 * 4) Receive a Transfer of Funds
	 * 5) Logout
	 */
	@Override
	public void loginCustomer() {
		Customer customer = null;

		//Retrieve customer using username and password
		System.out.println("Customer Login");
		System.out.print("Enter your username: ");
		input = scan.nextLine();
		try {
			customer = cdao.getCustomerByUsername(input);
			System.out.print("Enter your password: ");
			input = scan.nextLine();			
			if (!customer.getPassword().equals(input) || !customer.getStatus().equals("approved"))
				throw new SQLException();
			System.out.println("Welcome " + customer.getUsername());
		} catch (SQLException e) {
			System.out.println("Invalid login: username or password not registered");
			return;
		}

		//Customer menu
		while (true) {
			System.out.println("You may:\n1) Withdraw Funds\n2) Deposit Funds\n3) Send a Transfer of Funds\n4) Receive a Transfer of Funds\n5) Logout");
			System.out.printf("Your Current Balance is: $%.2f\n", customer.getBalance());
			System.out.print("What would you like to do? ");
			input = scan.nextLine();
			switch(input) {
			default:
				System.out.println("Invalid Input: " + input);
				break;
			case "1":
				//Input money amount before calling the withdraw method and logging the transaction
				System.out.print("Input withdraw amount: $");
				input = scan.nextLine();
				try {
					double amount = Double.parseDouble(input);
					amount = Math.floor(amount * 100) / 100;
					cdao.withdraw(customer, amount);
					logTransaction("Customer "
							+ customer.getId() + " " + customer.getUsername()
							+ " withdrew funds\n");
				} catch (SQLException | NumberFormatException e) {
					System.out.println("Cannot withdraw funds");
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "2":
				//Input money amount before calling the deposit method and logging the transaction
				System.out.print("Input deposit amount: $");
				input = scan.nextLine();
				try {
					double amount = Double.parseDouble(input);
					amount = Math.floor(amount * 100) / 100;
					cdao.deposit(customer, amount);
					logTransaction("Customer "
							+ customer.getId() + " " + customer.getUsername()
							+ " deposited funds\n");
				} catch (SQLException | NumberFormatException e) {
					System.out.println("Cannot deposit funds");
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "3":
				//Input username of recipient and the amount of funds sent
				//before calling the push method and logging the transaction
				System.out.print("Input username of recipient: ");
				input = scan.nextLine();
				try {
					Customer receiver = cdao.getCustomerByUsername(input);
					System.out.print("Input push amount: $");
					input = scan.nextLine();
					double amount = Double.parseDouble(input);
					amount = Math.floor(amount * 100) / 100;
					cdao.push(customer, receiver, amount);
					logTransaction("Customer "
							+ customer.getId() + " " + customer.getUsername()
							+ " sent funds to "
							+ receiver.getId() + " " + receiver.getUsername() + "\n");
				} catch (SQLException | NumberFormatException e) {
					System.out.println("Cannot send funds");
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "4":
				//Call the pull method and logging the transaction
				try {
					cdao.pull(customer);
					logTransaction("Customer "
							+ customer.getId() + " " + customer.getUsername()
							+ " received funds\n");
				} catch (SQLException e) {
					System.out.println("Cannot receive funds");
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "5":
				//Exit the Customer menu
				System.out.println("Exit");
				return;
			}
		}
	}

	/*
	 * The method will accept input to login and decide whether to:
	 * 1) Check Pending Accounts
	 * 2) View Account Information
	 * 3) View Transaction Log
	 * 4) Logout
	 */
	@Override
	public void loginEmployee() {
		Employee employee;

		//Retrieve customer using username and password
		System.out.println("Employee Login");
		System.out.print("Enter your username: ");
		input = scan.nextLine();
		try {
			employee = edao.getEmployeeByUsername(input);
			System.out.print("Enter your password: ");
			input = scan.nextLine();			
			if (!employee.getPassword().equals(input))
				throw new SQLException();
			System.out.println("Welcome " + employee.getUsername());
		} catch (SQLException e) {
			System.out.println("Invalid login: username or password not registered");
			return;
		}

		//Employee menu
		while (true) {
			System.out.println("You may:\n1) Check Pending Accounts\n2) View Account Information\n3) View Transaction Log\n4) Logout");
			System.out.print("What would you like to do? ");
			input = scan.nextLine();
			switch(input) {
			default:
				System.out.println("Invalid Input: " + input);
				break;
			case "1":
				edao.checkAccount();
				break;
			case "2":
				edao.viewAccount();
				break;
			case "3":
				edao.viewLog();
				break;
			case "4":
				System.out.println("Exit");
				return;
			}
		}
	}

	/*
	 * The method will accept input to add a Customer account with a status of pending
	 */
	@Override
	public void registerCustomer() {
		Customer customer = new Customer();

		System.out.println("Register Customer");
		System.out.print("Enter the username: ");
		input = scan.nextLine();
		customer.setUsername(input);
		System.out.print("Enter the password: ");
		input = scan.nextLine();
		customer.setPassword(input);
		System.out.print("Enter the starting balance: $");
		input = scan.nextLine();
		try {
			double balance = Double.parseDouble(input);
			balance = Math.floor(balance * 100) / 100;
			customer.setBalance(balance);
			customer.setStatus("pending");
			cdao.addCustomer(customer);
			logTransaction("Customer "
					+ customer.getId() + " " + customer.getUsername()
					+ " added\n");
		} catch (SQLException | NumberFormatException e) {
			System.out.println("Issues with account registration");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Account registration complete\nCurrently pending");
	}

	/*
	 * The method will accept a String object
	 * It will append the String to the log.txt file
	 */
	@Override
	public void logTransaction(String str) throws IOException {
    	File file = new File("log.txt");
    	file.createNewFile();
    	FileWriter fw = new FileWriter(file, true);
    	fw.write(str);
    	fw.flush();
    	fw.close();
	}

}
