package com.app.menu;

import java.util.Scanner;

import org.apache.log4j.Logger;
import com.app.exception.BusinessException;
import com.app.service.GeneralService;
import com.app.service.impl.GeneralServiceImpl;

public interface CreateNewUserMenu {

	Scanner sc = new Scanner(System.in);
	Logger log = Logger.getLogger(CreateNewUserMenu.class);

	static void CreateNewUser() throws BusinessException {

		String username;
		boolean checker = false;
		GeneralService dao = new GeneralServiceImpl();

		log.info("With the Creation of a User Account you");
		log.info("Will have Access to our Wonderful App.\n");

		log.info("Please Enter Your Name");
		String name = sc.nextLine();
		
		log.info("Please Enter Your Address");
		String address = sc.nextLine();
		
		log.info("Please Enter Your 10 digit Phone Number. (no dashes (-) please) ");
		Long contact;
		try {
			contact = Long.parseLong(sc.nextLine() + "L");
		} catch (NumberFormatException e) {
			log.info("Invalid Entry: Contact Set to Default 99999999999");
			contact = 9999999999L;
		}
		do {
			log.info("Please Enter a New Username (Case Sensitive)");
			username = sc.nextLine();
			checker = dao.CheckUsernameAvailability(username);
			if (checker == true) {
				log.info(username + " is Unavailable.\n");
			}
		} while (checker == true);
		log.info("\nPlease Enter Your New Password (Case Sensitive)");
		String password = sc.nextLine();
		dao.CreateNewUser(name, address, contact, username, password);
		log.info("\nCreated New User Successfully!!");
		log.info("	Please Login =)\n");
		return;
	}
}/******************** End of Interface ****************/
