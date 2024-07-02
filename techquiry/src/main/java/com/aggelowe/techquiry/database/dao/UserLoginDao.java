package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_LOGIN_COUNT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_LOGIN_DELETE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_LOGIN_INSERT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_LOGIN_RANGE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_LOGIN_SELECT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_LOGIN_SELECT_USERNAME_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_LOGIN_UPDATE_SCRIPT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import com.aggelowe.techquiry.common.Utilities;
import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.Database;
import com.aggelowe.techquiry.database.DatabaseUtilities;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DaoException;
import com.aggelowe.techquiry.database.exceptions.SQLExecutionException;

/**
 * The {@link UserLoginDao} interface provides methods to interact with the
 * database for managing user login information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class UserLoginDao {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link UserLoginDao} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private UserLoginDao() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method returns the number of user login entries inside the application
	 * database.
	 * 
	 * @return The number of user logins in the database
	 */
	public static int count() {
		LOGGER.debug("Getting user login entry count");
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_LOGIN_COUNT_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + USER_LOGIN_COUNT_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		ResultSet result = DatabaseUtilities.executeStatement(statement);
		if (result == null) {
			throw new DaoException("The first statement in " + USER_LOGIN_COUNT_SCRIPT + " did not yeild a result!");
		}
		int count;
		try {
			count = result.getInt("users_count");
		} catch (SQLException exception) {
			throw new DaoException("There was an error while retrieving the user count!", exception);
		}
		return count;
	}

	/**
	 * This method deletes the user login entry with the provided user id from the
	 * application database.
	 * 
	 * @param id The id of the user login entry
	 */
	public static void delete(int id) {
		LOGGER.debug("Deleting user with id " + id);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_LOGIN_DELETE_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + USER_LOGIN_DELETE_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		DatabaseUtilities.executeStatement(statement, id);
	}

	/**
	 * This method inserts the given {@link UserLogin} object as a new user login
	 * entry in the application database.
	 * 
	 * @param userLogin The user login to insert
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode insert(UserLogin userLogin) {
		LOGGER.debug("Inserting user login with information " + userLogin);
		int id = userLogin.getId();
		String username = userLogin.getUsername();
		byte[] passwordHash = userLogin.getPasswordHash();
		byte[] passwordSalt = userLogin.getPasswordSalt();
		String encodedHash = Utilities.encodeBase64(passwordHash);
		String encodedSalt = Utilities.encodeBase64(passwordSalt);
		try {
			List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_LOGIN_INSERT_SCRIPT);
			if (statements.size() < 1) {
				throw new DaoException("Invalid number of statements in " + USER_LOGIN_INSERT_SCRIPT + "!");
			}
			PreparedStatement statement = statements.getFirst();
			DatabaseUtilities.executeStatement(statement, id, username, encodedHash, encodedSalt);
		} catch (SQLExecutionException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while inserting the user login entry!", exception);
		}
		return null;
	}

	/**
	 * This method returns and retrieves a list of {@link UserLogin} objects from
	 * the application database, that has the given size and starts with the given
	 * offset.
	 * 
	 * @param count The number of entries
	 * @param offset The number of entries to skip
	 * @return The selected range
	 */
	public static List<UserLogin> range(int count, int offset) {
		LOGGER.debug("Getting " + count + " user login entries with offset " + offset);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_LOGIN_RANGE_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + USER_LOGIN_RANGE_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		ResultSet result = DatabaseUtilities.executeStatement(statement, offset, count);
		if (result == null) {
			throw new DaoException("The first statement in " + USER_LOGIN_RANGE_SCRIPT + " did not yeild results!");
		}
		List<UserLogin> range = new ArrayList<>(count);
		try {
			while (result.next()) {
				int id;
				String username;
				String encodedHash;
				String encodedSalt;
				try {
					id = result.getInt("user_id");
					username = result.getString("username");
					encodedHash = result.getString("password_hash");
					encodedSalt = result.getString("password_salt");
				} catch (SQLException exception) {
					throw new DaoException("There was an error while retrieving the user login information", exception);
				}
				byte[] passwordHash;
				byte[] passwordSalt;
				try {
					passwordHash = Utilities.decodeBase64(encodedHash);
					passwordSalt = Utilities.decodeBase64(encodedSalt);
				} catch (IllegalArgumentException exception) {
					throw new DaoException("There was an error while retrieving the user login information!", exception);
				}
				UserLogin userLogin = new UserLogin(id, username, passwordHash, passwordSalt);
				range.add(userLogin);
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		return range;
	}

	/**
	 * This method returns and retrieves the only {@link UserLogin} object with the
	 * given user id from the application database.
	 * 
	 * @param id The user id
	 * @return The user login with the given id
	 */
	public static UserLogin select(int id) {
		LOGGER.debug("Getting user login with user id " + id);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_LOGIN_SELECT_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + USER_LOGIN_SELECT_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		ResultSet result = DatabaseUtilities.executeStatement(statement, id);
		if (result == null) {
			throw new DaoException("The first statement in " + USER_LOGIN_SELECT_SCRIPT + " did not yeild results!");
		}
		try {
			if (!result.next()) {
				return null;
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		String username;
		String encodedHash;
		String encodedSalt;
		try {
			username = result.getString("username");
			encodedHash = result.getString("password_hash");
			encodedSalt = result.getString("password_salt");
		} catch (SQLException exception) {
			throw new DaoException("There was an error while retrieving the user login information", exception);
		}
		byte[] passwordHash;
		byte[] passwordSalt;
		try {
			passwordHash = Utilities.decodeBase64(encodedHash);
			passwordSalt = Utilities.decodeBase64(encodedSalt);
		} catch (IllegalArgumentException exception) {
			throw new DaoException("There was an error while retrieving the user login information!", exception);
		}
		UserLogin userLogin = new UserLogin(id, username, passwordHash, passwordSalt);
		return userLogin;
	}

	/**
	 * This method returns and retrieves the only {@link UserLogin} object with the
	 * given username from the application database.
	 * 
	 * @param username The username
	 * @return The user login with the given username
	 */
	public static UserLogin selectFromUsername(String username) {
		LOGGER.debug("Getting user login with username " + username);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_LOGIN_SELECT_USERNAME_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + USER_LOGIN_SELECT_USERNAME_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		ResultSet result = DatabaseUtilities.executeStatement(statement, username);
		if (result == null) {
			throw new DaoException("The first statement in " + USER_LOGIN_SELECT_USERNAME_SCRIPT + " did not yeild results!");
		}
		try {
			if (!result.next()) {
				return null;
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		int id;
		String encodedHash;
		String encodedSalt;
		try {
			id = result.getInt("user_id");
			encodedHash = result.getString("password_hash");
			encodedSalt = result.getString("password_salt");
		} catch (SQLException exception) {
			throw new DaoException("There was an error while retrieving the user login information", exception);
		}
		byte[] passwordHash;
		byte[] passwordSalt;
		try {
			passwordHash = Utilities.decodeBase64(encodedHash);
			passwordSalt = Utilities.decodeBase64(encodedSalt);
		} catch (IllegalArgumentException exception) {
			throw new DaoException("There was an error while retrieving the user login information!", exception);
		}
		UserLogin userLogin = new UserLogin(id, username, passwordHash, passwordSalt);
		return userLogin;
	}

	/**
	 * This method replaces the information of a user login entry with the data
	 * contained in the {@link UserLogin} object, using the user id from the object
	 * to select the correct entry.
	 * 
	 * @param userLogin The user login to update
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode update(UserLogin userLogin) {
		LOGGER.debug("Updating user login with data " + userLogin);
		int id = userLogin.getId();
		String username = userLogin.getUsername();
		byte[] passwordHash = userLogin.getPasswordHash();
		byte[] passwordSalt = userLogin.getPasswordSalt();
		String encodedHash = Utilities.encodeBase64(passwordHash);
		String encodedSalt = Utilities.encodeBase64(passwordSalt);
		try {
			List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_LOGIN_UPDATE_SCRIPT);
			if (statements.size() < 1) {
				throw new DaoException("Invalid number of statements in " + USER_LOGIN_UPDATE_SCRIPT + "!");
			}
			PreparedStatement statement = statements.getFirst();
			DatabaseUtilities.executeStatement(statement, username, encodedHash, encodedSalt, id);
		} catch (SQLExecutionException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while updating the user login entry!", exception);
		}
		return null;
	}

}
