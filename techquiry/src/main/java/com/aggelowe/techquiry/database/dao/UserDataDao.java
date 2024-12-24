package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
@Component
public final class UserDataDao {

	/**
	 * The path of the SQL script for deleting a user data entry.
	 */
	public static final String USER_DATA_DELETE_SCRIPT = "/database/user_data/delete.sql";

	/**
	 * The path of the SQL script for inserting a user data entry.
	 */
	public static final String USER_DATA_INSERT_SCRIPT = "/database/user_data/insert.sql";

	/**
	 * The path of the SQL script for selecting a user data entry.
	 */
	public static final String USER_DATA_SELECT_SCRIPT = "/database/user_data/select.sql";

	/**
	 * The path of the SQL script for updating a user data entry.
	 */
	public static final String USER_DATA_UPDATE_SCRIPT = "/database/user_data/update.sql";

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
	@Autowired
	public UserDataDao(final SQLRunner runner) {
		this.runner = runner;
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
