package com.aggelowe.techquiry.database.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.common.SecurityUtils;
import com.aggelowe.techquiry.database.LocalResult;
import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.exception.DataAccessException;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.Response;
import com.aggelowe.techquiry.entity.Upvote;
import com.aggelowe.techquiry.entity.UserLogin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link UpvoteDao} interface provides methods to interact with the
 * database for managing upvote information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@Log4j2
@RequiredArgsConstructor
public final class UpvoteDao {

	/**
	 * The path of the SQL script for obtaining the count of upvote entries with an
	 * inquiry id.
	 */
	public static final String UPVOTE_CHECK_SCRIPT = "/database/upvote/check.sql";

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
	 * This method checks whether the given {@link Upvote} object exists inside the
	 * application database.
	 * 
	 * @param upvote The upvote entry to check
	 * @return Whether the entry exists
	 * @throws DatabaseException If an error occurs while checking for the upvote
	 */
	public boolean check(Upvote upvote) throws DatabaseException {
		log.debug("Selecting upvote exists (upvote=%s)".formatted(upvote));
		int responseId = upvote.getResponseId();
		int userId = upvote.getUserId();
		List<LocalResult> results = runner.runScript(UPVOTE_CHECK_SCRIPT, responseId, userId);
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + UPVOTE_CHECK_SCRIPT + " did not yeild a result!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + UPVOTE_CHECK_SCRIPT + " did not yeild results!");
		}
		List<Map<String, Object>> list = result.list();
		if (list.size() == 0) {
			throw new DataAccessException("The first statement in " + UPVOTE_CHECK_SCRIPT + " did not yeild a result!");
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("exist") == 1;
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
		log.debug("Selecting upvote entry count (responseId=%s)".formatted(responseId));
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
		log.debug("Deleting upvote entry (upvote=%s)".formatted(upvote));
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
		log.debug("Inserting upvote entry (upvote=%s)".formatted(upvote));
		int responseId = upvote.getResponseId();
		int userId = upvote.getUserId();
		runner.runScript(UPVOTE_INSERT_SCRIPT, responseId, userId);
	}

	/**
	 * This method returns and retrieves the list of {@link UserLogin} objects from
	 * the application database where the user id matches with the user id in the
	 * upvote objects with the given response id.
	 * 
	 * @param responseId The response id
	 * @return The selected user logins
	 * @throws DatabaseException If an error occurs while retrieving the response
	 *                           information
	 */
	public List<UserLogin> selectFromResponseId(int responseId) throws DatabaseException {
		log.debug("Selecting upvote entries (responseId=%s)".formatted(responseId));
		List<LocalResult> results = runner.runScript(UPVOTE_SELECT_RESPONSE_ID_SCRIPT, responseId);
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_RESPONSE_ID_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_RESPONSE_ID_SCRIPT + " did not yeild results!");
		}
		List<UserLogin> list = new ArrayList<>();
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
			list.add(userLogin);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the list of {@link Response} objects from
	 * the application database where the response id matches with the response id
	 * in the upvote objects with the given user id.
	 * 
	 * @param userId The user id
	 * @return The selected responses
	 * @throws DatabaseException If an error occurs while retrieving the response
	 *                           information
	 */
	public List<Response> selectFromUserId(int userId) throws DatabaseException {
		log.debug("Selecting upvote entries (userId=%s)".formatted(userId));
		List<LocalResult> results = runner.runScript(UPVOTE_SELECT_USER_ID_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + UPVOTE_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		}
		List<Response> list = new ArrayList<>();
		for (Map<String, Object> row : result) {
			int responseId = (int) row.get("response_id");
			int inquiryId = (int) row.get("inquiry_id");
			int authorId = (int) row.get("user_id");
			boolean anonymous = (int) row.get("anonymous") == 1;
			String content = (String) row.get("content");
			Response response = new Response(responseId, inquiryId, authorId, anonymous, content);
			list.add(response);
		}
		return list;
	}

}
