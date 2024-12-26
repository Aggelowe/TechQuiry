package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aggelowe.techquiry.database.LocalResult;
import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.Response;
import com.aggelowe.techquiry.database.exceptions.DataAccessException;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;

/**
 * The {@link ResponseDao} interface provides methods to interact with the
 * database for managing response information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class ResponseDao {

	/**
	 * The path of the SQL script for obtaining the count of response entries with
	 * an inquiry id.
	 */
	public static final String RESPONSE_COUNT_INQUIRY_ID_SCRIPT = "/database/response/count_inquiry_id.sql";

	/**
	 * The path of the SQL script for deleting a response entry.
	 */
	public static final String RESPONSE_DELETE_SCRIPT = "/database/response/delete.sql";

	/**
	 * The path of the SQL script for inserting a response entry.
	 */
	public static final String RESPONSE_INSERT_SCRIPT = "/database/response/insert.sql";

	/**
	 * The path of the SQL script for selecting response entries with an inquiry id.
	 */
	public static final String RESPONSE_SELECT_INQUIRY_ID_SCRIPT = "/database/response/select_inquiry_id.sql";

	/**
	 * The path of the SQL script for selecting a response entry.
	 */
	public static final String RESPONSE_SELECT_SCRIPT = "/database/response/select.sql";

	/**
	 * The path of the SQL script for updating a response entry.
	 */
	public static final String RESPONSE_UPDATE_SCRIPT = "/database/response/update.sql";

	/**
	 * The runner responsible for executing the SQL scripts.
	 */
	private final SQLRunner runner;

	/**
	 * This constructor constructs a new {@link ResponseDao} instance that is
	 * responsible for handling the data access for {@link Response} objects.
	 * 
	 * @param runner The SQL script runner
	 */
	public ResponseDao(SQLRunner runner) {
		this.runner = runner;
	}

	/**
	 * This method returns the number of response entries inside the application
	 * database with the given inquiry id.
	 * 
	 * @param inquiryId the inquiry id
	 * @return The number of inquiry entries in the database
	 * @throws DatabaseException If an error occurs while retrieving the response
	 *                           count
	 */
	public int countFromInquiryId(int inquiryId) throws DatabaseException {
		LOGGER.debug("Getting inquiry entry count");
		List<LocalResult> results = runner.runScript(RESPONSE_COUNT_INQUIRY_ID_SCRIPT, inquiryId);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + RESPONSE_COUNT_INQUIRY_ID_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + RESPONSE_COUNT_INQUIRY_ID_SCRIPT + " did not yeild results!");
		}
		List<Map<String, Object>> list = result.list();
		if (list.size() == 0) {
			throw new DataAccessException("The first statement in " + RESPONSE_COUNT_INQUIRY_ID_SCRIPT + " did not yeild a response count!");
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("response_count");
	}

	/**
	 * This method deletes the response with the provided response id from the
	 * application database.
	 * 
	 * @param id The id of the response entry
	 * @throws DatabaseException If an error occurs while deleting the response
	 *                           entry
	 */
	public void delete(int id) throws DatabaseException {
		LOGGER.debug("Deleting response with id " + id);
		runner.runScript(RESPONSE_DELETE_SCRIPT, id);
	}

	/**
	 * This method inserts the given {@link Response} object as a new response entry
	 * in the application database. The response id is not carried over to the
	 * database.
	 * 
	 * @param response The response to insert
	 * @return The id of the inserted response
	 * @throws DatabaseException If an error occurs while inserting the response
	 *                           entry
	 */
	public int insert(Response response) throws DatabaseException {
		LOGGER.debug("Inserting response with information " + response);
		int inquiryId = response.getInquiryId();
		int userId = response.getUserId();
		boolean anonymous = response.isAnonymous();
		String content = response.getContent();
		List<LocalResult> results = runner.runScript(RESPONSE_INSERT_SCRIPT, inquiryId, userId, anonymous, content);
		if (results.size() < 2) {
			throw new DataAccessException("The script " + RESPONSE_INSERT_SCRIPT + " did not yeild at least two results!");
		}
		LocalResult result = results.get(1);
		if (result == null) {
			throw new DataAccessException("The first statement in " + RESPONSE_INSERT_SCRIPT + " did not yeild results!");
		}
		List<Map<String, Object>> list = result.list();
		if (list.size() == 0) {
			throw new DataAccessException("The first statement in " + RESPONSE_INSERT_SCRIPT + " did not yeild a response id!");
		}
		Map<String, Object> row = list.getFirst();
		return (int) row.get("response_id");
	}

	/**
	 * This method returns and retrieves the list of {@link Response} objects with
	 * the given inquiry id from the application database.
	 * 
	 * @param inquiryId The inquiry id
	 * @return The responses with the given id
	 * @throws DatabaseException If an error occurs while retrieving the response
	 *                           information
	 */
	public List<Response> selectFromInquiryId(int inquiryId) throws DatabaseException {
		LOGGER.debug("Getting responses with inquiry id " + inquiryId);
		List<LocalResult> results = runner.runScript(RESPONSE_SELECT_INQUIRY_ID_SCRIPT, inquiryId);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + RESPONSE_SELECT_INQUIRY_ID_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + RESPONSE_SELECT_INQUIRY_ID_SCRIPT + " did not yeild results!");
		}
		List<Response> list = new ArrayList<>();
		for (Map<String, Object> row : result) {
			int id = (int) row.get("response_id");
			int userId = (int) row.get("user_id");
			boolean anonymous = (int) row.get("anonymous") == 1;
			String content = (String) row.get("content");
			Response response = new Response(id, inquiryId, userId, anonymous, content);
			list.add(response);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the only {@link Response} object with the
	 * given response id from the application database.
	 * 
	 * @param id The response id
	 * @return The response with the given id
	 * @throws DatabaseException If an error occurs while retrieving the response
	 *                           information
	 */
	public Response select(int id) throws DatabaseException {
		LOGGER.debug("Getting response with response id " + id);
		List<LocalResult> results = runner.runScript(RESPONSE_SELECT_SCRIPT, id);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + RESPONSE_SELECT_SCRIPT + " did not yeild results!");
		}
		LocalResult result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + RESPONSE_SELECT_SCRIPT + " did not yeild results!");
		}
		List<Map<String, Object>> list = result.list();
		if (list.size() == 0) {
			return null;
		}
		Map<String, Object> row = list.getFirst();
		int inquiryId = (int) row.get("inquiry_id");
		int userId = (int) row.get("user_id");
		boolean anonymous = (int) row.get("anonymous") == 1;
		String content = (String) row.get("content");
		Response response = new Response(id, inquiryId, userId, anonymous, content);
		return response;
	}

	/**
	 * This method replaces the information of an response entry with the data
	 * contained in the {@link Response} object, using the response id from the
	 * object to select the correct entry.
	 * 
	 * @param response The response to update
	 * @throws DatabaseException If an error occurs while updating the response
	 *                           entry
	 */
	public void update(Response response) throws DatabaseException {
		LOGGER.debug("Updating response with data " + response);
		int id = response.getId();
		int inquiryId = response.getInquiryId();
		int userId = response.getUserId();
		boolean anonymous = response.isAnonymous();
		String content = response.getContent();
		runner.runScript(RESPONSE_UPDATE_SCRIPT, inquiryId, userId, anonymous, content, id);
	}

}
