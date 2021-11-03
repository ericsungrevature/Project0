package com.sung.employee;

import java.sql.SQLException;

public interface EmployeeDao {

	Employee getEmployeeById(int id) throws SQLException;

	Employee getEmployeeByUsername(String username) throws SQLException;

	void checkAccount();

	void viewAccount();

	void viewLog();

}
