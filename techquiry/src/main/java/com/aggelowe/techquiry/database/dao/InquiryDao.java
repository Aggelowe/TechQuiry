package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.Inquiry;
import com.aggelowe.techquiry.database.exceptions.DataAccessException;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;

/**
 * The {@link InquiryDao} interface provides methods to interact with the
 * database for managing inquiry information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
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
	 * This constructor constructs a new {@link InquiryDao} instance that is
	 * responsible for handling the data access for {@link Inquiry} objects.
	 * 
	 * @param runner The SQL script runner
	 */
	@Autowired
	public InquiryDao(final SQLRunner runner) {
		this.runner = runner;
	}

	/**
	 * This method returns the number of inquiry entries inside the application
	 * database.
	 * 
	 * @return The number of inquiry entries in the database
	 * @throws DatabaseException If an error occurs while retrieving the inquiry
	 *                           count
	 */
	public int count() throws DatabaseException {
		LOGGER.debug("Getting inquiry entry count");
		List<ResultSet> results = runner.runScript(INQUIRY_COUNT_SCRIPT);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + INQUIRY_COUNT_SCRIPT + " did not yeild results!");
		}
		ResultSet result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + INQUIRY_COUNT_SCRIPT + " did not yeild results!");
		}
		int count;
		try {
			count = result.getInt("inquiry_count");
		} catch (SQLException exception) {
			throw new DataAccessException("There was an error while retrieving the inquiry count!", exception);
		}
		return count;
	}

	/**
	 * This method deletes the inquiry with the provided inquiry id from the
	 * application database.
	 * 
	 * @param id The id of the inquiry entry
	 * @throws DatabaseException If an error occurs while deleting the inquiry entry
	 */
	public void delete(int id) throws DatabaseException {
		LOGGER.debug("Deleting inquiry with id " + id);
		runner.runScript(INQUIRY_DELETE_SCRIPT, id);
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
		LOGGER.debug("Inserting inquiry with information " + inquiry);
		int userId = inquiry.getUserId();
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		boolean anonymous = inquiry.isAnonymous();
		List<ResultSet> results = runner.runScript(INQUIRY_INSERT_SCRIPT, userId, title, content, anonymous);
		if (results.size() < 2) {
			throw new DataAccessException("The script " + INQUIRY_INSERT_SCRIPT + " did not yeild at least two results!");
		}
		ResultSet result = results.get(1);
		if (result == null) {
			throw new DataAccessException("The first statement in " + INQUIRY_INSERT_SCRIPT + " did not yeild results!");
		}
		int id;
		try {
			id = result.getInt("inquiry_id");
		} catch (SQLException exception) {
			throw new DataAccessException("There was an error while retrieving the inserted inquiry id!", exception);
		}
		return id;
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
		LOGGER.debug("Getting " + count + " inquiry entries with offset " + offset);
		List<ResultSet> results = runner.runScript(INQUIRY_RANGE_SCRIPT, offset, count);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + INQUIRY_RANGE_SCRIPT + " did not yeild results!");
		}
		ResultSet result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + INQUIRY_RANGE_SCRIPT + " did not yeild results!");
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
					throw new DataAccessException("There was an error while retrieving the inquiry information", exception);
				}
				Inquiry inquiry = new Inquiry(id, userId, title, content, anonymous);
				range.add(inquiry);
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
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
		LOGGER.debug("Getting responses with user id " + userId);
		List<ResultSet> results = runner.runScript(INQUIRY_SELECT_USER_ID_NON_ANONYMOUS_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + INQUIRY_SELECT_USER_ID_NON_ANONYMOUS_SCRIPT + " did not yeild results!");
		}
		ResultSet result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + INQUIRY_SELECT_USER_ID_NON_ANONYMOUS_SCRIPT + " did not yeild results!");
		}
		List<Inquiry> list = new ArrayList<>();
		try {
			while (result.next()) {
				int id;
				String title;
				String content;
				try {
					id = result.getInt("inquiry_id");
					title = result.getString("title");
					content = result.getString("content");
				} catch (SQLException exception) {
					throw new DataAccessException("There was an error while retrieving the inquiry information", exception);
				}
				Inquiry inquiry = new Inquiry(id, userId, title, content, false);
				list.add(inquiry);
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
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
		LOGGER.debug("Getting responses with user id " + userId);
		List<ResultSet> results = runner.runScript(INQUIRY_SELECT_USER_ID_SCRIPT, userId);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + INQUIRY_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		}
		ResultSet result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + INQUIRY_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		}
		List<Inquiry> list = new ArrayList<>();
		try {
			while (result.next()) {
				int id;
				String title;
				String content;
				boolean anonymous;
				try {
					id = result.getInt("inquiry_id");
					title = result.getString("title");
					content = result.getString("content");
					anonymous = result.getBoolean("anonymous");
				} catch (SQLException exception) {
					throw new DataAccessException("There was an error while retrieving the inquiry information", exception);
				}
				Inquiry inquiry = new Inquiry(id, userId, title, content, anonymous);
				list.add(inquiry);
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the only {@link Inquiry} object with the
	 * given inquiry id from the application database.
	 * 
	 * @param id The inquiry id
	 * @return The inquiry with the given id
	 * @throws DatabaseException If an error occurs while retrieving the inquiry
	 *                           information
	 */
	public Inquiry select(int id) throws DatabaseException {
		LOGGER.debug("Getting inquiry with inquiry id " + id);
		List<ResultSet> results = runner.runScript(INQUIRY_SELECT_SCRIPT, id);
		if (results.isEmpty()) {
			throw new DataAccessException("The script " + INQUIRY_SELECT_SCRIPT + " did not yeild results!");
		}
		ResultSet result = results.getFirst();
		if (result == null) {
			throw new DataAccessException("The first statement in " + INQUIRY_SELECT_SCRIPT + " did not yeild results!");
		}
		try {
			if (!result.next()) {
				return null;
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
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
			throw new DataAccessException("There was an error while retrieving the inquiry information", exception);
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
	 * @throws DatabaseException If an error occurs while updating the inquiry entry
	 */
	public void update(Inquiry inquiry) throws DatabaseException {
		LOGGER.debug("Updating inquiry with data " + inquiry);
		int id = inquiry.getId();
		int userId = inquiry.getUserId();
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		boolean anonymous = inquiry.isAnonymous();
		runner.runScript(INQUIRY_UPDATE_SCRIPT, userId, title, content, anonymous, id);
	}

}
