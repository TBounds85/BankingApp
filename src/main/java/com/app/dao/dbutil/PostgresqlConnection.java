package com.app.dao.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresqlConnection {

	private static Connection connection;
	
	private PostgresqlConnection() {
		// TODO Auto-generated constructor stub
	}
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		
		Class.forName("org.postgresql.Driver");
		String url="jdbc:postgresql://localhost:5432/postgres?currentSchema=bankapp";
		String username="postgres";
		String password="P@$sWord1985";
		connection=DriverManager.getConnection(url, username, password);
		return connection;

	}
}