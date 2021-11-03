package com.sung.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sung.bank.ConnectionFactory;

public class CustomerDaoImpl implements CustomerDao {

	private Connection connection;

	public CustomerDaoImpl() {
		this.connection = ConnectionFactory.getConnection();
	}

	/*
	 * The method will accept a Customer object
	 * It will insert the values of Customer to the customer table in MySQL and will set
	 * the id value of the Customer object to the id value of the customer table created
	 */
	@Override
	public void addCustomer(Customer customer) throws SQLException {
		String sql = "insert into customer (username, password, balance, status) values (?, ?, ?, ?)";
		PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		preparedStatement.setString(1, customer.getUsername());
		preparedStatement.setString(2, customer.getPassword());
		preparedStatement.setDouble(3, customer.getBalance());
		preparedStatement.setString(4, customer.getStatus());
		int count = preparedStatement.executeUpdate();
		if(count > 0)
			System.out.println("Customer added");
		ResultSet resultSet = preparedStatement.getGeneratedKeys();
		while(resultSet.next()) {
			customer.setId(resultSet.getInt(1));
		}
	}

	/*
	 * The method will accept a Customer object
	 * It will update the balance and status columns in the customer table in MySQL
	 * corresponding to the id value of Customer
	 */
	@Override
	public void updateCustomer(Customer customer) throws SQLException {
		String sql = "update customer set balance = ?, status = ? where id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setDouble(1, customer.getBalance());
		preparedStatement.setString(2, customer.getStatus());
		preparedStatement.setInt(3, customer.getId());
		int count = preparedStatement.executeUpdate();
		if(count > 0)
			System.out.println("Customer updated");
	}

	/*
	 * The method will accept a Customer object
	 * It will remove the values in the customer table in MySQL
	 * corresponding to the id value of Customer
	 */
	@Override
	public void deleteCustomer(Customer customer) throws SQLException {
		String sql = "delete from customer where id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, customer.getId());
		int count = preparedStatement.executeUpdate();
		if(count > 0)
			System.out.println("Customer deleted");
	}

	/*
	 * The method will accept a String object
	 * It will acquire the rows from the customer table in MySQL whose status value
	 * is equal to the String and create a List of Customer objects
	 * The method will return the List object
	 */
	@Override
	public List<Customer> getCustomers(String status) throws SQLException {
		String sql = "select * from customer where status = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, status);
		ResultSet resultSet = preparedStatement.executeQuery();
		List<Customer> list = new ArrayList<Customer>();
		while (resultSet.next()) {
			Customer customer = new Customer();
			customer.setId(resultSet.getInt(1));
			customer.setUsername(resultSet.getString(2));
			customer.setPassword(resultSet.getString(3));
			customer.setBalance(resultSet.getDouble(4));
			customer.setStatus(resultSet.getString(5));
			list.add(customer);
		}
		return list;
	}

	/*
	 * The method will accept a integer variable
	 * It will retrieve the information from the customer table in MySQL whose id value
	 * is equal to the integer variable and create a Customer from the information
	 * The method will return the Customer object
	 */
	@Override
	public Customer getCustomerById(int id) throws SQLException {
		Customer customer = new Customer();
		String sql = "select * from customer where id = " + id;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		resultSet.next();
		if (resultSet != null) {
			customer.setId(resultSet.getInt(1));
			customer.setUsername(resultSet.getString(2));
			customer.setPassword(resultSet.getString(3));
			customer.setBalance(resultSet.getDouble(4));
			customer.setStatus(resultSet.getString(5));
		}
		return customer;
	}

	/*
	 * The method will accept a String object
	 * It will retrieve the information from the customer table in MySQL whose username
	 * is equal to the integer variable and create a Customer from the information
	 * The method will return the Customer object
	 */
	@Override
	public Customer getCustomerByUsername(String username) throws SQLException {
		Customer customer = new Customer();
		String sql = "select * from customer where username = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, username);
		ResultSet resultSet = preparedStatement.executeQuery();
		resultSet.next();
		if (resultSet != null) {
			customer.setId(resultSet.getInt(1));
			customer.setUsername(resultSet.getString(2));
			customer.setPassword(resultSet.getString(3));
			customer.setBalance(resultSet.getDouble(4));
			customer.setStatus(resultSet.getString(5));
		}
		return customer;
	}

	/*
	 * The method will accept a Customer object and a double variable
	 * It will add the double variable to the Customer balance
	 * then it will update its entry in the customer table in MySQL
	 */
	@Override
	public void withdraw(Customer customer, double amount) throws SQLException {
		System.out.printf("Withdrawing $%.2f\n", amount);
		if (customer.getBalance() < amount || amount <= 0) {
			throw new SQLException();
		} else {
			customer.setBalance(customer.getBalance() - amount);
			updateCustomer(customer);
		}
	}

	/*
	 * The method will accept a Customer object and a double variable
	 * It will subtract the double variable from the Customer balance
	 * then it will update its entry in the customer table in MySQL
	 */
	@Override
	public void deposit(Customer customer, double amount) throws SQLException {
		System.out.printf("Depositing $%.2f\n", amount);
		if (amount <= 0)
			throw new SQLException();
		customer.setBalance(customer.getBalance() + amount);
		updateCustomer(customer);
	}

	/*
	 * The method will accept two Customer objects and a double variable
	 * It will call the withdraw method to remove funds from the sending Customer
	 * It will take the id values from the Customers and insert them with the double
	 * variable to the transfer table in MySQL
	 */
	@Override
	public void push(Customer sender, Customer receiver, double amount) throws SQLException {
		System.out.printf("Sending $%.2f\n", amount);
		if (amount <= 0 || sender.getUsername().equals(receiver.getUsername()) || !receiver.getStatus().equals("approved"))
			throw new SQLException();
		withdraw(sender, amount);
		String sql = "insert into transfer (receiver_id, sender_id, amount) values (?, ?, ?)";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, receiver.getId());
		preparedStatement.setInt(2, sender.getId());
		preparedStatement.setDouble(3, amount);
		int count = preparedStatement.executeUpdate();
		if(count > 0)
			System.out.println("Transfer complete");
	}

	/*
	 * The method will accept a Customer object
	 * It will acquire the amount values from the transfer table in MySQL corresponding
	 * to the id value of the Customer
	 * It will call the deposit method to add funds to the receiving Customer
	 * It will remove the entries acquired from the transfer table
	 */
	@Override
	public void pull(Customer receiver) throws SQLException {
		String sql = "select * from transfer where receiver_id = " + receiver.getId();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		while (resultSet.next()) {
			double amount = resultSet.getDouble(4);
			System.out.printf("Receiving $%.2f\n", amount);
			deposit(receiver, amount);
		}
		sql = "delete from transfer where receiver_id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, receiver.getId());
		int count = preparedStatement.executeUpdate();
		if(count > 0)
			System.out.println("Transfer complete");
	}

}
