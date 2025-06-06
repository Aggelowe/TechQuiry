package net.techquiry.app.database.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.database.LocalResult;
import net.techquiry.app.database.SQLRunner;
import net.techquiry.app.database.exception.DataAccessException;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.database.exception.SQLRunnerLoadException;
import net.techquiry.app.entity.UserData;

/**
 * The {@link UserDataDao} class provides methods to interact with the database
 * for managing user data entries in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class UserDataDao {

	/**
	 * The path of the SQL script for deleting a user data entry.
	 */
	private static final String USER_DATA_DELETE_SCRIPT = "/database/user_data/delete.sql";

	/**
	 * The path of the SQL script for inserting a user data entry.
	 */
	private static final String USER_DATA_INSERT_SCRIPT = "/database/user_data/insert.sql";

	/**
	 * The path of the SQL script for selecting a user data entry.
	 */
	private static final String USER_DATA_SELECT_SCRIPT = "/database/user_data/select.sql";

	/**
	 * The path of the SQL script for updating a user data entry.
	 */
	private static final String USER_DATA_UPDATE_SCRIPT = "/database/user_data/update.sql";

	/**
	 * The runner responsible for executing the SQL scripts.
	 */
	private final SQLRunner runner;

	/**
	 * This method deletes the user data entry with the provided user id from the
	 * application database.
	 * 
	 * @param userId The user id of the user data entry
	 * @throws DatabaseException If a database error occurs while deleting the user
	 *                           data entry
	 */
	public void delete(int userId) throws DatabaseException {
		log.debug("Deleting user data entry (userId=%s)".formatted(userId));
		try {
			runner.runScript(USER_DATA_DELETE_SCRIPT, userId);
		} catch (SQLRunnerLoadException exception) {
			throw new DataAccessException("There was an error while deleting the user data entry!", exception);
		}
	}

	/**
	 * This method inserts the given {@link UserData} object as a new user data
	 * entry in the application database.
	 * 
	 * @param userData The user data entry to insert
	 * @throws DatabaseException If a database occurs while inserting the user data
	 *                           entry
	 */
	public void insert(UserData userData) throws DatabaseException {
		log.debug("Inserting user data entry (userData=%s)".formatted(userData));
		int id = userData.getUserId();
		String firstName = userData.getFirstName();
		String lastName = userData.getLastName();
		byte[] icon = userData.getIcon();
		runner.runScript(USER_DATA_INSERT_SCRIPT, id, firstName, lastName, icon);
	}

	/**
	 * This method returns the only user data entry with the given user id from the
	 * application database.
	 * 
	 * @param userId The user id
	 * @return The user data entry with the given id
	 * @throws DatabaseException If a database error occurs while retrieving the
	 *                           user data information
	 */
	public UserData select(int userId) throws DatabaseException {
		log.debug("Selecting user data entry (userId=%s)".formatted(userId));
		List<LocalResult> results = runner.runScript(USER_DATA_SELECT_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(USER_DATA_SELECT_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(USER_DATA_SELECT_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			return null;
		}
		Map<String, Object> row = list.getFirst();
		String firstName = (String) row.get("first_name");
		String lastName = (String) row.get("last_name");
		byte[] icon = (byte[]) row.get("icon");
		return new UserData(userId, firstName, lastName, icon);
	}

	/**
	 * This method replaces the information of a user data entry with the data
	 * contained in the {@link UserData} object, using the user id from the object
	 * to select the correct entry.
	 * 
	 * @param userData The user data entry to update
	 * @throws DatabaseException If a database error occurs while updating the user
	 *                           data entry
	 */
	public void update(UserData userData) throws DatabaseException {
		log.debug("Updating user data entry (userData=%s)".formatted(userData));
		int id = userData.getUserId();
		String firstName = userData.getFirstName();
		String lastName = userData.getLastName();
		byte[] icon = userData.getIcon();
		runner.runScript(USER_DATA_UPDATE_SCRIPT, firstName, lastName, icon, id);
	}

}
