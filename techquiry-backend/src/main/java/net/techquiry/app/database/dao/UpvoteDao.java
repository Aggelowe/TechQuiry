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
import net.techquiry.app.entity.Response;
import net.techquiry.app.entity.Upvote;
import net.techquiry.app.entity.UserLogin;

/**
 * The {@link UpvoteDao} class provides methods to interact with the database
 * for managing upvote entries in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class UpvoteDao {

	/**
	 * The path of the SQL script for obtaining the count of upvote entries with an
	 * inquiry id.
	 */
	private static final String UPVOTE_CHECK_SCRIPT = "/database/upvote/check.sql";

	/**
	 * The path of the SQL script for obtaining the count of upvote entries with a
	 * response id.
	 */
	private static final String UPVOTE_COUNT_RESPONSE_ID_SCRIPT = "/database/upvote/count_response_id.sql";

	/**
	 * The path of the SQL script for deleting an upvote entry.
	 */
	private static final String UPVOTE_DELETE_SCRIPT = "/database/upvote/delete.sql";

	/**
	 * The path of the SQL script for inserting an upvote entry.
	 */
	private static final String UPVOTE_INSERT_SCRIPT = "/database/upvote/insert.sql";

	/**
	 * The path of the SQL script for selecting an upvote entry with a response id.
	 */
	private static final String UPVOTE_SELECT_RESPONSE_ID_SCRIPT = "/database/upvote/select_response_id.sql";

	/**
	 * The path of the SQL script for selecting an upvote entry with a user id.
	 */
	private static final String UPVOTE_SELECT_USER_ID_SCRIPT = "/database/upvote/select_user_id.sql";

	/**
	 * The runner responsible for executing the SQL scripts.
	 */
	private final SQLRunner runner;

	/**
	 * This method checks whether the given upvote entry exists inside the
	 * application database.
	 * 
	 * @param upvote The upvote entry to check
	 * @return Whether the upvote entry exists
	 * @throws DatabaseException If a database error occurs while checking for the
	 *                           upvote
	 */
	public boolean check(Upvote upvote) throws DatabaseException {
		log.debug("Selecting upvote exists (upvote=%s)".formatted(upvote));
		int responseId = upvote.getResponseId();
		int userId = upvote.getUserId();
		List<LocalResult> results = runner.runScript(UPVOTE_CHECK_SCRIPT, responseId, userId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(UPVOTE_CHECK_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(UPVOTE_CHECK_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_MESSAGE.formatted(UPVOTE_CHECK_SCRIPT));
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("exist") == 1;
	}

	/**
	 * This method returns the number of upvote entries inside the application
	 * database with the given response id.
	 * 
	 * @param responseId The response id
	 * @return The number of upvote entries in the database
	 * @throws DatabaseException If a database error occurs while retrieving the
	 *                           upvote count
	 */
	public int countFromResponseId(int responseId) throws DatabaseException {
		log.debug("Selecting upvote entry count (responseId=%s)".formatted(responseId));
		List<LocalResult> results = runner.runScript(UPVOTE_COUNT_RESPONSE_ID_SCRIPT, responseId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(UPVOTE_COUNT_RESPONSE_ID_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(UPVOTE_COUNT_RESPONSE_ID_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_MESSAGE.formatted(UPVOTE_COUNT_RESPONSE_ID_SCRIPT));
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("upvote_count");
	}

	/**
	 * This method deletes the upvote entry with the provided information from the
	 * application database.
	 * 
	 * @param upvote The upvote entry to delete
	 * @throws DatabaseException If a database error occurs while deleting the
	 *                           upvote entry
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
	 * @throws DatabaseException If a database error occurs while inserting the
	 *                           upvote entry
	 */
	public void insert(Upvote upvote) throws DatabaseException {
		log.debug("Inserting upvote entry (upvote=%s)".formatted(upvote));
		int responseId = upvote.getResponseId();
		int userId = upvote.getUserId();
		runner.runScript(UPVOTE_INSERT_SCRIPT, responseId, userId);
	}

	/**
	 * This method returns the list of user login entries from the application
	 * database where the user id matches with the user id in the upvote objects
	 * with the given response id.
	 * 
	 * @param responseId The response id
	 * @return The selected user login entries
	 * @throws DatabaseException If a database error occurs while retrieving the
	 *                           response information
	 */
	public List<UserLogin> selectFromResponseId(int responseId) throws DatabaseException {
		log.debug("Selecting upvote entries (responseId=%s)".formatted(responseId));
		List<LocalResult> results = runner.runScript(UPVOTE_SELECT_RESPONSE_ID_SCRIPT, responseId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(UPVOTE_SELECT_RESPONSE_ID_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(UPVOTE_SELECT_RESPONSE_ID_SCRIPT));
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
	 * This method returns the list of response entries from the application
	 * database where the response id matches with the response id in the upvote
	 * objects with the given user id.
	 * 
	 * @param userId The user id
	 * @return The selected response entries
	 * @throws DatabaseException If a database error occurs while retrieving the
	 *                           response information
	 */
	public List<Response> selectFromUserId(int userId) throws DatabaseException {
		log.debug("Selecting upvote entries (userId=%s)".formatted(userId));
		List<LocalResult> results = runner.runScript(UPVOTE_SELECT_USER_ID_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(UPVOTE_SELECT_USER_ID_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(UPVOTE_SELECT_USER_ID_SCRIPT));
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
