package com.app.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;
import com.app.dao.dbutil.PostgresqlConnection;
import com.app.dao.impl.EmployeeCrudDAOImpl;
import com.app.exception.BusinessException;
import com.app.service.GeneralService;

public class GeneralServiceImpl implements GeneralService {
	
	//initiating objects needed globally
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM-dd-yyyy z  HH:mm:ss");
		Logger log=Logger.getLogger(EmployeeCrudDAOImpl.class);
		public Date d=new Date();
		Scanner sc = new Scanner(System.in);
	
	public int getNextTransactionId() throws BusinessException {
		
		try (Connection connection = PostgresqlConnection.getConnection()) {
			int transid=100001; //initial transid
			String sql = "select transid from transaction";
			PreparedStatement preparedStatement1 = connection.prepareStatement(sql);	
			ResultSet resultSet = preparedStatement1.executeQuery();
			while (resultSet.next()) {
				transid++;//increments to next transId number
			}
			return transid;
		} catch (SQLException | ClassNotFoundException e) {
		throw new BusinessException("Error Connecting to Database thru Services");
		}
	}

	@Override
	public int getNextAccountNumber() throws BusinessException {
		
		int accountNumber = 100000111; //acount numbers start at
		try {
			Connection connection = PostgresqlConnection.getConnection();
			String sql="select accountnumber from accounts";
			PreparedStatement preparedStatement1 = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement1.executeQuery();
			while (resultSet.next()) {
				accountNumber++;  //increments to next available Account number
			}
			return accountNumber;
		}
		catch (Exception e) {
			throw new BusinessException("Error Connecting to Database thru Services");
		}
	}//END of getNextAccoutNumber()

	@Override
	public boolean CheckUsernameAvailability(String username) throws BusinessException {
		
		boolean b = false;
		try(Connection connection = PostgresqlConnection.getConnection()){
			
			String sql = "select username from loginverification";
			PreparedStatement preparedStatement=connection.prepareStatement(sql);
			ResultSet resultSet=preparedStatement.executeQuery();
			while(resultSet.next()) {
				String DBUsername = resultSet.toString().toLowerCase();
				b = username.toLowerCase().equals(DBUsername);
				if (b == true) {
					return b;
				}
			}
			return b;
		} catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Connecting to Database.");
		}
	}  //End of CheckUsernameAvailability

	@Override
	public void CreateNewUser(String name, String address, long contact, String username, String password) throws BusinessException {
		try(Connection connection = PostgresqlConnection.getConnection()){
			
			//generates next available userId available from database
			int userId = 1000;	//client IDs Start at 1000;
			
			String sql = "select userid from client";
			PreparedStatement preparedStatement=connection.prepareStatement(sql);
			ResultSet resultSet=preparedStatement.executeQuery();
			while(resultSet.next()) {
				userId++; //++1 for each client found 
			}
			String sql2 = "insert into client(userid,name,address,contact) values(?,?,?,?)";
			preparedStatement = connection.prepareStatement(sql2);
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, address);
			preparedStatement.setLong(4, contact);
			preparedStatement.executeUpdate();
			
			String sql3= "insert into loginverification(userid,username,password) values(?,?,?)";
			preparedStatement = connection.prepareStatement(sql3);
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, username);
			preparedStatement.setString(3, password);
			preparedStatement.executeUpdate();
		}catch (SQLException | ClassNotFoundException e) {
			throw new BusinessException("Error Connecting to Database.");
		}
	}//End of CreateNewUser()

	@Override	
	public void CreateNewAccountClient(int UserId, int accountNumber, float transferAmount) throws BusinessException {
		
		GeneralService dao = new GeneralServiceImpl();
		
			try(Connection connection = PostgresqlConnection.getConnection()){
				
				String sql = "insert into newaccounts(userid,accountnumber,accountbalance) values(?,?,?)";
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, UserId);
				preparedStatement.setInt(2, accountNumber);
				preparedStatement.setFloat(3, transferAmount);
				preparedStatement.executeUpdate();
				
				String sql1= "insert into accounts(userid,accountnumber,accountbalance) values(?,?,?)";
				preparedStatement = connection.prepareStatement(sql1);
				preparedStatement.setInt(1, UserId);
				preparedStatement.setInt(2, accountNumber);
				preparedStatement.setFloat(3, 0-transferAmount); //this is reference point mentioned in EmployeeCrudDAOImpl
				preparedStatement.executeUpdate();
				
				//removes pendingtransfers record because it is in newaccounts table & dont want to Give away free Money
				String sql2= "delete from pendingtransfers where toaccount=?";
				preparedStatement = connection.prepareStatement(sql2);
				preparedStatement.setInt(1, accountNumber);
				preparedStatement.executeUpdate();
				
				dao.GenerateTransactionRecord(0, transferAmount, transferAmount, accountNumber);
				log.info("Account Created Successfully\n");
			} catch (ClassNotFoundException | SQLException e) {
				throw new BusinessException("Error Connecting to Database.");
			}
	}

	@Override
	public void CreateNewAccountEmployee(int userId) throws BusinessException {
		
		GeneralService dao = new GeneralServiceImpl();
		log.info("Generic Bank's Minimum amount to start an account is $500\n");
		log.info("Enter amount Received from Client:");
		float accountBalance = 0;
		try {
			accountBalance = Float.parseFloat(sc.nextLine());
		}catch (NumberFormatException e) {
			log.info("Must be Dollar ($) Amount\n");
			return;
		}
		if (accountBalance < 500) {
			log.info("\nSorry, thats not enough to start an account.");
			return;
		}else {
			int accountNumber=dao.getNextAccountNumber();
			try(Connection connection = PostgresqlConnection.getConnection()){
				String sql = "insert into accounts(userid,accountnumber,accountbalance) values(?,?,?)";
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, userId);
				preparedStatement.setInt(2, accountNumber);
				preparedStatement.setFloat(3, accountBalance);
				preparedStatement.executeUpdate();
				
				dao.GenerateTransactionRecord(0, accountBalance, accountBalance, accountNumber);
				log.info("Account Created Successfully");
			} catch (ClassNotFoundException | SQLException e) {
				throw new BusinessException("Error Connecting to Database.");
			}
		}
	}
	
	@Override
	public void GenerateTransactionRecord(float oldBalance, float transAmount, float newBalance, int accountNumber) throws BusinessException {
		GeneralService dao = new GeneralServiceImpl();
		try(Connection connection = PostgresqlConnection.getConnection()){
				
			//generates next transId
			int transId = dao.getNextTransactionId();
			
			//inserting transaction record
			String sql = "insert into transaction(transid,oldbalance,transamount,newbalance,accountnumber,timestamp) values(?,?,?,?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, transId);
			preparedStatement.setFloat(2, oldBalance);
			preparedStatement.setFloat(3, transAmount);
			preparedStatement.setFloat(4, newBalance);
			preparedStatement.setInt(5, accountNumber);
			preparedStatement.setString(6, sdf.format(d));
			preparedStatement.executeUpdate();		
		}catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException("Error Connecting to Database thru Services "+e);
		}
	}

}/********************End of Class****************/