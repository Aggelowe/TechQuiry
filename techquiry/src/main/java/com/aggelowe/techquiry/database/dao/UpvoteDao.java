package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.UPVOTE_DELETE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.UPVOTE_INSERT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.UPVOTE_SELECT_RESPONSE_ID_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.UPVOTE_SELECT_USER_ID_SCRIPT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.entities.Upvote;
import com.aggelowe.techquiry.database.exceptions.DaoException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerException;

/**
 * The {@link UpvoteDao} interface provides methods to interact with the
 * database for managing upvote information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class UpvoteDao {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link UpvoteDao} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private UpvoteDao() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method deletes the upvote with the provided information from the
	 * application database.
	 * 
	 * @param upvote The upvote to delete
	 */
	public static void delete(Upvote upvote) {
		LOGGER.debug("Deleting upvote with information " + upvote);
		int responseId = upvote.getResponseId();
		int userId = upvote.getUserId();
		try {
			DatabaseManager.getRunner().runScript(UPVOTE_DELETE_SCRIPT, responseId, userId);
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while deleting the upvote entry!", exception);
		}
	}

	/**
	 * This method inserts the given {@link Upvote} object as a new upvote entry in
	 * the application database.
	 * 
	 * @param upvote The upvote to insert
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode insert(Upvote upvote) {
		LOGGER.debug("Inserting upvote with information " + upvote);
		int responseId = upvote.getResponseId();
		int userId = upvote.getUserId();
		try {
			DatabaseManager.getRunner().runScript(UPVOTE_INSERT_SCRIPT, responseId, userId);
		} catch (SQLRunnerException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while inserting the upvote entry!", exception);
		}
		return null;
	}

	/**
	 * This method returns and retrieves the list of {@link Upvote} objects with the
	 * given response id from the application database.
	 * 
	 * @param responseId The response id
	 * @return The upvotes with the given id
	 */
	public static List<Upvote> selectFromResponseId(int responseId) {
		LOGGER.debug("Getting upvotes with response id " + responseId);
		ResultSet result;
		try {
			List<ResultSet> results = DatabaseManager.getRunner().runScript(UPVOTE_SELECT_RESPONSE_ID_SCRIPT, responseId);
			if (results.isEmpty()) {
				result = null;
			} else {
				result = results.getFirst();
			}
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while retrieving the response information!", exception);
		}
		if (result == null) {
			throw new DaoException("The first statement in " + UPVOTE_SELECT_RESPONSE_ID_SCRIPT + " did not yeild results!");
		}
		List<Upvote> list = new ArrayList<>();
		try {
			while (result.next()) {
				int userId;
				try {
					userId = result.getInt("user_id");
				} catch (SQLException exception) {
					throw new DaoException("There was an error while retrieving the upvote information", exception);
				}
				Upvote upvote = new Upvote(responseId, userId);
				list.add(upvote);
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the list of {@link Upvote} objects with the
	 * given user id from the application database.
	 * 
	 * @param userId The user id
	 * @return The upvotes with the given id
	 */
	public static List<Upvote> selectFromUserId(int userId) {
		LOGGER.debug("Getting upvotes with user id " + userId);
		ResultSet result;
		try {
			List<ResultSet> results = DatabaseManager.getRunner().runScript(UPVOTE_SELECT_USER_ID_SCRIPT, userId);
			if (results.isEmpty()) {
				result = null;
			} else {
				result = results.getFirst();
			}
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while retrieving the response information!", exception);
		}
		if (result == null) {
			throw new DaoException("The first statement in " + UPVOTE_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		}
		List<Upvote> list = new ArrayList<>();
		try {
			while (result.next()) {
				int responseId;
				try {
					responseId = result.getInt("response_id");
				} catch (SQLException exception) {
					throw new DaoException("There was an error while retrieving the upvote information", exception);
				}
				Upvote response = new Upvote(responseId, userId);
				list.add(response);
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		return list;
	}

}
