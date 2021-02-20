package com.app.menu;

import java.util.NoSuchElementException;
import java.util.Scanner;
import org.apache.log4j.Logger;

import com.app.dao.EmployeeCrudDAO;
import com.app.dao.impl.EmployeeCrudDAOImpl;
import com.app.exception.BusinessException;
import com.app.service.GeneralService;
import com.app.service.impl.GeneralServiceImpl;

public interface EmployeeMenu {
	
	static Logger log=Logger.getLogger(EmployeeMenu.class);
	
	Scanner sc = new Scanner(System.in);
	
	static void EmployeeMenuView() throws BusinessException{
	int clientUserId = 0;
	EmployeeCrudDAO dao = new EmployeeCrudDAOImpl();
	GeneralService dao2 = new GeneralServiceImpl();
	int echoice = 0;
	
	do {
		log.info("\nCurrect Assigned Client ID# : "+clientUserId);
		
		log.info("\n   	Employee Menu");
		log.info("====================================");
		log.info("1)  Set Current Client 	**Perform Before Client Transactions**" );
		log.info("2)  View Client Account Balance(s)");
		log.info("3)  View Client Transaction History");
		log.info("4)  Help Client Perform Withdrawl");
		log.info("5)  Help Client Perform Deposit");
		log.info("6)  Help Client Perform Money Transfer");
		log.info("7)  Help Client Open New Account");		
		log.info("8)  Clear Current Client 	**Perform When Client Transactions Complete**");
		log.info("9)  New Client? Choose Me!");
		log.info("10) Approve/Deny New Accounts");
		log.info("11) Log Out");
		
		log.info("Please enter appropriate choice between 1-11");
		try {
			echoice = Integer.parseInt(sc.nextLine());
		} 
		catch (NumberFormatException | NoSuchElementException e) {
			log.info("\nInvalid Option Chosen.\n");}
		
		switch (echoice) {
		
		case 1://assign current client			
			log.info("Hello, Im {Say your name here}, How May I have your name to get you started today?\n");
			String clientName = sc.nextLine();
			dao.getClientByName(clientName); 
			log.info("\n***Verify Client with Government Issued ID Card***");
			log.info("\nEnter Userid Associated with the Client:");
			log.info(" If No Clients Listed assign ID to Zero (0)");
			try {
				clientUserId = Integer.parseInt(sc.nextLine());
				if(clientUserId !=0 && clientUserId<999 || clientUserId>10000) { //can adjust when needing more USERID# above 10,000 {
					log.info("No Client With that ID #.");
					clientUserId = 0;
				}
			}catch (NumberFormatException e) {
				log.info("Account # Doesn't Exist\n");
				break;}
			
		case 2://View Client Account Balance(s)
			if(clientUserId>999) {
				dao.showAllAccounts(clientUserId);
			}
			else {
				log.info("No Client # Assigned\n");
			}	
			break;
			
		case 3:// View Client Transaction History
			if(clientUserId>999) {
				dao.showAllAccounts(clientUserId);
				log.info("\nEnter Account Number the Client Wants Transaction Record for:");
				int clientAccount=0;
				try {
					clientAccount =Integer.parseInt(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Account # Doesn't Exist\n");
					break;
				}
				dao.getAllTransactions(clientAccount);
			}
			else {
				log.info("No Client # Assigned\n");
			}	
			break;
			
		case 4://Help Client Perform Withdrawl
			if(clientUserId>999) {
				dao.showAllAccounts(clientUserId);
				
				log.info("\nEnter Account Number Client Wants to Withdraw From");
				int accountNumber = 0;
				try {
					accountNumber = Integer.parseInt(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Account # Doesn't Exist\n");
					break;}
				
				log.info("\nEnter Amount Client Wants to Withdraw");	
				float withdrawlAmount = 0;
				try {
					withdrawlAmount = Float.parseFloat(sc.nextLine());	
				}catch (NumberFormatException e) {
					log.info("Must Be Dollar ($) Amount\n");
					break;
				}
				dao.withdrawForClient(accountNumber , withdrawlAmount);
			}
			else {
				log.info("No Client # Assigned\n");
				break;
			}
			break;
			
		
		case 5://Deposit
			if(clientUserId>999) {
				dao.showAllAccounts(clientUserId);
				log.info("\nEnter Account Number Client Wants to Deposit to");
				int accountNumber = 0;
				try {
					accountNumber = Integer.parseInt(sc.nextLine());
				}catch(NumberFormatException e){
					log.info("That is not a vaild AccountNumber\n");
					break;
				}
				log.info("\n***Count Money Twice Before Entering Amount***");
				log.info("\nEnter Amount Client Wants to Deposit");
				float depositAmount=0;
				try {
					depositAmount = Float.parseFloat(sc.nextLine());
				}catch (NumberFormatException e) {
					log.info("Must Be Dollar ($) Amount\n");
					break;
				}
				dao.depositForClient(accountNumber, depositAmount);
			}
			else {
				log.info("No Client # Assigned\n");
			}	
			break;
			
		case 6://Help Client Perform Money Transfer
			
			if(clientUserId>999) {
				//shows all client's Accounts
				dao.showAllAccounts(clientUserId);
				log.info("\nEnter Account Number of Client Transfering Money");
				int fromAccountId=0;
				try {
					fromAccountId = Integer.parseInt(sc.nextLine());
				}catch(NumberFormatException e){
					log.info("Account # Doesn't Exist\n");
					break;
				}
				log.info("\nEnter Account Number Money will be transfered to");
				int toAccountId=0;
				try {
					toAccountId = Integer.parseInt(sc.nextLine());
				}catch(NumberFormatException e){
					log.info("Account # Doesn't Exist\n");
					break;
				}			
				log.info("\nEnter Amount to Transfer");
				float transferAmount=0;
				try {
					transferAmount = Float.parseFloat(sc.nextLine());
				}catch(NumberFormatException e){
					log.info("Must Be Dollar ($) Amount\n");
					break;
				}
				dao.transferFundsForClient(fromAccountId, toAccountId, transferAmount);
				break;
				}
			else {
				log.info("No Client # Assigned\n");
			}	
			break;
			
		case 7://create new bank account using clientUserID
			if(clientUserId>999) {
				dao2.CreateNewAccountEmployee(clientUserId);
			}else {
				log.info("No Client # Assigned\n");
			}
			break;
		
		case 8://clear assigned client ID
			clientUserId = 0;
			break;
		
		case 9://New USERID Creation
			if(clientUserId==0) {
				boolean checker=false;
				String username;
				log.info("May I have Your Full Name?");
				String name = sc.nextLine();
				log.info("\nWhats your Address?");
				String address = sc.nextLine();
				long contact=0;
				log.info("\nWhat is your 10 Digit Contact Number?");
				try {
					contact = Long.parseLong(sc.nextLine());
				}catch(NumberFormatException e){
					log.info("Must be Numbers, no contact number saved");
					contact = 9999999999L; //default input if wrong number 
					
				log.info("\nThe Client needs to Come up with a Username.");
				log.info("This will be used to Login with our Client App.");
				
				do {
					log.info("Please Enter a New Username");
					username = sc.nextLine();
					checker = dao2.CheckUsernameAvailability(username);
					if(checker == true) {
						log.info(username+" is Unavailable.\n");
					}
				}while (checker == true);
				String password = "tempPasswordGBA";
				dao2.CreateNewUser(name, address, contact, username, password); 
				log.info("\n**Write "+username+" and 'tempPasswordGBA' on NEW CLIENT CARD and hand to Client.");
				log.info("let them know this is their temporary Password and encourage them to change it ASAP\n");
				log.info("Now you are set up to use our Generic Banking App.");
			}
			break;
			}
			else {
				log.info("Client # Assigned Must Set to Zero (0)\n");
			}
			break;
			
		case 10://Approve/Deny New Accounts
			dao.seeUnapprovedAccounts();
			log.info("\nEnter Account Number you want to Approve.");
			int accountnumber =0;
			try {
				accountnumber = Integer.parseInt(sc.nextLine());
			}catch (NumberFormatException e) {
				log.info("Account # Doesn't Exist\n");
				break;
			}
			dao.approveAccount(accountnumber);	
			break;
			
		case 11://logout
			break;
			
		default:
			log.info("Invalid menu choice. Please Enter Valid Menu Option.");
			break;}//end of Switch
	
	}while (echoice != 11);
	
	log.info("See you next time you are scheduled at Generic Bank!!");
	
	}//end of method
}/********************End of Interface****************/