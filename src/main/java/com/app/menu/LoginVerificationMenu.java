package com.app.menu;


import java.util.Scanner;

import org.apache.log4j.Logger;

import com.app.dao.LoginVerificationDAO;
import com.app.dao.impl.LoginVerificationDAOImpl;
import com.app.exception.BusinessException;
import com.app.model.Client;


public interface LoginVerificationMenu  {

	Client c = new Client();
	static Logger log=Logger.getLogger(LoginVerificationMenu.class);
	static String userId = null;


	static void LoginVerification() throws BusinessException {
	
		int checker = 0;
		int counter = 5;
		LoginVerificationDAO dao = new LoginVerificationDAOImpl();	

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
	
		do {
			log.info("\nWelcome to Login Verification\n");
			log.info("Remaining Login Attempts: "+counter);
			log.info("\nPlease Enter Your Username: ");
			String un = sc.next();
			log.info("\nPlease Enter Your Password: ");
			String pass = sc.next();
	
			//determines what menu they goto by returning userId as int checker 
			checker = dao.verifyUserId(un, pass);
			
			if(checker<=999 && checker>0) {
				log.info("\nWelcome to GB Employee App!\n");
				counter=0;
				EmployeeMenu.EmployeeMenuView();
			}
			else if(checker>999 || checker==0 ){
				log.info("\nWelcome to GB Client App!\n");
				counter=0;
				ClientMenu.ClientMenuDisplay(checker);
			}
			else {
				log.info("\nInvalid Username/Password Combination.\n");
				--counter;
			}
		
		}while(counter!=0);
		
	return;
	}
}/********************End of Interface****************/
