package com.aggelowe.techquiry.database;

import static com.aggelowe.techquiry.Reference.DATABASE_FILENAME;
import static com.aggelowe.techquiry.Reference.EXECUTION_DIRECTORY;
import static com.aggelowe.techquiry.Reference.LOGGER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.aggelowe.techquiry.exception.InvalidConstructionException;

/**
 * The {@link Database} class is the one responsible for initializing and
 * handling the database used by the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class Database {

	/**
	 * This objects represents the connection with the SQLite database.
	 */
	private static Connection connection = null;

	/**
	 * This constructor will throw an {@link InvalidConstructionException} whenever
	 * invoked. {@link Database} objects should <b>not</b> be constructible.
	 * 
	 * @throws InvalidConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private Database() {
		throw new InvalidConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * The {@link #initialize()} method is responsible for initializing the database
	 * used by the application. When invoked, the method connects to the database
	 * file and performs the necessary initialization operations.
	 */
	public static void initialize() {
		LOGGER.info("Initializing application database");
		connectDatabase();
	}

	/**
	 * This method establishes the connection between the database file and the
	 * application. If an error occurs while connecting, the application will exit.
	 */
	private static void connectDatabase() {
		LOGGER.debug("Establishing database connection");
		String databasePath = EXECUTION_DIRECTORY.toPath().resolve(DATABASE_FILENAME).toString();
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
	 * This method returns the {@link Connection} object linked to the application's
	 * SQLite database file.
	 * 
	 * @return The connection with the database file
	 */
	static Connection getConnection() {
		return connection;
	}

}
