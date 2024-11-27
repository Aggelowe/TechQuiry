package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.INQUIRY_COUNT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.INQUIRY_DELETE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.INQUIRY_INSERT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.INQUIRY_RANGE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.INQUIRY_SELECT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.INQUIRY_UPDATE_SCRIPT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.entities.Inquiry;
import com.aggelowe.techquiry.database.exceptions.DaoException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerException;

/**
 * The {@link InquiryDao} interface provides methods to interact with the
 * database for managing inquiry information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class InquiryDao {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link InquiryDao} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private InquiryDao() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method returns the number of inquiry entries inside the application
	 * database.
	 * 
	 * @return The number of inquiry entries in the database
	 */
	public static int count() {
		LOGGER.debug("Getting inquiry entry count");
		ResultSet result;
		try {
			List<ResultSet> results = DatabaseManager.getRunner().runScript(INQUIRY_COUNT_SCRIPT);
			if (results.isEmpty()) {
				result = null;
			} else {
				result = results.getFirst();
			}
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while retrieving the inquiry count!", exception);
		}
		DatabaseManager.getRunner().runScript(INQUIRY_COUNT_SCRIPT);
		if (result == null) {
			throw new DaoException("The first statement in " + INQUIRY_COUNT_SCRIPT + " did not yeild a result!");
		}
		int count;
		try {
			count = result.getInt("inquiry_count");
		} catch (SQLException exception) {
			throw new DaoException("There was an error while retrieving the inquiry count!", exception);
		}
		return count;
	}

	/**
	 * This method deletes the inquiry with the provided inquiry id from the
	 * application database.
	 * 
	 * @param id The id of the inquiry entry
	 */
	public static void delete(int id) {
		LOGGER.debug("Deleting inquiry with id " + id);
		try {
			DatabaseManager.getRunner().runScript(INQUIRY_DELETE_SCRIPT, id);
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while deleting the inquiry entry!", exception);
		}
	}

	/**
	 * This method inserts the given {@link Inquiry} object as a new inquiry entry
	 * in the application database.
	 * 
	 * @param inquiry The inquiry to insert
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode insert(Inquiry inquiry) {
		LOGGER.debug("Inserting inquiry with information " + inquiry);
		int id = inquiry.getId();
		int userId = inquiry.getUserId();
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		boolean anonymous = inquiry.isAnonymous();
		try {
			DatabaseManager.getRunner().runScript(INQUIRY_INSERT_SCRIPT, id, userId, title, content, anonymous);
		} catch (SQLRunnerException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while inserting the inquiry entry!", exception);
		}
		return null;
	}

	/**
	 * This method returns and retrieves a list of {@link Inquiry} objects from the
	 * application database, that has the given size and starts with the given
	 * offset.
	 * 
	 * @param count  The number of entries
	 * @param offset The number of entries to skip
	 * @return The selected range
	 */
	public static List<Inquiry> range(int count, int offset) {
		LOGGER.debug("Getting " + count + " inquiry entries with offset " + offset);
		ResultSet result;
		try {
			List<ResultSet> results = DatabaseManager.getRunner().runScript(INQUIRY_RANGE_SCRIPT, offset, count);
			if (results.isEmpty()) {
				result = null;
			} else {
				result = results.getFirst();
			}
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while retrieving the inquiry information!", exception);
		}
		List<Inquiry> range = new ArrayList<>(count);
		try {
			while (result.next()) {
				int id;
				int userId;
				String title;
				String content;
				boolean anonymous;
				try {
					id = result.getInt("inquiry_id");
					userId = result.getInt("user_id");
					title = result.getString("title");
					content = result.getString("content");
					anonymous = result.getBoolean("anonymous");
				} catch (SQLException exception) {
					throw new DaoException("There was an error while retrieving the inquiry information", exception);
				}
				Inquiry inquiry = new Inquiry(id, userId, title, content, anonymous);
				range.add(inquiry);
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		return range;
	}

	/**
	 * This method returns and retrieves the only {@link Inquiry} object with the
	 * given inquiry id from the application database.
	 * 
	 * @param id The inquiry id
	 * @return The inquiry with the given id
	 */
	public static Inquiry select(int id) {
		LOGGER.debug("Getting inquiry with inquiry id " + id);
		ResultSet result;
		try {
			List<ResultSet> results = DatabaseManager.getRunner().runScript(INQUIRY_SELECT_SCRIPT, id);
			if (results.isEmpty()) {
				result = null;
			} else {
				result = results.getFirst();
			}
		} catch (SQLRunnerException exception) {
			throw new DaoException("There was an error while retrieving the inquiry information!", exception);
		}
		if (result == null) {
			throw new DaoException("The first statement in " + INQUIRY_SELECT_SCRIPT + " did not yeild results!");
		}
		try {
			if (!result.next()) {
				return null;
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		int userId;
		String title;
		String content;
		boolean anonymous;
		try {
			userId = result.getInt("user_id");
			title = result.getString("title");
			content = result.getString("content");
			anonymous = result.getBoolean("anonymous");
		} catch (SQLException exception) {
			throw new DaoException("There was an error while retrieving the inquiry information", exception);
		}
		Inquiry inquiry = new Inquiry(id, userId, title, content, anonymous);
		return inquiry;
	}

	/**
	 * This method replaces the information of an inquiry entry with the data
	 * contained in the {@link Inquiry} object, using the inquiry id from the object
	 * to select the correct entry.
	 * 
	 * @param inquiry The inquiry to update
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode update(Inquiry inquiry) {
		LOGGER.debug("Updating inquiry with data " + inquiry);
		int id = inquiry.getId();
		int userId = inquiry.getUserId();
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		boolean anonymous = inquiry.isAnonymous();
		try {
			DatabaseManager.getRunner().runScript(INQUIRY_UPDATE_SCRIPT, userId, title, content, anonymous, id);
		} catch (SQLRunnerException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while updating the inquiry entry!", exception);
		}
		return null;
	}

}
