package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.Observer;
import com.aggelowe.techquiry.database.exceptions.DataAccessException;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;

/**
 * The {@link ObserverDao} interface provides methods to interact with the
 * database for managing observer information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class ObserverDao {

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
	 * This constructor constructs a new {@link ObserverDao} instance that is
	 * responsible for handling the data access for {@link Observer} objects.
	 * 
	 * @param runner The SQL script runner
	 */
	public ObserverDao(SQLRunner runner) {
		this.runner = runner;
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
		LOGGER.debug("Inserting observer with information " + observer);
		int inquiryId = observer.getInquiryId();
		int userId = observer.getUserId();
		runner.runScript(OBSERVER_INSERT_SCRIPT, inquiryId, userId);
	}

	/**
	 * This method returns and retrieves the list of {@link Observer} objects with
	 * the given inquiry id from the application database.
	 * 
	 * @param inquiryId The inquiry id
	 * @return The observers with the given id
	 * @throws DatabaseException If an error occurs while retrieving the observer
	 *                           information
	 */
	public List<Observer> selectFromInquiryId(int inquiryId) throws DatabaseException {
		LOGGER.debug("Getting observers with inquiry id " + inquiryId);
		List<ResultSet> results = runner.runScript(OBSERVER_SELECT_INQUIRY_ID_SCRIPT, inquiryId);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + OBSERVER_SELECT_INQUIRY_ID_SCRIPT + " did not yeild results!");
		} else {
			result = results.getFirst();
		}
		List<Observer> list = new ArrayList<>();
		try {
			while (result.next()) {
				int userId;
				try {
					userId = result.getInt("user_id");
				} catch (SQLException exception) {
					throw new DataAccessException("There was an error while retrieving the observer information", exception);
				}
				Observer observer = new Observer(inquiryId, userId);
				list.add(observer);
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
		}
		return list;
	}

	/**
	 * This method returns and retrieves the list of {@link Observer} objects with
	 * the given user id from the application database.
	 * 
	 * @param userId The user id
	 * @return The observers with the given id
	 * @throws DatabaseException If an error occurs while retrieving the observer
	 *                           information
	 */
	public List<Observer> selectFromUserId(int userId) throws DatabaseException {
		LOGGER.debug("Getting observers with user id " + userId);
		List<ResultSet> results = runner.runScript(OBSERVER_SELECT_USER_ID_SCRIPT, userId);
		ResultSet result;
		if (results.isEmpty()) {
			throw new DataAccessException("The first statement in " + OBSERVER_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		} else {
			result = results.getFirst();
		}
		List<Observer> list = new ArrayList<>();
		try {
			while (result.next()) {
				int inquiryId;
				try {
					inquiryId = result.getInt("inquiry_id");
				} catch (SQLException exception) {
					throw new DataAccessException("There was an error while retrieving the observer information", exception);
				}
				Observer observer = new Observer(inquiryId, userId);
				list.add(observer);
			}
		} catch (SQLException exception) {
			throw new DataAccessException("A database error occured!", exception);
		}
		return list;
	}

}
