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
import com.aggelowe.techquiry.entity.Inquiry;
import com.aggelowe.techquiry.entity.Observer;
import com.aggelowe.techquiry.entity.UserLogin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link ObserverDao} interface provides methods to interact with the
 * database for managing observer information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@Log4j2
@RequiredArgsConstructor
public final class ObserverDao {

	/**
	 * The path of the SQL script for obtaining the count of observer entries with
	 * an inquiry id.
	 */
	public static final String OBSERVER_CHECK_SCRIPT = "/database/observer/check.sql";

	/**
	 * The path of the SQL script for obtaining the count of observer entries with
	 * an inquiry id.
	 */
	public static final String OBSERVER_COUNT_INQUIRY_ID_SCRIPT = "/database/observer/count_inquiry_id.sql";

	/**
	 * The path of the SQL script for deleting an observer entry.
	 */
	public static final String OBSERVER_DELETE_SCRIPT = "/database/observer/delete.sql";

	/**
	 * The path of the SQL script for inserting an observer entry.
	 */
	public static final String OBSERVER_INSERT_SCRIPT = "/database/observer/insert.sql";

	/**
	 * The path of the SQL script for selecting an observer entry with an inquiry
	 * id.
	 */
	public static final String OBSERVER_SELECT_INQUIRY_ID_SCRIPT = "/database/observer/select_inquiry_id.sql";

	/**
	 * The path of the SQL script for selecting an observer entry with a user id.
	 */
	public static final String OBSERVER_SELECT_USER_ID_SCRIPT = "/database/observer/select_user_id.sql";

	/**
	 * The runner responsible for executing the SQL scripts.
	 */
	private final SQLRunner runner;

	/**
	 * This method checks whether the given {@link Observer} object exists inside
	 * the application database.
	 * 
	 * @param observer The observer entry to check
	 * @return Whether the entry exists
	 * @throws DatabaseException If an error occurs while checking for the observer
	 */
	public boolean check(Observer observer) throws DatabaseException {
		log.debug("Selecting observer exists (observer=%s)".formatted(observer));
		int inquiryId = observer.getInquiryId();
		int userId = observer.getUserId();
		List<LocalResult> results = runner.runScript(OBSERVER_CHECK_SCRIPT, inquiryId, userId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(OBSERVER_CHECK_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(OBSERVER_CHECK_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_MESSAGE.formatted(OBSERVER_CHECK_SCRIPT));
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("exist") == 1;
	}

	/**
	 * This method returns the number of observer entries inside the application
	 * database with the given inquiry id.
	 * 
	 * @param inquiryId the inquiry id
	 * @return The number of observer entries in the database
	 * @throws DatabaseException If an error occurs while retrieving the observer
	 *                           count
	 */
	public int countFromInquiryId(int inquiryId) throws DatabaseException {
		log.debug("Selecting observer entry count (inquiryId=%s)".formatted(inquiryId));
		List<LocalResult> results = runner.runScript(OBSERVER_COUNT_INQUIRY_ID_SCRIPT, inquiryId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(OBSERVER_COUNT_INQUIRY_ID_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(OBSERVER_COUNT_INQUIRY_ID_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_MESSAGE.formatted(OBSERVER_COUNT_INQUIRY_ID_SCRIPT));
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("observer_count");
	}

	/**
	 * This method deletes the observer with the provided information from the
	 * application database.
	 * 
	 * @param observer The observer to delete
	 * @throws DatabaseException If an error occurs while deleting the observer
	 *                           entry
	 */
	public void delete(Observer observer) throws DatabaseException {
		log.debug("Deleting observer entry (observer=%s)".formatted(observer));
		int inquiryId = observer.getInquiryId();
		int userId = observer.getUserId();
		runner.runScript(OBSERVER_DELETE_SCRIPT, inquiryId, userId);
	}

	/**
	 * This method inserts the given {@link Observer} object as a new observer entry
	 * in the application database.
	 * 
	 * @param observer The observer to insert
	 * @throws DatabaseException If an error occurs while inserting the observer
	 *                           entry
	 */
	public void insert(Observer observer) throws DatabaseException {
		log.debug("Inserting observer entry (observer=%s)".formatted(observer));
		int inquiryId = observer.getInquiryId();
		int userId = observer.getUserId();
		runner.runScript(OBSERVER_INSERT_SCRIPT, inquiryId, userId);
	}

	/**
	 * This method returns and retrieves the list of {@link UserLogin} objects from
	 * the application database where the user id matches with the user id in the
	 * observer objects with the given inquiry id.
	 * 
	 * @param inquiryId The inquiry id
	 * @return The selected user logins
	 * @throws DatabaseException If an error occurs while retrieving the observer
	 *                           information
	 */
	public List<UserLogin> selectFromInquiryId(int inquiryId) throws DatabaseException {
		log.debug("Selecting observer entries (inquiryId=%s)".formatted(inquiryId));
		List<LocalResult> results = runner.runScript(OBSERVER_SELECT_INQUIRY_ID_SCRIPT, inquiryId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(OBSERVER_SELECT_INQUIRY_ID_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(OBSERVER_SELECT_INQUIRY_ID_SCRIPT));
		}
		List<UserLogin> list = new ArrayList<>();
		for (Map<String, Object> row : result) {
			int userId = (int) row.get("user_id");
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
			UserLogin userLogin = new UserLogin(userId, username, passwordHash, passwordSalt);
			list.add(userLogin);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the list of {@link Inquiry} objects from
	 * the application database where the inquiry id matches with the inquiry id in
	 * the observer objects with the given user id.
	 * 
	 * @param userId The user id
	 * @return The selected inquiries
	 * @throws DatabaseException If an error occurs while retrieving the observer
	 *                           information
	 */
	public List<Inquiry> selectFromUserId(int userId) throws DatabaseException {
		log.debug("Selecting observer entries (userId=%s)".formatted(userId));
		List<LocalResult> results = runner.runScript(OBSERVER_SELECT_USER_ID_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(OBSERVER_SELECT_USER_ID_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(OBSERVER_SELECT_USER_ID_SCRIPT));
		}
		List<Inquiry> list = new ArrayList<>();
		for (Map<String, Object> row : result) {
			int inquiryId = (int) row.get("inquiry_id");
			int authorId = (int) row.get("user_id");
			String title = (String) row.get("title");
			String content = (String) row.get("content");
			boolean anonymous = (int) row.get("anonymous") == 1;
			Inquiry inquiry = new Inquiry(inquiryId, authorId, title, content, anonymous);
			list.add(inquiry);
		}
		return list;
	}

}
