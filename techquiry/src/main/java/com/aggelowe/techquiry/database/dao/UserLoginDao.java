package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aggelowe.techquiry.common.Utilities;
import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DataAccessException;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;

/**
 * The {@link UserLoginDao} interface provides methods to interact with the
 * database for managing user login information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class UserLoginDao {

	/**
	 * The path of the SQL script for obtaining the count of user login entries.
	 */
	public static final String USER_LOGIN_COUNT_SCRIPT = "/database/user_login/count.sql";

	/**
	 * The path of the SQL script for deleting a user login entry.
	 */
	public static final String USER_LOGIN_DELETE_SCRIPT = "/database/user_login/delete.sql";

	/**
	 * The path of the SQL script for inserting a user login entry.
	 */
	public static final String USER_LOGIN_INSERT_SCRIPT = "/database/user_login/insert.sql";

	/**
	 * The path of the SQL script for selecting a user login entry range.
	 */
	public static final String USER_LOGIN_RANGE_SCRIPT = "/database/user_login/range.sql";

	/**
	 * The path of the SQL script for selecting a user login entry with an id.
	 */
	public static final String USER_LOGIN_SELECT_SCRIPT = "/database/user_login/select.sql";

	/**
	 * The path of the SQL script for selecting a user login entry with a username.
	 */
	public static final String USER_LOGIN_SELECT_USERNAME_SCRIPT = "/database/user_login/select_username.sql";

	/**
	 * The path of the SQL script for updating a user login entry.
	 */
	public static final String USER_LOGIN_UPDATE_SCRIPT = "/database/user_login/update.sql";

	/**
	 * The runner responsible for executing the SQL scripts.
	 */
	private final SQLRunner runner;

	/**
	 * This constructor constructs a new {@link UserLoginDao} instance that is
	 * responsible for handling the data access for {@link UserLogin} objects.
	 * 
	 * @param runner The SQL script runner
	 */
	public UserLoginDao(SQLRunner runner) {
		this.runner = runner;
	}

	/**
	 * This method returns the number of user login entries inside the application
	 * database.
	 * 
	 * @return The number of user logins in the database
	 * @throws DatabaseException If an error occurs while retrieving the user count
	 */
	public int count() throws DatabaseException {
		LOGGER.debug("Getting user login entry count");
		List<ResultSet> results = runner.runScript(USER_LOGIN_COUNT_SCRIPT);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_COUNT_SCRIPT + " did not yeild a result!");
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
	 * This method deletes the user login entry with the provided user id from the
	 * application database.
	 * 
	 * @param id The id of the user login entry
	 * @throws DatabaseException If an error occurs while deleting the user login
	 *                           entry
	 */
	public void delete(int id) throws DatabaseException {
		LOGGER.debug("Deleting user with id " + id);
		runner.runScript(USER_LOGIN_DELETE_SCRIPT, id);
	}

	/**
	 * This method inserts the given {@link UserLogin} object as a new user login
	 * entry in the application database.
	 * 
	 * @param userLogin The user login to insert
	 * @throws DatabaseException If an error occurs while inserting the user login
	 *                           entry
	 */
	public void insert(UserLogin userLogin) throws DatabaseException {
		LOGGER.debug("Inserting user login with information " + userLogin);
		int id = userLogin.getId();
		String username = userLogin.getUsername();
		byte[] passwordHash = userLogin.getPasswordHash();
		byte[] passwordSalt = userLogin.getPasswordSalt();
		String encodedHash = Utilities.encodeBase64(passwordHash);
		String encodedSalt = Utilities.encodeBase64(passwordSalt);
		runner.runScript(USER_LOGIN_INSERT_SCRIPT, id, username, encodedHash, encodedSalt);
	}

	/**
	 * This method returns and retrieves a list of {@link UserLogin} objects from
	 * the application database, that has the given size and starts with the given
	 * offset.
	 * 
	 * @param count  The number of entries
	 * @param offset The number of entries to skip
	 * @return The selected range
	 * @throws DatabaseException If an error occurs while retrieving the user login
	 *                           information
	 */
	public List<UserLogin> range(int count, int offset) throws DatabaseException {
		LOGGER.debug("Getting " + count + " user login entries with offset " + offset);
		List<ResultSet> results = runner.runScript(USER_LOGIN_RANGE_SCRIPT, offset, count);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_RANGE_SCRIPT + " did not yeild results!");
		} else {
			result = results.getFirst();
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
					throw new DataAccessException("There was an error while retrieving the user login information", exception);
				}
				byte[] passwordHash;
				byte[] passwordSalt;
				try {
					passwordHash = Utilities.decodeBase64(encodedHash);
					passwordSalt = Utilities.decodeBase64(encodedSalt);
				} catch (IllegalArgumentException exception) {
					throw new DataAccessException("There was an error while retrieving the user login information!", exception);
				}
				UserLogin userLogin = new UserLogin(id, username, passwordHash, passwordSalt);
				range.add(userLogin);
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
		}
		return range;
	}

	/**
	 * This method returns and retrieves the only {@link UserLogin} object with the
	 * given user id from the application database.
	 * 
	 * @param id The user id
	 * @return The user login with the given id
	 * @throws DatabaseException If an error occurs while retrieving the user login
	 *                           information
	 */
	public UserLogin select(int id) throws DatabaseException {
		LOGGER.debug("Getting user login with user id " + id);
		List<ResultSet> results = runner.runScript(USER_LOGIN_SELECT_SCRIPT, id);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_SELECT_SCRIPT + " did not yeild results!");
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
		String username;
		String encodedHash;
		String encodedSalt;
		try {
			username = result.getString("username");
			encodedHash = result.getString("password_hash");
			encodedSalt = result.getString("password_salt");
		} catch (SQLException exception) {
			throw new DataAccessException("There was an error while retrieving the user login information", exception);
		}
		byte[] passwordHash;
		byte[] passwordSalt;
		try {
			passwordHash = Utilities.decodeBase64(encodedHash);
			passwordSalt = Utilities.decodeBase64(encodedSalt);
		} catch (IllegalArgumentException exception) {
			throw new DataAccessException("There was an error while retrieving the user login information!", exception);
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
	 * @throws DatabaseException If an error occurs while retrieving the user login
	 *                           information
	 */
	public UserLogin selectFromUsername(String username) throws DatabaseException {
		LOGGER.debug("Getting user login with username " + username);
		List<ResultSet> results = runner.runScript(USER_LOGIN_SELECT_USERNAME_SCRIPT, username);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_SELECT_USERNAME_SCRIPT + " did not yeild results!");
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
		int id;
		String encodedHash;
		String encodedSalt;
		try {
			id = result.getInt("user_id");
			encodedHash = result.getString("password_hash");
			encodedSalt = result.getString("password_salt");
		} catch (SQLException exception) {
			throw new DataAccessException("There was an error while retrieving the user login information", exception);
		}
		byte[] passwordHash;
		byte[] passwordSalt;
		try {
			passwordHash = Utilities.decodeBase64(encodedHash);
			passwordSalt = Utilities.decodeBase64(encodedSalt);
		} catch (IllegalArgumentException exception) {
			throw new DataAccessException("There was an error while retrieving the user login information!", exception);
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
	 * @throws DatabaseException If an error occurs while updating the user login
	 *                           entry
	 */
	public void update(UserLogin userLogin) throws DatabaseException {
		LOGGER.debug("Updating user login with data " + userLogin);
		int id = userLogin.getId();
		String username = userLogin.getUsername();
		byte[] passwordHash = userLogin.getPasswordHash();
		byte[] passwordSalt = userLogin.getPasswordSalt();
		String encodedHash = Utilities.encodeBase64(passwordHash);
		String encodedSalt = Utilities.encodeBase64(passwordSalt);
		runner.runScript(USER_LOGIN_UPDATE_SCRIPT, username, encodedHash, encodedSalt, id);
	}

}
