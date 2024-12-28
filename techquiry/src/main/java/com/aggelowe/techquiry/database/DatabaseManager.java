package com.aggelowe.techquiry.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.common.Environment;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;

import lombok.extern.log4j.Log4j2;

/**
 * The {@link DatabaseManager} class is the one responsible for initializing the
 * database used by the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@Log4j2
public final class DatabaseManager {

	/**
	 * The path of the SQL script for applying the database schema.
	 */
	public static final String CREATE_SCHEMA_SCRIPT = "/database/schema.sql";

	/**
	 * This object is responsible for executing SQL scripts on the application
	 * database
	 */
	private final SQLRunner runner;

	/**
	 * This constructor constructs a new {@link DatabaseManager} instance with the
	 * provided {@link SQLRunner} as the interface between the application and the
	 * database.
	 * 
	 * @param runner The runner for interfacing with the database
	 */
	@Autowired
	public DatabaseManager(SQLRunner runner) {
		this.runner = runner;
	}

	/**
	 * This method applies the database schema to the database if the respective
	 * environment variable is true.
	 * 
	 * @throws DatabaseException If an error occurs while creating the schema.
	 */
	public void createSchema() throws DatabaseException {
		log.debug("Applying database schema");
		runner.runScript(CREATE_SCHEMA_SCRIPT);
	}
	
	/**
	 * The {@link #initialize()} method is responsible for initializing the database
	 * used by the application. When invoked, the method connects to the database
	 * file and performs the necessary initialization operations.
	 */
	public void initialize() {
		if (Environment.getSetup()) {
			try {
				createSchema();
			} catch (DatabaseException exception) {
				log.fatal("An error occured while applying the database schema!", exception);
				System.exit(1);
			}
		}
	}

}
