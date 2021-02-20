/**
 * 
 */
package com.app.model.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.app.model.Client;

/**
 * @author ThomasBounds{ <~~~~get it cause im a class-y guy
 *
 */
class ClientTest {

	@Test
	void testGetUserId() {
		Client client = new Client();
		client.setUserId(1000);                      
		assertEquals(1000, client.getUserId());
	
	}
	
	@Test
	void testGetName() {
		Client client = new Client();
		client.setName("Joe Moneybags");
		assertEquals("Joe Moneybags", client.getName());
	
	}
	
	@Test
	void testGetAddress() {
		Client client = new Client();
		client.setAddress("123 This Street");
		assertEquals("123 This Street",client.getAddress());
	
	}
	
	@Test
	void testGetContact() {
		Client client = new Client();
		client.setContact(1234567890L);
		assertEquals(1234567890L, client.getContact());
	
	}
}
	