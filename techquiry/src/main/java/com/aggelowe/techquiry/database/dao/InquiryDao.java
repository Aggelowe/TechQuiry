package com.aggelowe.techquiry.database.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.database.LocalResult;
import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.exception.DataAccessException;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.Inquiry;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link InquiryDao} interface provides methods to interact with the
 * database for managing inquiry information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@Log4j2
@RequiredArgsConstructor
public final class InquiryDao {

	/**
	 * The path of the SQL script for obtaining the count of inquiry entries.
	 */
	public static final String INQUIRY_COUNT_SCRIPT = "/database/inquiry/count.sql";

	/**
	 * The path of the SQL script for deleting an inquiry entry.
	 */
	public static final String INQUIRY_DELETE_SCRIPT = "/database/inquiry/delete.sql";

	/**
	 * The path of the SQL script for inserting an inquiry entry.
	 */
	public static final String INQUIRY_INSERT_SCRIPT = "/database/inquiry/insert.sql";

	/**
	 * The path of the SQL script for selecting an inquiry entry range.
	 */
	public static final String INQUIRY_RANGE_SCRIPT = "/database/inquiry/range.sql";

	/**
	 * The path of the SQL script for selecting inquiry entries with a user id which
	 * are non-anonymous.
	 */
	public static final String INQUIRY_SELECT_USER_ID_NON_ANONYMOUS_SCRIPT = "/database/inquiry/select_user_id_non_anonymous.sql";

	/**
	 * The path of the SQL script for selecting inquiry entries with a user id.
	 */
	public static final String INQUIRY_SELECT_USER_ID_SCRIPT = "/database/inquiry/select_user_id.sql";

	/**
	 * The path of the SQL script for selecting an inquiry entry.
	 */
	public static final String INQUIRY_SELECT_SCRIPT = "/database/inquiry/select.sql";

	/**
	 * The path of the SQL script for updating an inquiry entry.
	 */
	public static final String INQUIRY_UPDATE_SCRIPT = "/database/inquiry/update.sql";

	/**
	 * The runner responsible for executing the SQL scripts.
	 */
	private final SQLRunner runner;

	/**
	 * This method returns the number of inquiry entries inside the application
	 * database.
	 * 
	 * @return The number of inquiry entries in the database
	 * @throws DatabaseException If an error occurs while retrieving the inquiry
	 *                           count
	 */
	public int count() throws DatabaseException {
		log.debug("Selecting inquiry entry count");
		List<LocalResult> results = runner.runScript(INQUIRY_COUNT_SCRIPT);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(INQUIRY_COUNT_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(INQUIRY_COUNT_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_MESSAGE.formatted(INQUIRY_COUNT_SCRIPT));
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("inquiry_count");
	}

	/**
	 * This method deletes the inquiry with the provided inquiry id from the
	 * application database.
	 * 
	 * @param inquiryId The id of the inquiry entry
	 * @throws DatabaseException If an error occurs while deleting the inquiry entry
	 */
	public void delete(int inquiryId) throws DatabaseException {
		log.debug("Deleting inquiry entry (inquiryId=%s)".formatted(inquiryId));
		runner.runScript(INQUIRY_DELETE_SCRIPT, inquiryId);
	}

	/**
	 * This method inserts the given {@link Inquiry} object as a new inquiry entry
	 * in the application database. The inquiry id is not carried over to the
	 * database.
	 * 
	 * @param inquiry The inquiry to insert
	 * @return The id of the inserted inquiry
	 * @throws DatabaseException If an error occurs while inserting the inquiry
	 *                           entry
	 */
	public int insert(Inquiry inquiry) throws DatabaseException {
		log.debug("Inserting inquiry entry (inquiry=%s)".formatted(inquiry));
		int userId = inquiry.getUserId();
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		boolean anonymous = inquiry.getAnonymous();
		List<LocalResult> results = runner.runScript(INQUIRY_INSERT_SCRIPT, userId, title, content, anonymous);
		if (results.size() < 2) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(INQUIRY_INSERT_SCRIPT));
		}
		LocalResult result = results.get(1);
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(INQUIRY_INSERT_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_MESSAGE.formatted(INQUIRY_INSERT_SCRIPT));
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("inquiry_id");
	}

	/**
	 * This method returns and retrieves a list of {@link Inquiry} objects from the
	 * application database, that has the given size and starts with the given
	 * offset.
	 * 
	 * @param count  The number of entries
	 * @param offset The number of entries to skip
	 * @return The selected range
	 * @throws DatabaseException If an error occurs while retrieving the inquiry
	 *                           information
	 */
	public List<Inquiry> range(int count, int offset) throws DatabaseException {
		log.debug("Selecting inquiry entries (count=%s, offset=%s)".formatted(count, offset));
		List<LocalResult> results = runner.runScript(INQUIRY_RANGE_SCRIPT, offset, count);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(INQUIRY_RANGE_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(INQUIRY_RANGE_SCRIPT));
		}
		List<Inquiry> range = new ArrayList<>(count);
		for (Map<String, Object> row : result) {
			int id = (int) row.get("inquiry_id");
			int userId = (int) row.get("user_id");
			String title = (String) row.get("title");
			String content = (String) row.get("content");
			boolean anonymous = (int) row.get("anonymous") == 1;
			Inquiry inquiry = new Inquiry(id, userId, title, content, anonymous);
			range.add(inquiry);
		}
		return range;
	}

	/**
	 * This method returns and retrieves the list of non-anonymous {@link Inquiry}
	 * objects with the given user id from the application database.
	 * 
	 * @param userId The user id
	 * @return The non-anonymous inquiries with the given id
	 * @throws DatabaseException If an error occurs while retrieving the inquiry
	 *                           information
	 */
	public List<Inquiry> selectFromUserIdNonAnonymous(int userId) throws DatabaseException {
		log.debug("Selecting non-anonymous inquiry entries (userId=%s)".formatted(userId));
		List<LocalResult> results = runner.runScript(INQUIRY_SELECT_USER_ID_NON_ANONYMOUS_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(INQUIRY_SELECT_USER_ID_NON_ANONYMOUS_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(INQUIRY_SELECT_USER_ID_NON_ANONYMOUS_SCRIPT));
		}
		List<Inquiry> list = new ArrayList<>();
		for (Map<String, Object> row : result) {
			int id = (int) row.get("inquiry_id");
			String title = (String) row.get("title");
			String content = (String) row.get("content");
			boolean anonymous = (int) row.get("anonymous") == 1;
			Inquiry inquiry = new Inquiry(id, userId, title, content, anonymous);
			list.add(inquiry);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the list of {@link Inquiry} objects with
	 * the given user id from the application database.
	 * 
	 * @param userId The user id
	 * @return The inquiries with the given id
	 * @throws DatabaseException If an error occurs while retrieving the inquiry
	 *                           information
	 */
	public List<Inquiry> selectFromUserId(int userId) throws DatabaseException {
		log.debug("Selecting inquiry entries (userId=%s)".formatted(userId));
		List<LocalResult> results = runner.runScript(INQUIRY_SELECT_USER_ID_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(INQUIRY_SELECT_USER_ID_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(INQUIRY_SELECT_USER_ID_SCRIPT));
		}
		List<Inquiry> list = new ArrayList<>();
		for (Map<String, Object> row : result) {
			int id = (int) row.get("inquiry_id");
			String title = (String) row.get("title");
			String content = (String) row.get("content");
			boolean anonymous = (int) row.get("anonymous") == 1;
			Inquiry inquiry = new Inquiry(id, userId, title, content, anonymous);
			list.add(inquiry);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the only {@link Inquiry} object with the
	 * given inquiry id from the application database.
	 * 
	 * @param inquiryId The inquiry id
	 * @return The inquiry with the given id
	 * @throws DatabaseException If an error occurs while retrieving the inquiry
	 *                           information
	 */
	public Inquiry select(int inquiryId) throws DatabaseException {
		log.debug("Selecting inquiry entry (inquiryId=%s)".formatted(inquiryId));
		List<LocalResult> results = runner.runScript(INQUIRY_SELECT_SCRIPT, inquiryId);
		if (results.isEmpty()) {
			throw new DataAccessException(DataAccessException.INVALID_RESULT_COUNT_MESSAGE.formatted(INQUIRY_SELECT_SCRIPT));
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException(DataAccessException.MISSING_RESULT_MESSAGE.formatted(INQUIRY_SELECT_SCRIPT));
		}
		List<Map<String, Object>> list = result.list();
		if (list.isEmpty()) {
			return null;
		}
		Map<String, Object> row = list.getFirst();
		int userId = (int) row.get("user_id");
		String title = (String) row.get("title");
		String content = (String) row.get("content");
		boolean anonymous = (int) row.get("anonymous") == 1;
		return new Inquiry(inquiryId, userId, title, content, anonymous);
	}

	/**
	 * This method replaces the information of an inquiry entry with the data
	 * contained in the {@link Inquiry} object, using the inquiry id from the object
	 * to select the correct entry.
	 * 
	 * @param inquiry The inquiry to update
	 * @throws DatabaseException If an error occurs while updating the inquiry entry
	 */
	public void update(Inquiry inquiry) throws DatabaseException {
		log.debug("Updating inquiry entry (inquiry=%s)".formatted(inquiry));
		int id = inquiry.getInquiryId();
		int userId = inquiry.getUserId();
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		boolean anonymous = inquiry.getAnonymous();
		runner.runScript(INQUIRY_UPDATE_SCRIPT, userId, title, content, anonymous, id);
	}

}
