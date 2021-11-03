package com.sung.user;

import com.sung.customer.CustomerDao;
import com.sung.customer.CustomerDaoImpl;
import com.sung.employee.EmployeeDao;
import com.sung.employee.EmployeeDaoImpl;

public class UserDaoFactory {

	private static UserDao userDao;
	private static CustomerDao customerDao;
	private static EmployeeDao employeeDao;

	private UserDaoFactory() {}

	public static UserDao getUserDao() {
		if (userDao == null)
			userDao = new UserDaoImpl();
		return userDao;
	}

	public static CustomerDao getCustomerDao() {
		if (customerDao == null)
			customerDao = new CustomerDaoImpl();
		return customerDao;
	}

	public static EmployeeDao getEmployeeDao() {
		if (employeeDao == null)
			employeeDao = new EmployeeDaoImpl();
		return employeeDao;
	}

}
