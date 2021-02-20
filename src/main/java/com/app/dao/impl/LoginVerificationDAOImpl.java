package com.app.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Connection;

import com.app.dao.LoginVerificationDAO;
import com.app.dao.dbutil.PostgresqlConnection;
import com.app.exception.BusinessException;

public class LoginVerificationDAOImpl implements LoginVerificationDAO {
	
	public int verifyUserId(String username, String password) throws BusinessException {
		
		String submittedPassword=password;
		int b;
		
		try {
			Connection connection = PostgresqlConnection.getConnection();
			String sql = "select userid,password from loginverification where username=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1,username);
			ResultSet resultSet  = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				
				b= resultSet.getInt("userid");
				String pulledPassword = resultSet.getString("password");
				
				//password test
				boolean passOrFail = submittedPassword.equals(pulledPassword);
			
				if(passOrFail==true) {
					return b;
				}
			}
		} 
		catch (ClassNotFoundException | SQLException e) {
			System.out.println(e);
			throw new BusinessException("Internal Error. Admin Suspects SQL Query Malfunction.");
		}
	return -1;}
}/********************End of Class****************/ 
