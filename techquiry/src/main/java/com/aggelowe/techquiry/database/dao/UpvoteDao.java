package com.aggelowe.techquiry.database.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aggelowe.techquiry.database.LocalResult;
import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.Upvote;
import com.aggelowe.techquiry.database.exceptions.DataAccessException;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;

import lombok.extern.log4j.Log4j2;

/**
 * The {@link UpvoteDao} interface provides methods to interact with the
 * database for managing upvote information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Log4j2
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
	public UpvoteDao(SQLRunner runner) {
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
		log.debug("Getting upvote entry count");
		List<LocalResult> results = runner.runScript(UPVOTE_COUNT_RESPONSE_ID_SCRIPT, responseId);
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + UPVOTE_COUNT_RESPONSE_ID_SCRIPT + " did not yeild a result!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + UPVOTE_COUNT_RESPONSE_ID_SCRIPT + " did not yeild results!");
		}
		List<Map<String, Object>> list = result.list();
		if (list.size() == 0) {
			throw new DataAccessException("The first statement in " + UPVOTE_COUNT_RESPONSE_ID_SCRIPT + " did not yeild an upvote count!");
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("upvote_count");
	}

	/**
	 * This method deletes the upvote with the provided information from the
	 * application database.
	 * 
	 * @param upvote The upvote to delete
	 * @throws DatabaseException If an error occurs while deleting the upvote entry
	 */
	public void delete(Upvote upvote) throws DatabaseException {
		log.debug("Deleting upvote with information " + upvote);
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
		log.debug("Inserting upvote with information " + upvote);
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
		log.debug("Getting upvotes with response id " + responseId);
		List<LocalResult> results = runner.runScript(UPVOTE_SELECT_RESPONSE_ID_SCRIPT, responseId);
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_RESPONSE_ID_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_RESPONSE_ID_SCRIPT + " did not yeild results!");
		}
		List<Upvote> list = new ArrayList<>();
		for (Map<String, Object> row : result) {
			int userId = (int) row.get("user_id");
			Upvote upvote = new Upvote(responseId, userId);
			list.add(upvote);
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
		log.debug("Getting upvotes with user id " + userId);
		List<LocalResult> results = runner.runScript(UPVOTE_SELECT_USER_ID_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		}
		List<Upvote> list = new ArrayList<>();
		for (Map<String, Object> row : result) {
			int responseId = (int) row.get("response_id");
			Upvote upvote = new Upvote(responseId, userId);
			list.add(upvote);
		}
		return list;
	}

}
