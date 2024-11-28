package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.OBSERVER_DELETE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.OBSERVER_INSERT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.OBSERVER_SELECT_INQUIRY_ID_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.OBSERVER_SELECT_USER_ID_SCRIPT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aggelowe.techquiry.common.exceptions.IllegalConstructionException;
import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.entities.Observer;
import com.aggelowe.techquiry.database.exceptions.DataAccessException;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerLoadException;

/**
 * The {@link ObserverDao} interface provides methods to interact with the
 * database for managing observer information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class ObserverDao {

	/**
	 * This constructor will throw an {@link IllegalConstructionException} whenever invoked.
	 * {@link ObserverDao} objects should <b>not</b> be constructible.
	 * 
	 * @throws IllegalConstructionException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private ObserverDao() throws IllegalConstructionException {
		throw new IllegalConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method deletes the observer with the provided information from the
	 * application database.
	 * 
	 * @param observer The observer to delete
	 * @throws DatabaseException If an error occurs while deleting the observer entry
	 */
	public static void delete(Observer observer) throws DatabaseException {
		int inquiryId = observer.getInquiryId();
		int userId = observer.getUserId();
		try {
			DatabaseManager.getRunner().runScript(OBSERVER_DELETE_SCRIPT, inquiryId, userId);
		} catch (SQLRunnerLoadException exception) {
			throw new DataAccessException("There was an error while deleting the observer entry!", exception);
		}
	}

	/**
	 * This method inserts the given {@link Observer} object as a new observer entry
	 * in the application database.
	 * 
	 * @param observer The observer to insert
	 * @throws DatabaseException If an error occurs while inserting the observer entry
	 */
	public static void insert(Observer observer) throws DatabaseException {
		LOGGER.debug("Inserting observer with information " + observer);
		int inquiryId = observer.getInquiryId();
		int userId = observer.getUserId();
		try {
			DatabaseManager.getRunner().runScript(OBSERVER_INSERT_SCRIPT, inquiryId, userId);
		} catch (SQLRunnerLoadException exception) {
			throw new DataAccessException("There was an error while inserting the observer entry!", exception);
		}
	}

	/**
	 * This method returns and retrieves the list of {@link Observer} objects with
	 * the given inquiry id from the application database.
	 * 
	 * @param inquiryId The inquiry id
	 * @return The observers with the given id
	 * @throws DatabaseException If an error occurs while retrieving the observer information
	 */
	public static List<Observer> selectFromInquiryId(int inquiryId) throws DatabaseException {
		LOGGER.debug("Getting observers with inquiry id " + inquiryId);
		ResultSet result;
		try {
			List<ResultSet> results = DatabaseManager.getRunner().runScript(OBSERVER_SELECT_INQUIRY_ID_SCRIPT, inquiryId);
			if (results.isEmpty()) {
				result = null;
			} else {
				result = results.getFirst();
			}
		} catch (SQLRunnerLoadException exception) {
			throw new DataAccessException("There was an error while retrieving the observer information!", exception);
		}
		if (result == null) {
			throw new DataAccessException("The first statement in " + OBSERVER_SELECT_INQUIRY_ID_SCRIPT + " did not yeild results!");
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
	 * @throws DatabaseException If an error occurs while retrieving the observer information
	 */
	public static List<Observer> selectFromUserId(int userId) throws DatabaseException {
		LOGGER.debug("Getting observers with user id " + userId);
		ResultSet result;
		try {
			List<ResultSet> results = DatabaseManager.getRunner().runScript(OBSERVER_SELECT_USER_ID_SCRIPT, userId);
			if (results.isEmpty()) {
				result = null;
			} else {
				result = results.getFirst();
			}
		} catch (SQLRunnerLoadException exception) {
			throw new DataAccessException("There was an error while retrieving the observer information!", exception);
		}
		if (result == null) {
			throw new DataAccessException("The first statement in " + OBSERVER_SELECT_USER_ID_SCRIPT + " did not yeild results!");
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
