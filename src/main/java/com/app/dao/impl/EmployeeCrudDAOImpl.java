package com.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.app.dao.EmployeeCrudDAO;
import com.app.dao.dbutil.PostgresqlConnection;
import com.app.exception.BusinessException;
import com.app.model.Accounts;
import com.app.model.Client;
import com.app.model.Transaction;
import com.app.service.GeneralService;
import com.app.service.impl.GeneralServiceImpl;

public class EmployeeCrudDAOImpl implements EmployeeCrudDAO{
	
	//initiating objects needed globally
	SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM-dd-yyyy z  HH:mm:ss");
	Logger log=Logger.getLogger(EmployeeCrudDAOImpl.class);
	public Date d=new Date();
	Scanner sc = new Scanner(System.in);
	
	//case SeNsItIvE
	@Override
	public void getClientByName(String clientName) throws BusinessException {
		
		try (Connection connection = PostgresqlConnection.getConnection()){
			String sql = "select userid, name, contact, address from client where name like ?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, "%"+clientName+"%");
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Client client = new Client();
				client.setUserId(resultSet.getInt("userid"));
				client.setName(resultSet.getString("name"));
				client.setAddress(resultSet.getString("address"));
				client.setContact(resultSet.getLong("contact"));
				log.info(client.toString());
			}			
			
		} catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Interacting with Database");
		}
	}//End of getClientByName()

	@Override
	public List<Accounts> showAllAccounts(int userId) throws BusinessException {
		
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			String sql = "select accountnumber,accountbalance from accounts where userid=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Accounts account = new Accounts();
				account.setAccountNumber(resultSet.getInt("accountnumber"));
				account.setAccountBalance(resultSet.getFloat("accountbalance"));
				log.info(account.toString());
				log.info("");
			}
			log.info("No Other accounts found For this userID");
				
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Interacting with Database");
		}
		return null;
	}

	@Override
	public List<Transaction> getAllTransactions(int accountNumber) throws BusinessException {
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			String sql = "select * from transaction where accountnumber=? order by transid";	
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, accountNumber);	
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Transaction transaction = new Transaction();
				transaction.setTransid(resultSet.getInt("transid"));
				transaction.setOldBalance(resultSet.getFloat("oldbalance"));
				transaction.setNewBalance(resultSet.getFloat("newbalance"));
				transaction.setTransamount(resultSet.getFloat("transamount"));
				transaction.setAccountNumber(resultSet.getInt("accountnumber"));
				transaction.setTimeStamp(resultSet.getString("timestamp"));;
				log.info(transaction.toString());
				log.info("");
			}
			log.info("End of Transactions");
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Interacting with Database");
		}
		return null;
	}
	
	@Override
	public void withdrawForClient(int accountnumber, float withdrawlAmount) throws BusinessException {
			
		try (Connection connection = PostgresqlConnection.getConnection()) {
			String sql = "select accountbalance from accounts where accountnumber=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, accountnumber);
			ResultSet balance = preparedStatement.executeQuery();
			balance.next();
			
			float oldBalance = balance.getFloat("accountbalance");
			
			if (oldBalance <= 0 || oldBalance - withdrawlAmount <0) {
				log.info("This Will Put Your Account in the (-) Negative, Please Enter Another Amount.");
				return;
			}else if(withdrawlAmount <=0) {
				log.info("You Can NOT Withdrawl a (-) Neagtive Amount. Please Enter (+) Positive Amount.");
				return;
			}else {
				float newBalance = oldBalance - withdrawlAmount;

				//Updating Accounts Table with New Balance
				String sql1 ="update accounts set accountbalance=? where accountnumber=?";
				preparedStatement = connection.prepareStatement(sql1);
				preparedStatement.setFloat(1, newBalance);
				preparedStatement.setInt(2, accountnumber);
				preparedStatement.executeUpdate();
				
				//Generate Transaction Record
				GeneralService dao =new GeneralServiceImpl();
				dao.GenerateTransactionRecord(oldBalance, withdrawlAmount, newBalance , accountnumber);
				
				log.info("Withdrawl Complete You may now Distribute Funds to Client");
			}
		return;
		}catch (ClassNotFoundException | SQLException e) {
			
			throw new BusinessException("Something is wrong in query to DB");
		}
		
	
	}//End of withdrawForClient()
		
	@Override
	public void depositForClient(int accountNumber, float depositAmount) throws BusinessException {
		
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			String sql = "select accountbalance from accounts where accountnumber=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, accountNumber);
			ResultSet balance = preparedStatement.executeQuery();
			balance.next();
			float oldBalance = balance.getFloat("accountbalance");
			
			if(depositAmount <=0) {
				log.info("You Can NOT deposit 0 or a Negative (-) amount");
				return;
			}else {
				
				float newBalance = oldBalance+depositAmount;
				
				String sql2 ="update accounts set accountbalance=? where accountnumber=?";
				preparedStatement = connection.prepareStatement(sql2);
				preparedStatement.setFloat(1, newBalance);
				preparedStatement.setInt(2, accountNumber);
				preparedStatement.executeUpdate();
				
				GeneralService dao =new GeneralServiceImpl();
				dao.GenerateTransactionRecord(oldBalance, depositAmount, newBalance, accountNumber);
				
				log.info("Deposit completed Successfully\n");
			}
			
		}catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Interacting with Database");
		}
		
	}//End of DepositForClient()
	
	@Override
	public void transferFundsForClient(int fromAccountId, int toAccountId, float transferAmount) throws BusinessException {
		
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			String sql = "select accountbalance from accounts where accountnumber=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, fromAccountId);
			
			ResultSet balance = preparedStatement.executeQuery();
			balance.next();
			float oldBalance = balance.getFloat("accountbalance");
			
			if (oldBalance <= 0 || oldBalance - transferAmount <0) {
				
				log.info("This Will Put Your Account in the (-) Negative, Please Enter Another Amount.\n");
				return;
			}else if(transferAmount <=0) {
				log.info("You Can NOT Transfer a (-) Neagtive Amount. Please Enter (+) Positive Amount.\n");
				return;
			}else {
				//actual adjustment of customer balance
				float newBalance = oldBalance-transferAmount; 
									
				//withdrawal funds from account 
				String sql2 = "update accounts set accountbalance=? where accountnumber=?";
				preparedStatement = connection.prepareStatement(sql2);
				preparedStatement.setFloat(1, newBalance);
				preparedStatement.setInt(2, fromAccountId);
				preparedStatement.executeUpdate();
				
				//put pending transaction in "pendingtransfers"
				String sql3 = "insert into pendingtransfers(fromaccount, toaccount, pendingamt) values(?,?,?)";
				preparedStatement = connection.prepareStatement(sql3);
				preparedStatement.setInt(1,fromAccountId);
				preparedStatement.setInt(2,toAccountId);
				preparedStatement.setFloat(3,transferAmount);
				preparedStatement.executeUpdate();
				
				//Generate TransactionRecord
				GeneralService dao =new GeneralServiceImpl();
				dao.GenerateTransactionRecord(oldBalance, transferAmount, newBalance, fromAccountId);
				
				log.info("\nYour Transfer Will be pending until the other account holder approves.");
				log.info("Your account balance will reflect the tranfer now, but");
				log.info("The transfer amount will be refunded if account holder refuses the Tansfer\n");
			}
			return;
		} catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Interacting with Database");
		}
	}
	
	@Override
	public void seeUnapprovedAccounts() throws BusinessException{
		try(Connection connection = PostgresqlConnection.getConnection()){
			String sql = "select userid, accountnumber,accountbalance from newaccounts";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Accounts account =new Accounts();
				account.setUserId(resultSet.getInt("userid"));
				account.setAccountNumber(resultSet.getInt("accountnumber"));
				account.setAccountBalance(resultSet.getFloat("accountbalance"));
				log.info(account);	
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Connecting to Database thru Services");
		}
	}
	
	@Override
	public void approveAccount(int accountnumber) throws BusinessException {
		
		try(Connection connection = PostgresqlConnection.getConnection()){
			
			//gets initial transfer positive amount from newaccounts 
			String sql = "select userid, accountbalance from newaccounts where accountnumber=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, accountnumber);
			ResultSet results = preparedStatement.executeQuery();
			
			if(results.next()) {
				float accountbalance= results.getFloat("accountbalance");
								
				//in GeneralServicesImpl... new account balance's are set to negative the initial transfer
				float setAccountRight = accountbalance*2; 
				
				//pulls account balance which should be negative
				String sql1="select accountbalance from accounts where accountnumber=?";
				preparedStatement= connection.prepareStatement(sql1);
				preparedStatement.setInt(1,accountnumber);
				ResultSet resultSet = preparedStatement.executeQuery();
				resultSet.next();
				float oldBalance =resultSet.getFloat("accountbalance");
				
				//Flips negative initial transfer to positive
				float newBalance = oldBalance + setAccountRight;
				
				String sql2 = "update accounts set accountbalance=? where accountnumber=?";
				preparedStatement = connection.prepareStatement(sql2);
				preparedStatement.setFloat(1, newBalance);
				preparedStatement.setInt(2, accountnumber);
				preparedStatement.executeUpdate();
				
				String sql3 = "delete from newaccounts where accountnumber=?";
				preparedStatement = connection.prepareStatement(sql3);
				preparedStatement.setInt(1, accountnumber);
				preparedStatement.executeUpdate();
				
				log.info("Account Approved!");
			}else {
				log.info("Account # Entered Doesn't Exist.");
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Connecting to Database thru Services "+e);
		}
	}

}/********************End of Class****************/ 
