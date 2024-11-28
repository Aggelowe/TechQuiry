package com.aggelowe.techquiry.database;

import static com.aggelowe.techquiry.common.Constants.DATABASE_FILENAME;
import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.CREATE_SCHEMA_SCRIPT;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

import com.aggelowe.techquiry.common.Environment;
import com.aggelowe.techquiry.common.exceptions.IllegalConstructionException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerException;

/**
 * The {@link DatabaseManager} class is the one responsible for initializing the
 * database used by the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class DatabaseManager {

	/**
	 * This object is responsible for executing SQL scripts on the application
	 * database
	 */
	private static SQLRunner runner;

	/**
	 * This constructor will throw an {@link IllegalConstructionException} whenever invoked.
	 * {@link Database} objects should <b>not</b> be constructible.
	 * 
	 * @throws IllegalConstructionException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private DatabaseManager() throws IllegalConstructionException {
		throw new IllegalConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * The {@link #initialize()} method is responsible for initializing the database
	 * used by the application. When invoked, the method connects to the database
	 * file and performs the necessary initialization operations.
	 */
	public static void initialize() {
		LOGGER.info("Establishing database connection");
		Path databasePath = Environment.getWorkDirectory().toPath().resolve(DATABASE_FILENAME);
		String databaseUrl = "jdbc:sqlite:" + databasePath;
		LOGGER.debug("Database URL: " + databaseUrl);
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(databaseUrl, config.toProperties());
			connection.setAutoCommit(false);
		} catch (SQLException exception) {
			LOGGER.error("An error occured while connecting to " + databaseUrl, exception);
			System.exit(1);
		}
		runner = new SQLRunner(connection);
		if (Environment.getSetup()) {
			LOGGER.debug("Applying database schema");
			try {
				runner.runScript(CREATE_SCHEMA_SCRIPT);
			} catch (SQLRunnerException exception) {
				LOGGER.error("An error occured while applying the database schema!", exception);
				System.exit(1);
			}
		}
	}

	/**
	 * This method returns the {@link SQLRunner} responsible for executing SQL
	 * scripts on the application database
	 * 
	 * @return The application's {@link SQLRunner}
	 */
	public static SQLRunner getRunner() {
		return runner;
	}

}
