package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.Upvote;
import com.aggelowe.techquiry.database.exceptions.DataAccessException;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;

/**
 * The {@link UpvoteDao} interface provides methods to interact with the
 * database for managing upvote information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
public final class UpvoteDao {

	/**
	 * The path of the SQL script for obtaining the count of upvote entries with a
	 * response id.
	 */
	public static final String UPVOTE_COUNT_RESPONSE_ID_SCRIPT = "/database/upvote/count_response_id.sql";

	/**
	 * The path of the SQL script for deleting an upvote entry.
	 */
	public static final String UPVOTE_DELETE_SCRIPT = "/database/upvote/delete.sql";

	/**
	 * The path of the SQL script for inserting an upvote entry.
	 */
	public static final String UPVOTE_INSERT_SCRIPT = "/database/upvote/insert.sql";

	/**
	 * The path of the SQL script for selecting an upvote entry with a response id.
	 */
	public static final String UPVOTE_SELECT_RESPONSE_ID_SCRIPT = "/database/upvote/select_response_id.sql";

	/**
	 * The path of the SQL script for selecting an upvote entry with a user id.
	 */
	public static final String UPVOTE_SELECT_USER_ID_SCRIPT = "/database/upvote/select_user_id.sql";

	/**
	 * The runner responsible for executing the SQL scripts.
	 */
	private final SQLRunner runner;

	/**
	 * This constructor constructs a new {@link UpvoteDao} instance that is
	 * responsible for handling the data access for {@link Upvote} objects.
	 * 
	 * @param runner The SQL script runner
	 */
	@Autowired
	public UpvoteDao(final SQLRunner runner) {
		this.runner = runner;
	}

	/**
	 * This method returns the number of upvote entries inside the application
	 * database with the given response id.
	 * 
	 * @param responseId the response id
	 * @return The number of upvote entries in the database
	 * @throws DatabaseException If an error occurs while retrieving the upvote
	 *                           count
	 */
	public int countFromResponseId(int responseId) throws DatabaseException {
		LOGGER.debug("Getting upvote entry count");
		List<ResultSet> results = runner.runScript(UPVOTE_COUNT_RESPONSE_ID_SCRIPT, responseId);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + UPVOTE_COUNT_RESPONSE_ID_SCRIPT + " did not yeild a result!");
		} else {
			result = results.getFirst();
		}
		int count;
		try {
			count = result.getInt("upvote_count");
		} catch (SQLException exception) {
			throw new DataAccessException("There was an error while retrieving the upvote count!", exception);
		}
		return count;
	}

	
	/**
	 * This method deletes the upvote with the provided information from the
	 * application database.
	 * 
	 * @param upvote The upvote to delete
	 * @throws DatabaseException If an error occurs while deleting the upvote entry
	 */
	public void delete(Upvote upvote) throws DatabaseException {
		LOGGER.debug("Deleting upvote with information " + upvote);
		int responseId = upvote.getResponseId();
		int userId = upvote.getUserId();
		runner.runScript(UPVOTE_DELETE_SCRIPT, responseId, userId);
	}

	/**
	 * This method inserts the given {@link Upvote} object as a new upvote entry in
	 * the application database.
	 * 
	 * @param upvote The upvote to insert
	 * @throws DatabaseException If an error occurs while inserting the upvote entry
	 */
	public void insert(Upvote upvote) throws DatabaseException {
		LOGGER.debug("Inserting upvote with information " + upvote);
		int responseId = upvote.getResponseId();
		int userId = upvote.getUserId();
		runner.runScript(UPVOTE_INSERT_SCRIPT, responseId, userId);
	}

	/**
	 * This method returns and retrieves the list of {@link Upvote} objects with the
	 * given response id from the application database.
	 * 
	 * @param responseId The response id
	 * @return The upvotes with the given id
	 * @throws DatabaseException If an error occurs while retrieving the response
	 *                           information
	 */
	public List<Upvote> selectFromResponseId(int responseId) throws DatabaseException {
		LOGGER.debug("Getting upvotes with response id " + responseId);
		List<ResultSet> results = runner.runScript(UPVOTE_SELECT_RESPONSE_ID_SCRIPT, responseId);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_RESPONSE_ID_SCRIPT + " did not yeild results!");
		} else {
			result = results.getFirst();
		}
		List<Upvote> list = new ArrayList<>();
		try {
			while (result.next()) {
				int userId;
				try {
					userId = result.getInt("user_id");
				} catch (SQLException exception) {
					throw new DataAccessException("There was an error while retrieving the upvote information", exception);
				}
				Upvote upvote = new Upvote(responseId, userId);
				list.add(upvote);
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the list of {@link Upvote} objects with the
	 * given user id from the application database.
	 * 
	 * @param userId The user id
	 * @return The upvotes with the given id
	 * @throws DatabaseException If an error occurs while retrieving the response
	 *                           information
	 */
	public List<Upvote> selectFromUserId(int userId) throws DatabaseException {
		LOGGER.debug("Getting upvotes with user id " + userId);
		List<ResultSet> results = runner.runScript(UPVOTE_SELECT_USER_ID_SCRIPT, userId);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		} else {
			result = results.getFirst();
		}
		List<Upvote> list = new ArrayList<>();
		try {
			while (result.next()) {
				int responseId;
				try {
					responseId = result.getInt("response_id");
				} catch (SQLException exception) {
					throw new DataAccessException("There was an error while retrieving the upvote information", exception);
				}
				Upvote response = new Upvote(responseId, userId);
				list.add(response);
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
		}
		return list;
	}

}
