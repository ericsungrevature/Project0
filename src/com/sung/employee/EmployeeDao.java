package com.sung.employee;

import java.sql.SQLException;
import java.util.Scanner;

public interface EmployeeDao {

	Employee getEmployeeById(int id) throws SQLException;

	Employee getEmployeeByUsername(String username) throws SQLException;

	void checkAccount(Scanner scan);

	void viewAccount(Scanner scan);

	void viewLog();

}
