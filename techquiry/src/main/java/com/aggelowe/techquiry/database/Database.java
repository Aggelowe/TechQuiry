package com.aggelowe.techquiry.database;

import static com.aggelowe.techquiry.common.Constants.DATABASE_FILENAME;
import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.CREATE_SCHEMA_SCRIPT;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.sqlite.SQLiteConfig;

import com.aggelowe.techquiry.common.Environment;
import com.aggelowe.techquiry.common.exceptions.ConstructorException;

/**
 * The {@link Database} class is the one responsible for initializing the
 * database used by the TechQuiry application.
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
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link Database} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private Database() {
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
		try {
			connection = DriverManager.getConnection(databaseUrl, config.toProperties());
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
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(connection, CREATE_SCHEMA_SCRIPT);
		DatabaseUtilities.executeStatements(statements);
	}

	/**
	 * This method returns the {@link Connection} object linked to the application's
	 * SQLite database file.
	 * 
	 * @return The connection with the database file
	 */
	public static Connection getConnection() {
		return connection;
	}

}
