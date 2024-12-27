package com.aggelowe.techquiry.service;

import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.entities.Inquiry;
import com.aggelowe.techquiry.database.entities.UserData;
import com.aggelowe.techquiry.database.entities.UserLogin;

import lombok.extern.log4j.Log4j2;

/**
 * The {@link ServiceManager} class is the one responsible for initializing and
 * managing the services used by the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Log4j2
public class ServiceManager {

	/**
	 * The instance of the {@link ServiceManager} class.
	 */
	private static ServiceManager instance;

	/**
	 * The object responsible for managing the {@link Inquiry} operations.
	 */
	private final InquiryService inquiryService;

	/**
	 * The object responsible for managing the {@link UserData} operations.
	 */
	private final UserDataService userDataService;

	/**
	 * The object responsible for managing the {@link UserLogin} operations.
	 */
	private final UserLoginService userLoginService;

	/**
	 * This constructor constructs a new {@link ServiceManager} instance with the
	 * provided database manager as the interface between the application and the
	 * database.
	 * 
	 * @param databaseManager The database manager
	 */
	public ServiceManager(DatabaseManager databaseManager) {
		this.inquiryService = new InquiryService(databaseManager);
		this.userDataService = new UserDataService(databaseManager);
		this.userLoginService = new UserLoginService(databaseManager);
	}

	/**
	 * This method returns the object responsible for managing the inquiry
	 * operations.
	 * 
	 * @return The {@link Inquiry} service object
	 */
	public InquiryService getInquiryService() {
		return inquiryService;
	}

	/**
	 * This method returns the object responsible for managing the user data
	 * operations.
	 * 
	 * @return The {@link UserData} service object
	 */
	public UserDataService getDataService() {
		return userDataService;
	}

	/**
	 * This method returns the object responsible for managing the user login
	 * operations.
	 * 
	 * @return The {@link UserLogin} service object
	 */
	public UserLoginService getUserLoginService() {
		return userLoginService;
	}

	/**
	 * This method is responsible for initializing the services used by the
	 * application.
	 */
	public static ServiceManager initialize(DatabaseManager databaseManager) {
		log.info("Initializing application services");
		instance = new ServiceManager(databaseManager);
		return instance;
	}

	/**
	 * This method returns the {@link ServiceManager} responsible for initializing
	 * and managing the services of the application.
	 * 
	 * @return The application's {@link ServiceManager}
	 */
	public static ServiceManager getInstance() {
		return instance;
	}

}
