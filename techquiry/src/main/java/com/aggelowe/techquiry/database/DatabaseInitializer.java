package com.aggelowe.techquiry.database;

import static com.aggelowe.techquiry.common.Constants.DATABASE_FILENAME;
import static com.aggelowe.techquiry.common.Constants.EXECUTION_DIRECTORY;
import static com.aggelowe.techquiry.common.Constants.LOGGER;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.users.UserAdapter;

/**
 * The {@link DatabaseInitializer} class is the one responsible for initializing
 * the database used by the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class DatabaseInitializer {

	/**
	 * This objects represents the connection with the SQLite database.
	 */
	private static Connection connection = null;

	/**
	 * This constructor will throw an {@link ConstructorException} whenever
	 * invoked. {@link DatabaseInitializer} objects should <b>not</b> be
	 * constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private DatabaseInitializer() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * The {@link #initialize()} method is responsible for initializing the database
	 * used by the application. When invoked, the method connects to the database
	 * file and performs the necessary initialization operations.
	 */
	public static void initialize() {
		LOGGER.info("Initializing application database");
		connect();
		makeTables();
	}

	/**
	 * This method establishes the connection between the database file and the
	 * application. If an error occurs while connecting, the application will exit.
	 */
	private static void connect() {
		LOGGER.debug("Establishing database connection");
		Path databasePath = EXECUTION_DIRECTORY.toPath().resolve(DATABASE_FILENAME);
		String databaseUrl = "jdbc:sqlite:" + databasePath;
		LOGGER.debug("Database URL: " + databaseUrl);
		try {
			connection = DriverManager.getConnection(databaseUrl);
		} catch (SQLException exception) {
			LOGGER.error("An error occured while connecting to " + databaseUrl, exception);
			System.exit(1);
		}
	}

	/**
	 * This method creates the database tables that contain the data used by the
	 * application if they do not already exist within the database. If an error
	 * occurs during this process, the application will exit.
	 */
	private static void makeTables() {
		LOGGER.debug("Creating missing database tables");
		UserAdapter.createUserTable();
	}

	/**
	 * This method returns the {@link Connection} object linked to the application's
	 * SQLite database file.
	 * 
	 * @return The connection with the database file
	 */
	static Connection getConnection() {
		return connection;
	}

}
