package com.aggelowe.techquiry.data.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import com.aggelowe.techquiry.Utilities;
import com.aggelowe.techquiry.exception.InvalidConstructionException;
import com.aggelowe.techquiry.exception.SQLExecutionException;

import static com.aggelowe.techquiry.Reference.LOGGER;

/**
 * The {@link UserAdapter} class is responsible for handling the connection of
 * {@link User} objects to the application database.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class UserAdapter {

	/**
	 * This constructor will throw an {@link InvalidConstructionException} whenever
	 * invoked. {@link UserAdapter} objects should <b>not</b> be constructible.
	 * 
	 * @throws InvalidConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private UserAdapter() {
		throw new InvalidConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method is responsible for creating the database table containing the
	 * user entries of the application in the application's database.
	 */
	public static void createUserTable() {
		LOGGER.debug("Creating user database table if missing");
		UserAccessor.createUserTable();
	}

	/**
	 * This method returns the number of user entries inside the user table of the
	 * application database.
	 * 
	 * @return The number of users in the database
	 */
	public static int getUserCount() {
		LOGGER.debug("Getting user entry count");
		ResultSet result = UserAccessor.selectUserCount();
		int count;
		try {
			count = result.getInt("users_count");
		} catch (SQLException exception) {
			throw new SQLExecutionException("There was an error while retrieving the user count!", exception);
		}
		return count;
	}

	/**
	 * This method returns and retrieves {@link User} object that has the given user
	 * id from the application database.
	 * 
	 * @param id The user id
	 * @return The user with the given id
	 */
	public static Optional<User> getUserById(int id) {
		LOGGER.debug("Getting user with user id " + id);
		ResultSet result = UserAccessor.selectUserById(id);
		try {
			if (!result.next()) {
				return Optional.empty();
			}
		} catch (SQLException exception) {
			throw new SQLExecutionException("A database error occured!", exception);
		}
		String username;
		String displayName;
		String encodedHash;
		String encodedSalt;
		try {
			username = result.getString("username");
			displayName = result.getString("display_name");
			encodedHash = result.getString("password_hash");
			encodedSalt = result.getString("password_salt");
		} catch (SQLException exception) {
			LOGGER.error("There was an error while retrieving the user information", exception);
			return Optional.empty();
		}
		byte[] passwordHash;
		byte[] passwordSalt;
		try {
			passwordHash = Utilities.decodeBase64(encodedHash);
			passwordSalt = Utilities.decodeBase64(encodedSalt);
		} catch (IllegalArgumentException exception) {
			throw new SQLExecutionException("There was an error while retrieving the user information!", exception);
		}
		User user = new User(id, username, passwordHash, passwordSalt);
		user.setDisplayName(displayName);
		return Optional.of(user);
	}

	/**
	 * This method returns and retrieves {@link User} object that has the given
	 * username from the application database.
	 * 
	 * @param id The user id
	 * @return The user with the given id
	 */
	public static Optional<User> getUserByUsername(String username) {
		LOGGER.debug("Getting user with username " + username);
		ResultSet result = UserAccessor.selectUserByUsername(username);
		try {
			if (!result.next()) {
				return Optional.empty();
			}
		} catch (SQLException exception) {
			throw new SQLExecutionException("A database error occured!", exception);
		}
		int userId;
		String displayName;
		String encodedHash;
		String encodedSalt;
		try {
			userId = result.getInt("user_id");
			displayName = result.getString("display_name");
			encodedHash = result.getString("password_hash");
			encodedSalt = result.getString("password_salt");
		} catch (SQLException exception) {
			throw new SQLExecutionException("There was an error while retrieving the user information", exception);
		}
		byte[] passwordHash;
		byte[] passwordSalt;
		try {
			passwordHash = Utilities.decodeBase64(encodedHash);
			passwordSalt = Utilities.decodeBase64(encodedSalt);
		} catch (IllegalArgumentException exception) {
			LOGGER.error("An error occured while decoding the password base64 strings!", exception);
			return Optional.empty();
		}
		User user = new User(userId, username, passwordHash, passwordSalt);
		user.setDisplayName(displayName);
		return Optional.of(user);
	}

	/**
	 * This method adds the given {@link User} object as a new user entry to the
	 * application database.
	 * 
	 * @param user The user to insert
	 * @return Whether the operation succeeded
	 */
	public static boolean addUser(User user) {
		LOGGER.debug("Inserting user with data " + user.toString());
		int id = user.getId();
		String username = user.getUsername();
		String displayName = user.getDisplayName();
		byte[] rawHash = user.getPasswordHash();
		byte[] rawSalt = user.getPasswordSalt();
		String passwordHash = Utilities.encodeBase64(rawHash);
		String passwordSalt = Utilities.encodeBase64(rawSalt);
		try {
			UserAccessor.insertUser(id, username, displayName, passwordHash, passwordSalt);
		} catch (SQLExecutionException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				SQLiteException origin = (SQLiteException) cause;
				if (origin.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
					return false;
				}
			}
			throw exception;
		}
		return true;
	}

	/**
	 * This method updates the data of a user entry with the data contained in the
	 * {@link User} object. The user id contained in the object is used for
	 * selecting the correct user entry.
	 * 
	 * @param user The user data to update
	 * @return Whether the operation succeeded
	 */
	public static boolean modifyUser(User user) {
		LOGGER.debug("Updating user with data " + user.toString());
		int id = user.getId();
		String username = user.getUsername();
		String displayName = user.getDisplayName();
		byte[] rawHash = user.getPasswordHash();
		byte[] rawSalt = user.getPasswordSalt();
		String passwordHash = Utilities.encodeBase64(rawHash);
		String passwordSalt = Utilities.encodeBase64(rawSalt);
		try {
			UserAccessor.updateUserById(id, username, displayName, passwordHash, passwordSalt);
		} catch (SQLExecutionException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				SQLiteException origin = (SQLiteException) cause;
				if (origin.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
					return false;
				}
			}
			throw exception;
		}
		return true;
	}

	/**
	 * This method deletes the user entry whose id matches the given id.
	 * 
	 * @param id The id of the user entry to delete
	 */
	public static void deleteUser(int id) {
		LOGGER.debug("Deleting user with id " + id);
		UserAccessor.deleteUserById(id);
	}

}
