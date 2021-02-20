package com.app.menu;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.app.dao.ClientCrudDAO;
import com.app.dao.impl.ClientCrudDAOImpl;
import com.app.exception.BusinessException;
import com.app.service.GeneralService;
import com.app.service.impl.GeneralServiceImpl;

public interface ClientMenu {
	
	public static void ClientMenuDisplay(final int UserId) throws BusinessException {
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		Logger log=Logger.getLogger(ClientMenu.class);
		int cchoice = 0;
		ClientCrudDAO dao = new ClientCrudDAOImpl();
		GeneralService dao2 =new GeneralServiceImpl();
		
		do {

			log.info("\n    Hello!\n");
			log.info("  Client Menu");
			log.info("==================");
			log.info("1) View Account Balances");
			log.info("2) View Transaction History of Account");
			log.info("3) Transfer Funds to Account");
			log.info("4) Your Pending Money Transfers");
			log.info("5) Change Login Password");
			log.info("6) Apply for New Account");
			log.info("7) Logout");

			log.info("\nPlease enter appropriate choice between 1-7");
			try {
				cchoice = Integer.parseInt(sc.nextLine());
			} 
			catch (NumberFormatException | NoSuchElementException e) {
				log.info("\nInvalid Option Chosen. Please Try Again...");}
			
			switch (cchoice) {
			
			
			case 1://Check Account Balances
				dao.checkBalanceOfAccounts(UserId);
				log.info("you will need the above account number(s) to:\nPerform any money tranfers & View Transaction History");
				break;
				
			case 2://View Transaction History of Account
//				displays accounts and balances above for easier client use
				dao.checkBalanceOfAccounts(UserId);
				log.info("Which account would you like to view?");
				int accountNumber;
				try{
					accountNumber = Integer.parseInt(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Invalid Account Number");
					break;
				}
				//View Transaction History of Account
				dao.viewTransactionsHistory(accountNumber);
				break;
				
			case 3://Transfer Funds to Account
//				displays accounts and balances above for easier client use
				dao.checkBalanceOfAccounts(UserId);
				log.info("\nEnter Account Number to Transfer FROM: ");
				int fromAccount;
				try {
					fromAccount = Integer.parseInt(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Invalid Account Number");
					break;
				}
				log.info("\nEnter Account Number to Transfer TO: ");
				int toAccount;
				try{
					toAccount= Integer.parseInt(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Invalid Account Number");
					break;
				}
				log.info("\nEnter Amount you would like to Transfer");
				float pendingAmt;
				try {
					pendingAmt = Float.parseFloat(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Must be Dollar ($) Amount");
					break;
				}
				dao.transferFundsToAccount(fromAccount, toAccount, pendingAmt);		
				break;
				
			case 4:// Pending Money Transfers
				log.info("Under Construction");
//				displays accounts and balances above for easier client use
				dao.checkBalanceOfAccounts(UserId);
				log.info("\nPlease Enter Account Number to view pending transactions");
				int pendingAccountNumber;
				try {
					pendingAccountNumber= Integer.parseInt(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Invalid Account Number");
					break;
				}
				dao.viewPendingTransfers(pendingAccountNumber);
				log.info("Enter Account Number Again to Verify & Approve Tranfers");
				log.info("Enter negative one to deny(-1) ");
				int	pendingAccountNumber2;
				try {
					pendingAccountNumber2= Integer.parseInt(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Invalid Account Number");
					break;
				}
				if(pendingAccountNumber == pendingAccountNumber2) {
					dao.acceptPendingTransfer(pendingAccountNumber);
					break;
				}else if(pendingAccountNumber2 == -1) {
					dao.denyPendingTransfer(pendingAccountNumber);
					break;
				}else {
					log.info("Account Numbers Didn't Match.");
				}
				break;
				
			case 5://Change Password
				log.info("Please Enter Your New Password!\n");
				String newPassword = sc.nextLine();
				dao.changePassword(UserId, newPassword);
				break;
				
			case 6://Apply for New Account
//				displays accounts and balances above for easier client use
				dao.checkBalanceOfAccounts(UserId);
				log.info("\nPlease Enter Account Number You Want to Transfer Funds from:");
				int transAccountNumber;
				try {
					transAccountNumber= Integer.parseInt(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Invalid Account Number");
					break;
				}
				log.info("Please Enter Amount You Want to Transfer to New Account");
				float transAmount;
				try {
					transAmount = Float.parseFloat(sc.nextLine());
				}catch (NumberFormatException e1) {
					log.info("Must be Dollar ($) Amount");
					break;
				}
				if (transAmount >=500) {
					int newAccountNumber= dao2.getNextAccountNumber();		
					dao.transferFundsToAccount(transAccountNumber, newAccountNumber, transAmount);
					dao2.CreateNewAccountClient(UserId, newAccountNumber, transAmount);		
					log.info("\nAn Employee has to Approve Your Account");
					log.info("Before You will Have Access\n");
					break;
				}else {
					log.info("Our Apologies, Generic Bank's Minimum Required Deposit of $500 for New Accounts");
					log.info("Account NOT Created\n");
				}
				break;
			
			case 7:
				break;
			
			default:
				log.info("Invalid menu choice. Please Enter Option Again.");
				break;
			}// End of Switch
			
		} while (cchoice != 7);
		
	return;
	}   //end of method
}/********************End of Interface****************/ 
