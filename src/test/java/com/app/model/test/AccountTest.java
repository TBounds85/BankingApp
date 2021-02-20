package com.app.model.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.app.model.Accounts;

class AccountTest {
	

	@Test
	void testGetUserId() {
		Accounts account = new Accounts();
		account.setUserId(1000);
		assertEquals(1000, account.getUserId());
	}
	
	@Test
	void testGetAccountNumber() {
		Accounts account = new Accounts();
		account.setAccountNumber(100000101);
		assertEquals(100000101, account.getAccountNumber());
	}
	
	@Test
	void testGetAccountBalance() {
		Accounts account = new Accounts();
		account.setAccountBalance((float) 1000000.25);
		assertEquals(1000000.25,account.getAccountBalance());
	}
	
	


}
