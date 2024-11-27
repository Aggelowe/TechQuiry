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
import com.aggelowe.techquiry.common.exceptions.ConstructorException;

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
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link Database} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private DatabaseManager() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
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
		try (Connection connection = DriverManager.getConnection(databaseUrl, config.toProperties());) {
			connection.setAutoCommit(false);
			runner = new SQLRunner(connection);
		} catch (SQLException exception) {
			LOGGER.error("An error occured while connecting to " + databaseUrl, exception);
			System.exit(1);
		}
		if (Environment.getSetup()) {
			applySchema();
		}
	}

	/**
	 * This method applies the TechQuiry database schema to the application's
	 * database. If an error occurs during this process, the application will exit.
	 */
	private static void applySchema() {
		LOGGER.debug("Applying database schema");
		runner.runScript(CREATE_SCHEMA_SCRIPT);
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
