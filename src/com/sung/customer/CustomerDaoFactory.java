package com.sung.customer;

public class CustomerDaoFactory {

	private static CustomerDao customerDao;

	private CustomerDaoFactory() {super();}

	public static CustomerDao getCustomerDao() {
		if (customerDao == null)
			customerDao = new CustomerDaoImpl();
		return customerDao;
	}

}
