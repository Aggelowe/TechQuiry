package com.aggelowe.techquiry.service;

import static com.aggelowe.techquiry.common.Constants.LOGGER;

import com.aggelowe.techquiry.database.DatabaseManager;

/**
 * The {@link ServiceManager} class is the one responsible for initializing and
 * managing the services used by the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class ServiceManager {

	/**
	 * The instance of the {@link ServiceManager} class.
	 */
	private static ServiceManager instance;

	/**
	 * This constructor constructs a new {@link ServiceManager} instance with the
	 * provided database manager as the interface between the application and the
	 * database.
	 * 
	 * @param databaseManager The database manager
	 */
	public ServiceManager(DatabaseManager databaseManager) {

	}

	/**
	 * This method is responsible for initializing the services used by the
	 * application.
	 */
	public static ServiceManager initialize(DatabaseManager databaseManager) {
		LOGGER.info("Initializing application services");
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