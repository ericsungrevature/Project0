package com.sung.user;

import java.io.IOException;

public interface UserDao {

	void loginStart();

	void loginCustomer();

	void loginEmployee();

	void registerCustomer();

	void logTransaction(String str) throws IOException;

}
