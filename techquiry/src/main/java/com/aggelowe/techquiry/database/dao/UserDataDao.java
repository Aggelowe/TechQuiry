package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_COUNT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_DELETE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_INSERT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_RANGE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_SELECT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_UPDATE_SCRIPT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.Database;
import com.aggelowe.techquiry.database.DatabaseUtilities;
import com.aggelowe.techquiry.database.entities.UserData;
import com.aggelowe.techquiry.database.exceptions.DaoException;
import com.aggelowe.techquiry.database.exceptions.SQLExecutionException;

/**
 * The {@link UserDataDao} interface provides methods to interact with the
 * database for managing user data information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class UserDataDao {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link UserDataDao} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private UserDataDao() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method returns the number of user data entries inside the application
	 * database.
	 * 
	 * @return The number of user data entries in the database
	 */
	public static int count() {
		LOGGER.debug("Getting user data entry count");
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_DATA_COUNT_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + USER_DATA_COUNT_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		ResultSet result = DatabaseUtilities.executeStatement(statement);
		if (result == null) {
			throw new DaoException("The first statement in " + USER_DATA_COUNT_SCRIPT + " did not yeild a result!");
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
	 * This method deletes the user data entry with the provided user id from the
	 * application database.
	 * 
	 * @param id The id of the user data entry
	 */
	public static void delete(int id) {
		LOGGER.debug("Deleting user with id " + id);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_DATA_DELETE_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + USER_DATA_DELETE_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		DatabaseUtilities.executeStatement(statement, id);
	}

	/**
	 * This method inserts the given {@link UserData} object as a new user data
	 * entry in the application database.
	 * 
	 * @param userData The user data to insert
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode insert(UserData userData) {
		LOGGER.debug("Inserting user data with information " + userData);
		int id = userData.getId();
		String firstName = userData.getFirstName();
		String lastName = userData.getLastName();
		byte[] icon = userData.getIcon();
		try {
			List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_DATA_INSERT_SCRIPT);
			if (statements.size() < 1) {
				throw new DaoException("Invalid number of statements in " + USER_DATA_INSERT_SCRIPT + "!");
			}
			PreparedStatement statement = statements.getFirst();
			DatabaseUtilities.executeStatement(statement, id, firstName, lastName, icon);
		} catch (SQLExecutionException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while inserting the user data entry!", exception);
		}
		return null;
	}

	/**
	 * This method returns and retrieves a list of {@link UserData} objects from
	 * the application database, that has the given size and starts with the given
	 * offset.
	 * 
	 * @param count  The number of entries
	 * @param offset The number of entries to skip
	 * @return The selected range
	 */
	public static List<UserData> range(int count, int offset) {
		LOGGER.debug("Getting " + count + " user data entries with offset " + offset);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_DATA_RANGE_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + USER_DATA_RANGE_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		ResultSet result = DatabaseUtilities.executeStatement(statement, offset, count);
		if (result == null) {
			throw new DaoException("The first statement in " + USER_DATA_RANGE_SCRIPT + " did not yeild results!");
		}
		List<UserData> range = new ArrayList<>(count);
		try {
			while (result.next()) {
				int id;
				String firstName;
				String lastName;
				byte[] icon;
				try {
					id = result.getInt("user_id");
					firstName = result.getString("first_name");
					lastName = result.getString("last_name");
					icon = result.getBytes("icon");
				} catch (SQLException exception) {
					throw new DaoException("There was an error while retrieving the user data information", exception);
				}
				UserData userData = new UserData(id, firstName, lastName);
				userData.setIcon(icon);
				range.add(userData);
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		return range;
	}
	
	/**
	 * This method returns and retrieves the only {@link UserData} object with the
	 * given user id from the application database.
	 * 
	 * @param id The user id
	 * @return The user data with the given id
	 */
	public static UserData select(int id) {
		LOGGER.debug("Getting user data with user id " + id);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_DATA_SELECT_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + USER_DATA_SELECT_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		ResultSet result = DatabaseUtilities.executeStatement(statement, id);
		if (result == null) {
			throw new DaoException("The first statement in " + USER_DATA_SELECT_SCRIPT + " did not yeild results!");
		}
		try {
			if (!result.next()) {
				return null;
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		String firstName;
		String lastName;
		byte[] icon;
		try {
			id = result.getInt("user_id");
			firstName = result.getString("first_name");
			lastName = result.getString("last_name");
			icon = result.getBytes("icon");
		} catch (SQLException exception) {
			throw new DaoException("There was an error while retrieving the user data information", exception);
		}
		UserData userData = new UserData(id, firstName, lastName);
		userData.setIcon(icon);
		return userData;
	}

	/**
	 * This method replaces the information of a user data entry with the data
	 * contained in the {@link UserData} object, using the user id from the object
	 * to select the correct entry.
	 * 
	 * @param userData The user data to update
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode update(UserData userData) {
		LOGGER.debug("Updating user data with data " + userData);
		int id = userData.getId();
		String firstName = userData.getFirstName();
		String lastName = userData.getLastName();
		byte[] icon = userData.getIcon();
		try {
			List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), USER_DATA_UPDATE_SCRIPT);
			if (statements.size() < 1) {
				throw new DaoException("Invalid number of statements in " + USER_DATA_UPDATE_SCRIPT + "!");
			}
			PreparedStatement statement = statements.getFirst();
			DatabaseUtilities.executeStatement(statement, firstName, lastName, icon, id);
		} catch (SQLExecutionException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while updating the user data entry!", exception);
		}
		return null;
	}
	
}
