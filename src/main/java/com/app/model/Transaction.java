package com.app.model;

public class Transaction {
	int transid;
	double oldBalance;
	double newBalance;
	double transamount;
	int accountNumber;
	String timeStamp;

	public Transaction(int transid, double oldBalance, double newBalance, double transamount, int accountNumber,
			String timeStamp) {
		super();
		this.transid = transid;
		this.oldBalance = oldBalance;
		this.newBalance = newBalance;
		this.transamount = transamount;
		this.accountNumber = accountNumber;
		this.timeStamp = timeStamp;
	}

	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getTransid() {
		return transid;
	}

	public void setTransid(int transid) {
		this.transid = transid;
	}

	public double getOldBalance() {
		return oldBalance;
	}

	public void setOldBalance(float oldBalance) {
		this.oldBalance = oldBalance;
	}

	public double getNewBalance() {
		return newBalance;
	}

	public void setNewBalance(float newBalance) {
		this.newBalance = newBalance;
	}

	public double getTransamount() {
		return transamount;
	}

	public void setTransamount(float transamount) {
		this.transamount = transamount;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String string) {
		this.timeStamp = string;
	}

	@Override
	public String toString() {
		return "Transaction [transid= " + transid + ", oldBalance= " + oldBalance + ", transamount= " + transamount
				+ ", newBalance= " + newBalance + ", timeStamp= " + timeStamp + "]";
	}
}/******************** End of Class ****************/
