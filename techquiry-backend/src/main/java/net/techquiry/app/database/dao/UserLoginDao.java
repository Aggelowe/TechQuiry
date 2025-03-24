package net.techquiry.app.database.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.common.SecurityUtils;
import net.techquiry.app.database.LocalResult;
import net.techquiry.app.database.SQLRunner;
import net.techquiry.app.database.exception.DataAccessException;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.UserLogin;

/**
 * The {@link UserLoginDao} class provides methods to interact with the database
 * for managing user login entries in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class UserLoginDao {

	/**
	 * The path of the SQL script for obtaining the count of user login entries.
	 */
	private static final String USER_LOGIN_COUNT_SCRIPT = "/database/user_login/count.sql";

	/**
	 * The path of the SQL script for deleting a user login entry.
	 */
	private static final String USER_LOGIN_DELETE_SCRIPT = "/database/user_login/delete.sql";

	/**
	 * The path of the SQL script for inserting a user login entry.
	 */
	private static final String USER_LOGIN_INSERT_SCRIPT = "/database/user_login/insert.sql";

	/**
	 * The path of the SQL script for selecting a user login entry range.
	 */
	private static final String USER_LOGIN_RANGE_SCRIPT = "/database/user_login/range.sql";

	/**
	 * The path of the SQL script for selecting a user login entry with an id.
	 */
	private static final String USER_LOGIN_SELECT_SCRIPT = "/database/user_login/select.sql";

	/**
	 * The path of the SQL script for selecting a user login entry with a username.
	 */
	private static final String USER_LOGIN_SELECT_USERNAME_SCRIPT = "/database/user_login/select_username.sql";

	/**
	 * The path of the SQL script for updating a user login entry.
	 */
	private static final String USER_LOGIN_UPDATE_SCRIPT = "/database/user_login/update.sql";

	/**
	 * The runner responsible for executing the SQL scripts.
	 */
	private final SQLRunner runner;

	/**
	 * This method returns the number of user login entries inside the application
	 * database.
	 * 
	 * @return The number of user logins in the database
	 * @throws DatabaseException If a database error occurs while retrieving the
	 *                           user count
	 */
	public int count() throws DatabaseException {
		log.debug("Selecting user login entry count");
		List<LocalResult> results = runner.runScript(USER_LOGIN_COUNT_SCRIPT);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(USER_LOGIN_COUNT_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(USER_LOGIN_COUNT_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_MESSAGE.formatted(USER_LOGIN_COUNT_SCRIPT));
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("users_count");
	}

	/**
	 * This method deletes the user login entry with the provided user id from the
	 * application database.
	 * 
	 * @param userId The user id of the user login entry
	 * @throws DatabaseException If a database error occurs while deleting the user
	 *                           login entry
	 */
	public void delete(int userId) throws DatabaseException {
		log.debug("Deleting user login entry (userId=%s)".formatted(userId));
		runner.runScript(USER_LOGIN_DELETE_SCRIPT, userId);
	}

	/**
	 * This method inserts the given {@link UserLogin} object as a new user login
	 * entry in the application database. The user id is not carried over to the
	 * database.
	 * 
	 * @param userLogin The user login entry to insert
	 * @return The user id of the inserted user
	 * @throws DatabaseException If a database error occurs while inserting the user
	 *                           login entry
	 */
	public int insert(UserLogin userLogin) throws DatabaseException {
		log.debug("Inserting user login entry (userLogin=%s)".formatted(userLogin));
		String username = userLogin.getUsername();
		byte[] passwordHash = userLogin.getPasswordHash();
		byte[] passwordSalt = userLogin.getPasswordSalt();
		String encodedHash = SecurityUtils.encodeBase64(passwordHash);
		String encodedSalt = SecurityUtils.encodeBase64(passwordSalt);
		List<LocalResult> results = runner.runScript(USER_LOGIN_INSERT_SCRIPT, username, encodedHash, encodedSalt);
		if (results.size() < 2) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(USER_LOGIN_INSERT_SCRIPT));
		}
		LocalResult result = results.get(1);
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(USER_LOGIN_INSERT_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_MESSAGE.formatted(USER_LOGIN_INSERT_SCRIPT));
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("user_id");
	}

	/**
	 * This method returns and retrieves a list of user login entries from the
	 * application database, that has the given size and starts with the given
	 * offset.
	 * 
	 * @param count  The number of user login entries
	 * @param offset The number of user login entries to skip
	 * @return The selected user login range range
	 * @throws DatabaseException If a database error occurs while retrieving the
	 *                           user login information
	 */
	public List<UserLogin> range(int count, int offset) throws DatabaseException {
		log.debug("Selecting user login entries (count=%s, offset=%s)".formatted(count, offset));
		List<LocalResult> results = runner.runScript(USER_LOGIN_RANGE_SCRIPT, offset, count);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(USER_LOGIN_RANGE_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(USER_LOGIN_RANGE_SCRIPT));
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
				throw new DataAccessException("Could not decode the user login information!", exception);
			}
			UserLogin userLogin = new UserLogin(id, username, passwordHash, passwordSalt);
			range.add(userLogin);
		}
		return range;
	}

	/**
	 * This method returns and retrieves the only user login entry with the given
	 * user id from the application database.
	 * 
	 * @param userId The user id
	 * @return The user login entry with the given id
	 * @throws DatabaseException If a database error occurs while retrieving the
	 *                           user login information
	 */
	public UserLogin select(int userId) throws DatabaseException {
		log.debug("Selecting user login entry (userId=%s)".formatted(userId));
		List<LocalResult> results = runner.runScript(USER_LOGIN_SELECT_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(USER_LOGIN_SELECT_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(USER_LOGIN_SELECT_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
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
		return new UserLogin(userId, username, passwordHash, passwordSalt);
	}

	/**
	 * This method returns the only user login entry with the given username from
	 * the application database.
	 * 
	 * @param username The username
	 * @return The user login entry with the given username
	 * @throws DatabaseException If a database error occurs while retrieving the
	 *                           user login information
	 */
	public UserLogin selectFromUsername(String username) throws DatabaseException {
		log.debug("Selecting user login entry (username=%s)".formatted(username));
		List<LocalResult> results = runner.runScript(USER_LOGIN_SELECT_USERNAME_SCRIPT, username);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(USER_LOGIN_SELECT_USERNAME_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(USER_LOGIN_SELECT_USERNAME_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
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
		return new UserLogin(id, username, passwordHash, passwordSalt);
	}

	/**
	 * This method replaces the information of a user login entry with the data
	 * contained in the {@link UserLogin} object, using the user id from the object
	 * to select the correct entry.
	 * 
	 * @param userLogin The user login entry to update
	 * @throws DatabaseException If a database error occurs while updating the user
	 *                           login entry
	 */
	public void update(UserLogin userLogin) throws DatabaseException {
		log.debug("Updating user login entry (userLogin=%s)".formatted(userLogin));
		int id = userLogin.getUserId();
		String username = userLogin.getUsername();
		byte[] passwordHash = userLogin.getPasswordHash();
		byte[] passwordSalt = userLogin.getPasswordSalt();
		String encodedHash = SecurityUtils.encodeBase64(passwordHash);
		String encodedSalt = SecurityUtils.encodeBase64(passwordSalt);
		runner.runScript(USER_LOGIN_UPDATE_SCRIPT, username, encodedHash, encodedSalt, id);
	}

}
