package com.aggelowe.techquiry.database;

import static com.aggelowe.techquiry.common.Constants.DATABASE_FILENAME;
import static com.aggelowe.techquiry.common.Constants.LOGGER;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

import com.aggelowe.techquiry.common.Environment;
import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.dao.ObserverDao;
import com.aggelowe.techquiry.database.dao.ResponseDao;
import com.aggelowe.techquiry.database.dao.UpvoteDao;
import com.aggelowe.techquiry.database.dao.UserDataDao;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entities.Inquiry;
import com.aggelowe.techquiry.database.entities.Observer;
import com.aggelowe.techquiry.database.entities.Response;
import com.aggelowe.techquiry.database.entities.Upvote;
import com.aggelowe.techquiry.database.entities.UserData;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;

/**
 * The {@link DatabaseManager} class is the one responsible for initializing the
 * database used by the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class DatabaseManager {

	/**
	 * The path of the SQL script for applying the database schema.
	 */
	public static final String CREATE_SCHEMA_SCRIPT = "/database/schema.sql";

	/**
	 * This object represents the connection with the SQLite database.
	 */
	private final Connection connection;

	/**
	 * This object is responsible for initializing and managing the database of the
	 * application.
	 */
	private static DatabaseManager manager;

	/**
	 * This object is responsible for executing SQL scripts on the application
	 * database
	 */
	private final SQLRunner runner;

	/**
	 * The object responsible for handling the data access for {@link Inquiry}
	 * objects.
	 */
	private final InquiryDao inquiryDao;

	/**
	 * The object responsible for handling the data access for {@link Observer}
	 * objects.
	 */
	private final ObserverDao observerDao;

	/**
	 * The object responsible for handling the data access for {@link Response}
	 * objects.
	 */
	private final ResponseDao responseDao;

	/**
	 * The object responsible for handling the data access for {@link Upvote}
	 * objects.
	 */
	private final UpvoteDao upvoteDao;

	/**
	 * The object responsible for handling the data access for {@link UserData}
	 * objects.
	 */
	private final UserDataDao userDataDao;

	/**
	 * The responsible for handling the data access for {@link UserLoginDao}
	 * objects.
	 */
	private final UserLoginDao userLoginDao;

	/**
	 * This constructor constructs a new {@link DatabaseManager} instance with the
	 * provided connection as the interface between the application and the
	 * database.
	 * 
	 * @param connection The database connection
	 */
	public DatabaseManager(Connection connection) {
		this.connection = connection;
		this.runner = new SQLRunner(connection);
		this.inquiryDao = new InquiryDao(runner);
		this.observerDao = new ObserverDao(runner);
		this.responseDao = new ResponseDao(runner);
		this.upvoteDao = new UpvoteDao(runner);
		this.userDataDao = new UserDataDao(runner);
		this.userLoginDao = new UserLoginDao(runner);
	}

	/**
	 * This method applies the database schema to the database if the respective
	 * environment variable is true.
	 * 
	 * @throws DatabaseException If an error occurs while creating the schema.
	 */
	public void createSchema() throws DatabaseException {
		LOGGER.debug("Applying database schema");
		runner.runScript(CREATE_SCHEMA_SCRIPT);
	}

	/**
	 * This method loses the connection with the application's database.
	 * 
	 * @throws DatabaseException If an error occurs while closing the connection.
	 */
	public void closeConnection() throws DatabaseException {
		LOGGER.debug("Closing database connection");
		try {
			connection.close();
		} catch (SQLException exception) {
			throw new DatabaseException("An error occured while closing the database connection!", exception);
		}
	}

	/**
	 * This method returns the {@link SQLRunner} responsible for executing SQL
	 * scripts on the application database
	 * 
	 * @return The application's {@link SQLRunner}
	 */
	SQLRunner getRunner() {
		return runner;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link Inquiry} objects.
	 * 
	 * @return The {@link Inquiry} data access object
	 */
	public InquiryDao getInquiryDao() {
		return inquiryDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link Observer} objects.
	 * 
	 * @return The {@link Observer} data access object
	 */
	public ObserverDao getObserverDao() {
		return observerDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link Response} objects.
	 * 
	 * @return The {@link Response} data access object
	 */
	public ResponseDao getResponseDao() {
		return responseDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link Upvote} objects.
	 * 
	 * @return The {@link Upvote} data access object
	 */
	public UpvoteDao getUpvoteDao() {
		return upvoteDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link UserData} objects.
	 * 
	 * @return The {@link UserData} data access object
	 */
	public UserDataDao getUserDataDao() {
		return userDataDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link UserLogin} objects.
	 * 
	 * @return The {@link UserLogin} data access object
	 */
	public UserLoginDao getUserLoginDao() {
		return userLoginDao;
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
			LOGGER.fatal("An error occured while connecting to " + databaseUrl, exception);
			System.exit(1);
		}
		manager = new DatabaseManager(connection);
		if (Environment.getSetup()) {
			try {
				manager.createSchema();
			} catch (DatabaseException exception) {
				LOGGER.fatal("An error occured while applying the database schema!", exception);
				System.exit(1);
			}
		}
	}

	/**
	 * This method returns the {@link DatabaseManager} responsible for initializing
	 * and managing the database of the application.
	 * 
	 * @return The application's {@link DatabaseManager}
	 */
	public static DatabaseManager getManager() {
		return manager;
	}

}
