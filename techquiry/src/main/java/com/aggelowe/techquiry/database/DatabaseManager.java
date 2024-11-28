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
	 * The object responsible for handling the data access for {@link Inquiry}
	 * objects.
	 */
	private static InquiryDao inquiryDao;

	/**
	 * The object responsible for handling the data access for {@link Observer}
	 * objects.
	 */
	private static ObserverDao observerDao;

	/**
	 * The object responsible for handling the data access for {@link Response}
	 * objects.
	 */
	private static ResponseDao responseDao;

	/**
	 * The object responsible for handling the data access for {@link Upvote}
	 * objects.
	 */
	private static UpvoteDao upvoteDao;

	/**
	 * The object responsible for handling the data access for {@link UserData}
	 * objects.
	 */
	private static UserDataDao userDataDao;

	/**
	 * The responsible for handling the data access for {@link UserLoginDao}
	 * objects.
	 */
	private static UserLoginDao userLoginDao;

	/**
	 * This constructor will throw an {@link IllegalConstructionException} whenever
	 * invoked. {@link Database} objects should <b>not</b> be constructible.
	 * 
	 * @throws IllegalConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
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
		makeDaos();
		createSchema();
	}

	/**
	 * This method initializes the data access objects necessary for the operation
	 * of the application.
	 */
	private static void makeDaos() {
		inquiryDao = new InquiryDao(runner);
		observerDao = new ObserverDao(runner);
		responseDao = new ResponseDao(runner);
		upvoteDao = new UpvoteDao(runner);
		userDataDao = new UserDataDao(runner);
		userLoginDao = new UserLoginDao(runner);
	}

	/**
	 * This method applies the database schema to the database if the respective
	 * environment variable is true. If the operation fails, the application will
	 * exit.
	 */
	private static void createSchema() {
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

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link Inquiry} objects.
	 * 
	 * @return The {@link Inquiry} data access object
	 */
	public static InquiryDao getInquiryDao() {
		return inquiryDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link Observer} objects.
	 * 
	 * @return The {@link Observer} data access object
	 */
	public static ObserverDao getObserverDao() {
		return observerDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link Response} objects.
	 * 
	 * @return The {@link Response} data access object
	 */
	public static ResponseDao getResponseDao() {
		return responseDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link Upvote} objects.
	 * 
	 * @return The {@link Upvote} data access object
	 */
	public static UpvoteDao getUpvoteDao() {
		return upvoteDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link UserData} objects.
	 * 
	 * @return The {@link UserData} data access object
	 */
	public static UserDataDao getUserDataDao() {
		return userDataDao;
	}

	/**
	 * This method returns the object responsible for handling the data access for
	 * {@link UserLogin} objects.
	 * 
	 * @return The {@link UserLogin} data access object
	 */
	public static UserLoginDao getUserLoginDao() {
		return userLoginDao;
	}

}
