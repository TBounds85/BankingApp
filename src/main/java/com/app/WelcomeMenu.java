package com.app;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.log4j.Logger;


import com.app.exception.BusinessException;
import com.app.menu.CreateNewUserMenu;
import com.app.menu.LoginVerificationMenu;

public interface WelcomeMenu {

	public static void main(String[] args) throws BusinessException {
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		Logger log=Logger.getLogger(WelcomeMenu.class);
		int choice=0;
		do {
			log.info("");
			log.info("Welcome to Generic Bank App!");
			log.info("----------------------------");
			log.info("	Welcome Menu");
			log.info("----------------------------");
			log.info("	1) Login");
			log.info("	2) Create New User");
			log.info("	3) Close App");
			log.info("----------------------------");
			log.info("\nPlease enter Your Choice: ");

			try {
				choice = Integer.parseInt(sc.nextLine());
			}catch (NumberFormatException | NoSuchElementException e) {
				log.info("\nInvalid Option Chosen. Please Try Again...\n");}
			
			switch (choice) {
						
			case 1:
				LoginVerificationMenu.LoginVerification();
			break;
			
			case 2:
				CreateNewUserMenu.CreateNewUser();
			break;
			
			case 3:
				log.info("Thank You for using Generic Banking App!!");
				log.info("\nLooking Forward to Serving you Again! =)");
			break;
			
			default:
				log.info("\nInvalid Option Chosen. Please Try Again...\n");
			break;}//End of Switch
			
		} while (choice != 3);}//End of Switch	
}/********************End of Interface****************/ 

