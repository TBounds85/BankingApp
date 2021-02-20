package com.app.service;

import com.app.exception.BusinessException;

public interface GeneralService {
	
	int getNextTransactionId() throws BusinessException;
	int getNextAccountNumber() throws BusinessException;
	boolean CheckUsernameAvailability(String username) throws BusinessException;
	void CreateNewAccountClient(int UserId, int accountNumber, float transferAmount) throws BusinessException;
	void CreateNewAccountEmployee(int UserId) throws BusinessException;
	void GenerateTransactionRecord(float oldBalance, float transAmount, float newBalance, int accountNumber) throws BusinessException;
	void CreateNewUser(String name, String address, long contact, String username, String password) throws BusinessException;
	
}/********************End of Interface****************/
