package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.RESPONSE_DELETE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.RESPONSE_INSERT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.RESPONSE_SELECT_INQUIRY_ID_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.RESPONSE_SELECT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.RESPONSE_UPDATE_SCRIPT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.entities.Response;
import com.aggelowe.techquiry.database.exceptions.DaoException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerException;

/**
 * The {@link ResponseDao} interface provides methods to interact with the
 * database for managing response information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class ResponseDao {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link ResponseDao} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private ResponseDao() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method deletes the response with the provided response id from the
	 * application database.
	 * 
	 * @param id The id of the response entry
	 */
	public static void delete(int id) {
		LOGGER.debug("Deleting response with id " + id);
		try {
			DatabaseManager.getRunner().runScript(RESPONSE_DELETE_SCRIPT, id);
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while deleting the response entry!", exception);
		}
	}

	/**
	 * This method inserts the given {@link Response} object as a new response entry
	 * in the application database.
	 * 
	 * @param response The response to insert
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode insert(Response response) {
		LOGGER.debug("Inserting response with information " + response);
		int id = response.getId();
		int inquiryId = response.getInquiryId();
		int userId = response.getUserId();
		boolean anonymous = response.isAnonymous();
		String content = response.getContent();
		try {
			DatabaseManager.getRunner().runScript(RESPONSE_INSERT_SCRIPT, id, inquiryId, userId, anonymous, content);
		} catch (SQLRunnerException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while inserting the response entry!", exception);
		}
		return null;
	}

	/**
	 * This method returns and retrieves the list of {@link Response} objects with
	 * the given inquiry id from the application database.
	 * 
	 * @param inquiryId The inquiry id
	 * @return The responses with the given id
	 */
	public static List<Response> selectFromInquiryId(int inquiryId) {
		LOGGER.debug("Getting responses with inquiry id " + inquiryId);
		ResultSet result;
		try {
			List<ResultSet> results = DatabaseManager.getRunner().runScript(RESPONSE_SELECT_INQUIRY_ID_SCRIPT, inquiryId);
			if (results.isEmpty()) {
				result = null;
			} else {
				result = results.getFirst();
			}
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while retrieving the response information!", exception);
		}
		if (result == null) {
			throw new DaoException("The first statement in " + RESPONSE_SELECT_INQUIRY_ID_SCRIPT + " did not yeild results!");
		}
		List<Response> list = new ArrayList<>();
		try {
			while (result.next()) {
				int id;
				int userId;
				boolean anonymous;
				String content;
				try {
					id = result.getInt("response_id");
					userId = result.getInt("user_id");
					anonymous = result.getBoolean("anonymous");
					content = result.getString("content");
				} catch (SQLException exception) {
					throw new DaoException("There was an error while retrieving the response information", exception);
				}
				Response response = new Response(id, inquiryId, userId, anonymous, content);
				list.add(response);
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the only {@link Response} object with the
	 * given response id from the application database.
	 * 
	 * @param id The response id
	 * @return The response with the given id
	 */
	public static Response select(int id) {
		LOGGER.debug("Getting response with response id " + id);
		ResultSet result;
		try {
			List<ResultSet> results = DatabaseManager.getRunner().runScript(RESPONSE_SELECT_SCRIPT, id);
			if (results.isEmpty()) {
				result = null;
			} else {
				result = results.getFirst();
			}
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while retrieving the response information!", exception);
		}
		if (result == null) {
			throw new DaoException("The first statement in " + RESPONSE_SELECT_SCRIPT + " did not yeild results!");
		}
		try {
			if (!result.next()) {
				return null;
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		int inquiryId;
		int userId;
		boolean anonymous;
		String content;
		try {
			inquiryId = result.getInt("inquiry_id");
			userId = result.getInt("user_id");
			anonymous = result.getBoolean("anonymous");
			content = result.getString("content");
		} catch (SQLException exception) {
			throw new DaoException("There was an error while retrieving the response information", exception);
		}
		Response response = new Response(id, inquiryId, userId, anonymous, content);
		return response;
	}

	/**
	 * This method replaces the information of an response entry with the data
	 * contained in the {@link Response} object, using the response id from the
	 * object to select the correct entry.
	 * 
	 * @param response The response to update
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode update(Response response) {
		LOGGER.debug("Updating response with data " + response);
		int id = response.getId();
		int inquiryId = response.getInquiryId();
		int userId = response.getUserId();
		boolean anonymous = response.isAnonymous();
		String content = response.getContent();
		try {
			DatabaseManager.getRunner().runScript(RESPONSE_UPDATE_SCRIPT, inquiryId, userId, anonymous, content, id);
		} catch (SQLRunnerException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while updating the response entry!", exception);
		}
		return null;
	}

}
