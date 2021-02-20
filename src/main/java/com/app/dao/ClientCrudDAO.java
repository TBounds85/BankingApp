package com.app.dao;

import com.app.exception.BusinessException;

public interface ClientCrudDAO {
	
	public void checkBalanceOfAccounts(int UserId) throws BusinessException;
	public void acceptPendingTransfer(int accountNumber) throws BusinessException;
	public void viewTransactionsHistory(int accountNumber) throws BusinessException;
	public void changePassword(int UserId, String newPassword) throws BusinessException;
	public void transferFundsToAccount(int transAccountNumber, int toAccount, float transAmount) throws BusinessException;
	public void viewPendingTransfers(int accountNumber) throws BusinessException;
	public void denyPendingTransfer(int accountNumber) throws BusinessException;

}/********************End of Interface****************/ 
