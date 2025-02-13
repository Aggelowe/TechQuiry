package com.aggelowe.techquiry.database.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.common.SecurityUtils;
import com.aggelowe.techquiry.database.LocalResult;
import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.database.exception.DataAccessException;
import com.aggelowe.techquiry.database.exception.DatabaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link UserLoginDao} interface provides methods to interact with the
 * database for managing user login information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@Log4j2
@RequiredArgsConstructor
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
	 * This method returns the number of user login entries inside the application
	 * database.
	 * 
	 * @return The number of user logins in the database
	 * @throws DatabaseException If an error occurs while retrieving the user count
	 */
	public int count() throws DatabaseException {
		log.debug("Getting user login entry count");
		List<LocalResult> results = runner.runScript(USER_LOGIN_COUNT_SCRIPT);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + USER_LOGIN_COUNT_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_COUNT_SCRIPT + " did not yeild results!");
		}
		List<Map<String, Object>> list = result.list();
		if (list.size() == 0) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_COUNT_SCRIPT + " did not yeild a user count!");
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("users_count");
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
		log.debug("Deleting user with id " + id);
		runner.runScript(USER_LOGIN_DELETE_SCRIPT, id);
	}

	/**
	 * This method inserts the given {@link UserLogin} object as a new user login
	 * entry in the application database. The user id is not carried over to the
	 * database.
	 * 
	 * @param userLogin The user login to insert
	 * @return The id of the inserted user
	 * @throws DatabaseException If an error occurs while inserting the user login
	 *                           entry
	 */
	public int insert(UserLogin userLogin) throws DatabaseException {
		log.debug("Inserting user login with information " + userLogin);
		String username = userLogin.getUsername();
		byte[] passwordHash = userLogin.getPasswordHash();
		byte[] passwordSalt = userLogin.getPasswordSalt();
		String encodedHash = SecurityUtils.encodeBase64(passwordHash);
		String encodedSalt = SecurityUtils.encodeBase64(passwordSalt);
		List<LocalResult> results = runner.runScript(USER_LOGIN_INSERT_SCRIPT, username, encodedHash, encodedSalt);
		if (results.size() < 2) {
			throw new DataAccessException("The script " + USER_LOGIN_INSERT_SCRIPT + " did not yeild at least two results!");
		}
		LocalResult result = results.get(1);
		if (result == null) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_INSERT_SCRIPT + " did not yeild results!");
		}
		List<Map<String, Object>> list = result.list();
		if (list.size() == 0) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_INSERT_SCRIPT + " did not yeild a user id!");
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("user_id");
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
		log.debug("Getting " + count + " user login entries with offset " + offset);
		List<LocalResult> results = runner.runScript(USER_LOGIN_RANGE_SCRIPT, offset, count);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + USER_LOGIN_RANGE_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_RANGE_SCRIPT + " did not yeild results!");
		}
		List<UserLogin> range = new ArrayList<>(count);
		for (Map<String, Object> row : result) {
			int id = (int) row.get("user_id");
			String username = (String) row.get("username");
			String encodedHash = (String) row.get("password_hash");
			String encodedSalt = (String) row.get("password_salt");
			byte[] passwordHash;
			byte[] passwordSalt;
			try {
				passwordHash = SecurityUtils.decodeBase64(encodedHash);
				passwordSalt = SecurityUtils.decodeBase64(encodedSalt);
			} catch (IllegalArgumentException exception) {
				throw new DataAccessException("There was an error while retrieving the user login information!", exception);
			}
			UserLogin userLogin = new UserLogin(id, username, passwordHash, passwordSalt);
			range.add(userLogin);
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
		log.debug("Getting user login with user id " + id);
		List<LocalResult> results = runner.runScript(USER_LOGIN_SELECT_SCRIPT, id);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + USER_LOGIN_SELECT_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_SELECT_SCRIPT + " did not yeild results!");
		}
		List<Map<String, Object>> list = result.list();
		if (list.size() == 0) {
			return null;
		}
		Map<String, Object> row = list.getFirst();
		String username = (String) row.get("username");
		String encodedHash = (String) row.get("password_hash");
		String encodedSalt = (String) row.get("password_salt");
		byte[] passwordHash;
		byte[] passwordSalt;
		try {
			passwordHash = SecurityUtils.decodeBase64(encodedHash);
			passwordSalt = SecurityUtils.decodeBase64(encodedSalt);
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
		log.debug("Getting user login with username " + username);
		List<LocalResult> results = runner.runScript(USER_LOGIN_SELECT_USERNAME_SCRIPT, username);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + USER_LOGIN_SELECT_USERNAME_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + USER_LOGIN_SELECT_USERNAME_SCRIPT + " did not yeild results!");
		}
		List<Map<String, Object>> list = result.list();
		if (list.size() == 0) {
			return null;
		}
		Map<String, Object> row = list.getFirst();
		int id = (int) row.get("user_id");
		String encodedHash = (String) row.get("password_hash");
		String encodedSalt = (String) row.get("password_salt");
		byte[] passwordHash;
		byte[] passwordSalt;
		try {
			passwordHash = SecurityUtils.decodeBase64(encodedHash);
			passwordSalt = SecurityUtils.decodeBase64(encodedSalt);
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
		log.debug("Updating user login with data " + userLogin);
		int id = userLogin.getUserId();
		String username = userLogin.getUsername();
		byte[] passwordHash = userLogin.getPasswordHash();
		byte[] passwordSalt = userLogin.getPasswordSalt();
		String encodedHash = SecurityUtils.encodeBase64(passwordHash);
		String encodedSalt = SecurityUtils.encodeBase64(passwordSalt);
		runner.runScript(USER_LOGIN_UPDATE_SCRIPT, username, encodedHash, encodedSalt, id);
	}

}
