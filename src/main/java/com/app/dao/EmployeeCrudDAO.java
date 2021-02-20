package com.app.dao;

import java.util.List;

import com.app.exception.BusinessException;
import com.app.model.Accounts;
import com.app.model.Transaction;

public interface EmployeeCrudDAO{
	
	void getClientByName(String clientName) throws BusinessException;
	public List<Accounts> showAllAccounts(int userId) throws BusinessException;
	public List<Transaction> getAllTransactions(int accountID) throws BusinessException;
	public void withdrawForClient(int accountnumber, float withdrawlAmount) throws BusinessException;
	public void depositForClient(int accountnumber, float depositAmount) throws BusinessException;
	public void transferFundsForClient(int fromAccountId, int toAccountId, float transferAmount ) throws BusinessException;
	void seeUnapprovedAccounts() throws BusinessException;
	void approveAccount(int accountnumber) throws BusinessException;
	
}/********************End of Interface****************/ 