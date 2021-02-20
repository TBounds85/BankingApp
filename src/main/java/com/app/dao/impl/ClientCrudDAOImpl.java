package com.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.app.dao.ClientCrudDAO;
import com.app.dao.dbutil.PostgresqlConnection;
import com.app.exception.BusinessException;
import com.app.model.Accounts;
import com.app.model.Transaction;
import com.app.service.GeneralService;
import com.app.service.impl.GeneralServiceImpl;



public class ClientCrudDAOImpl implements ClientCrudDAO{
	
	//initiating objects needed globally
	public Date d=new Date();
	Scanner sc = new Scanner(System.in);
	SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM-dd-yyyy z  HH:mm:ss");
	Logger log=Logger.getLogger(ClientCrudDAOImpl.class);
	
	
	@Override
	public void checkBalanceOfAccounts(int UserId) throws BusinessException {
		
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			//sets accounts with Negative account balances not visible to Client
			String sql = "select accountnumber,accountbalance from accounts where userid=? and accountbalance>=0";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, UserId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				Accounts account = new Accounts();
				account.setAccountNumber(resultSet.getInt("accountnumber"));
				account.setAccountBalance(resultSet.getFloat("accountbalance"));
				log.info(account.toString());
				log.info("");
			}
			log.info("End of Accounts Found.");
			return;
		}catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Interacting with Database");
		}		
	}

	@Override
	public void viewTransactionsHistory(int acctId) throws BusinessException {
		try (Connection connection = PostgresqlConnection.getConnection()) {
				
			String sql = "select * from transaction where accountnumber=? order by transid";				
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, acctId);				
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
			return;	
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Interacting with Database");
		}
	}

	@Override
	public void transferFundsToAccount(int fromAccount, int toAccount, float pendingAmt) throws BusinessException {
		
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			String sql = "select accountbalance from accounts where accountnumber=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, fromAccount);
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			float oldBalance = resultSet.getFloat("accountbalance");
			
			if (oldBalance <= 0 || oldBalance - pendingAmt <=0) {
				log.info("This Will Put Your Account in the (-) Negative, Please Enter Another Amount.");
				log.info("Transaction Cancelled");
				return;
			}else if(pendingAmt <=0) {
				log.info("You Can NOT Transfer a (-) Neagtive Amount. Please Enter (+) Positive Amount.");
				log.info("Transaction Cancelled");
				return;
			}else {
				//actual adjustment of customer balance
				float newBalance = oldBalance-pendingAmt; 
									
				//withdrawal funds from account 
				String sql2 = "update accounts set accountbalance=? where accountnumber=?";
				preparedStatement = connection.prepareStatement(sql2);
				preparedStatement.setFloat(1, newBalance);
				preparedStatement.setInt(2, fromAccount);
				preparedStatement.executeUpdate();
				
				//put pending transaction in "pendingtransfers"
				String sql3 = "insert into pendingtransfers(fromaccount, toaccount, pendingamt) values(?,?,?)";
				preparedStatement = connection.prepareStatement(sql3);
				preparedStatement.setInt(1,fromAccount);
				preparedStatement.setInt(2,toAccount);
				preparedStatement.setFloat(3,pendingAmt);
				preparedStatement.executeUpdate();
				
				//Generate Transaction Record
				GeneralService dao =new GeneralServiceImpl();
				dao.GenerateTransactionRecord(oldBalance, pendingAmt, newBalance, fromAccount);
				log.info("Transfer Complete.\n");
				return;
			}		
		}catch (ClassNotFoundException | SQLException e) {
				throw new BusinessException("Error Interacting with Database");
		}
	}//End of transferFundsToAccount()	
	
	@Override
	public void viewPendingTransfers(int accountNumber) throws BusinessException{
		
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			String sql = "select toaccount,pendingamt from pendingtransfers where toaccount=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, accountNumber);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				accountNumber = resultSet.getInt("toaccount");
				float pendingAmount = resultSet.getFloat("pendingamt");
				log.info("Account Number: "+accountNumber);
				log.info("Pending Amount: "+pendingAmount+"\n");
			}		
		} catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Interacting with Database");
		}return;
	}
	
	@Override
	public void acceptPendingTransfer(int accountNumber) throws BusinessException {
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			String sql = "select pendingamt from pendingtransfers where toaccount=?";	
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, accountNumber);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				float pendingAmount = resultSet.getFloat("pendingamt");
				
				String sql2 = "select accountbalance from accounts where accountnumber=?";
				PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
				preparedStatement2.setInt(1, accountNumber);
				ResultSet resultSet2 = preparedStatement2.executeQuery();
				resultSet2.next();
				
				Float oldBalance = resultSet2.getFloat("accountbalance");
				Float newBalance = oldBalance+pendingAmount;
				
				String sql3="delete from pendingtransfers where pendingamt=? and toaccount=?";
				PreparedStatement preparedStatement3 = connection.prepareStatement(sql3);
				preparedStatement3.setFloat(1, pendingAmount);
				preparedStatement3.setInt(2, accountNumber);
				preparedStatement3.executeUpdate();
				
				String sql4 ="update accounts set accountbalance=? where accountnumber=?";
				PreparedStatement preparedStatement4 = connection.prepareStatement(sql4);
				preparedStatement4.setFloat(1, newBalance);
				preparedStatement4.setInt(2, accountNumber);
				preparedStatement4.executeUpdate();
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Interacting with Database");
		}return;
	}
	
	@Override//working on
	public void denyPendingTransfer(int accountNumber) throws BusinessException {
		
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			String sql = "select pendingamt,fromaccount from pendingtransfers where toaccount=?";	
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, accountNumber);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				float pendingAmount = resultSet.getFloat("pendingamt");
				int fromAccount = resultSet.getInt("fromaccount");
				
				String sql2 = "select accountbalance from accounts where accountnumber=?";
				preparedStatement = connection.prepareStatement(sql2);
				preparedStatement.setInt(1, fromAccount);
				ResultSet resultSet2 = preparedStatement.executeQuery();
				resultSet2.next();
				
				//Arithmetic
				Float oldBalance = resultSet2.getFloat("accountbalance");
				Float newBalance = oldBalance+pendingAmount;
				
				String sql3="delete from pendingtransfers where pendingamt=? and fromaccount=?";
				preparedStatement = connection.prepareStatement(sql3);
				preparedStatement.setFloat(1, pendingAmount);
				preparedStatement.setInt(2, fromAccount);
				preparedStatement.executeUpdate();
				
				//Adding Pending Funds Back to Original Account
				String sql4 ="update accounts set accountbalance=? where accountnumber=?";
				preparedStatement = connection.prepareStatement(sql4);
				preparedStatement.setFloat(1, newBalance);
				preparedStatement.setInt(2, fromAccount);
				preparedStatement.executeUpdate();
				
				//Generate Transaction Record for sending money Back
				GeneralService dao =new GeneralServiceImpl();
				dao.GenerateTransactionRecord(oldBalance, pendingAmount, newBalance, fromAccount);
				return;
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Connecting to DB in ClientCrud "+e);
		}return;
		
	}

	@Override
	public void changePassword(int UserId, String newPassword) throws BusinessException {
		
		try (Connection connection = PostgresqlConnection.getConnection()) {
			
			String sql = "update loginverification set password=? where userid=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, newPassword);
			preparedStatement.setInt(2, UserId);
			preparedStatement.executeUpdate();
			log.info("Password successfully Changed");
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Connecting to Database ");
		}
		return;
	}

}/********************End of Class****************/ 