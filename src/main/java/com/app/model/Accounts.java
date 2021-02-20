package com.app.model;

public class Accounts {
	int userId;
	int accountNumber;
	float accountBalance;
	
	public Accounts(int userId, int accountNumber, float accountBalance) {
		super();
		this.userId = userId;
		this.accountNumber = accountNumber;
		this.accountBalance = accountBalance;
	}

	public Accounts() {
		super();
	}

	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public float getAccountBalance() {
		return accountBalance;
	}
	
	public void setAccountBalance(float accountBalance) {
		this.accountBalance = accountBalance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(accountBalance);
		result = prime * result + accountNumber;
		result = prime * result + userId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Accounts other = (Accounts) obj;
		if (Float.floatToIntBits(accountBalance) != Float.floatToIntBits(other.accountBalance))
			return false;
		if (accountNumber != other.accountNumber)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[Account Number : " + accountNumber + ", Balance : " + accountBalance + "]";
	}
}/********************End of Class****************/
