package com.app.model.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.app.model.Employee;

class EmployeeTest {

	@Test
	void testGetUserId() {
		Employee employee = new Employee();
		employee.setUserId(100);
		assertEquals(100, employee.getUserId());
	}

	@Test
	void testGetName() {
		Employee employee = new Employee();
		employee.setName("Thomas Bounds");
		assertEquals("Thomas Bounds", employee.getName());
	}
	
	@Test
	void testGetAddress() {
		Employee employee = new Employee();
		employee.setAddress("1234 Testing Way");
		assertEquals("1234 Testing Way", employee.getAddress());
	}
	
	@Test
	void testGetContact() {
		Employee employee = new Employee();
		employee.setContact(1234567890L);
		assertEquals(1234567890L, employee.getContact());
	}
}
