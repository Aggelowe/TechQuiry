package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_COUNT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_DELETE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_INSERT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_RANGE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_SELECT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.USER_DATA_UPDATE_SCRIPT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteErrorCode;

import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.UserData;
import com.aggelowe.techquiry.database.exceptions.DataAccessException;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerLoadException;

/**
 * The {@link UserDataDao} interface provides methods to interact with the
 * database for managing user data information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class UserDataDao {

	/**
	 * The runner responsible for executing the SQL scripts.
	 */
	private final SQLRunner runner;

	/**
	 * This constructor constructs a new {@link UserDataDao} instance that is
	 * responsible for handling the data access for {@link UserData} objects.
	 * 
	 * @param runner The SQL script runner
	 */
	public UserDataDao(SQLRunner runner) {
		this.runner = runner;
	}

	/**
	 * This method returns the number of user data entries inside the application
	 * database.
	 * 
	 * @return The number of user data entries in the database
	 * @throws DatabaseException If an error occurs while retrieving the user count
	 */
	public int count() throws DatabaseException {
		LOGGER.debug("Getting user data entry count");
		List<ResultSet> results = runner.runScript(USER_DATA_COUNT_SCRIPT);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + USER_DATA_COUNT_SCRIPT + " did not yeild a result!");
		} else {
			result = results.getFirst();
		}
		int count;
		try {
			count = result.getInt("users_count");
		} catch (SQLException exception) {
			throw new DataAccessException("There was an error while retrieving the user count!", exception);
		}
		return count;
	}

	/**
	 * This method deletes the user data entry with the provided user id from the
	 * application database.
	 * 
	 * @param id The id of the user data entry
	 * @throws DatabaseException If an error occurs while deleting the user data
	 *                           entry
	 */
	public void delete(int id) throws DatabaseException {
		LOGGER.debug("Deleting user with id " + id);
		try {
			runner.runScript(USER_DATA_DELETE_SCRIPT, id);
		} catch (SQLRunnerLoadException exception) {
			throw new DataAccessException("There was an error while deleting the user data entry!", exception);
		}
	}

	/**
	 * This method inserts the given {@link UserData} object as a new user data
	 * entry in the application database.
	 * 
	 * @param userData The user data to insert
	 * @throws DatabaseException If an error occurs while inserting the user data
	 *                           entry
	 */
	public void insert(UserData userData) throws DatabaseException {
		LOGGER.debug("Inserting user data with information " + userData);
		int id = userData.getId();
		String firstName = userData.getFirstName();
		String lastName = userData.getLastName();
		byte[] icon = userData.getIcon();
		runner.runScript(USER_DATA_INSERT_SCRIPT, id, firstName, lastName, icon);
	}

	/**
	 * This method returns and retrieves a list of {@link UserData} objects from the
	 * application database, that has the given size and starts with the given
	 * offset.
	 * 
	 * @param count  The number of entries
	 * @param offset The number of entries to skip
	 * @return The selected range
	 * @throws DatabaseException If an error occurs while retrieving the user data
	 *                           information
	 */
	public List<UserData> range(int count, int offset) throws DatabaseException {
		LOGGER.debug("Getting " + count + " user data entries with offset " + offset);
		List<ResultSet> results = runner.runScript(USER_DATA_RANGE_SCRIPT, offset, count);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + USER_DATA_RANGE_SCRIPT + " did not yeild results!");
		} else {
			result = results.getFirst();
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
					throw new DataAccessException("There was an error while retrieving the user data information", exception);
				}
				UserData userData = new UserData(id, firstName, lastName);
				userData.setIcon(icon);
				range.add(userData);
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
		}
		return range;
	}

	/**
	 * This method returns and retrieves the only {@link UserData} object with the
	 * given user id from the application database.
	 * 
	 * @param id The user id
	 * @return The user data with the given id
	 * @throws DatabaseException If an error occurs while retrieving the user data
	 *                           information
	 */
	public UserData select(int id) throws DatabaseException {
		LOGGER.debug("Getting user data with user id " + id);
		List<ResultSet> results = runner.runScript(USER_DATA_SELECT_SCRIPT, id);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + USER_DATA_SELECT_SCRIPT + " did not yeild results!");
		} else {
			result = results.getFirst();
		}
		try {
			if (!result.next()) {
				return null;
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
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
			throw new DataAccessException("There was an error while retrieving the user data information", exception);
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
	 * @throws DatabaseException If an error occurs while updating the user data
	 *                           entry
	 */
	public void update(UserData userData) throws DatabaseException {
		LOGGER.debug("Updating user data with data " + userData);
		int id = userData.getId();
		String firstName = userData.getFirstName();
		String lastName = userData.getLastName();
		byte[] icon = userData.getIcon();
		runner.runScript(USER_DATA_UPDATE_SCRIPT, firstName, lastName, icon, id);
	}

}
