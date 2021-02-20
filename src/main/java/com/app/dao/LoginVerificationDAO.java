package com.app.dao;

import com.app.exception.BusinessException;

public interface LoginVerificationDAO {
	
	public int verifyUserId(String username, String password) throws BusinessException;		

}/********************End of Interface****************/ 
