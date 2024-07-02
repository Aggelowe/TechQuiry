package com.aggelowe.techquiry.database.dao;

import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.database.DatabaseConstants.OBSERVER_DELETE_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.OBSERVER_INSERT_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.OBSERVER_SELECT_INQUIRY_ID_SCRIPT;
import static com.aggelowe.techquiry.database.DatabaseConstants.OBSERVER_SELECT_USER_ID_SCRIPT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.Database;
import com.aggelowe.techquiry.database.DatabaseUtilities;
import com.aggelowe.techquiry.database.entities.Observer;
import com.aggelowe.techquiry.database.exceptions.DaoException;
import com.aggelowe.techquiry.database.exceptions.SQLExecutionException;

/**
 * The {@link ObserverDao} interface provides methods to interact with the
 * database for managing observer information in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public final class ObserverDao {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link ObserverDao} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private ObserverDao() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method deletes the observer with the provided information from the
	 * application database.
	 * 
	 * @param observer The observer to delete
	 */
	public static void delete(Observer observer) {
		LOGGER.debug("Deleting observer with information " + observer);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), OBSERVER_DELETE_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + OBSERVER_DELETE_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		int inquiryId = observer.getInquiryId();
		int userId = observer.getUserId();
		DatabaseUtilities.executeStatement(statement, inquiryId, userId);
	}

	/**
	 * This method inserts the given {@link Observer} object as a new observer entry
	 * in the application database.
	 * 
	 * @param observer The observer to insert
	 * @return The {@link SQLiteErrorCode}, if it exists
	 */
	public static SQLiteErrorCode insert(Observer observer) {
		LOGGER.debug("Inserting observer with information " + observer);
		int inquiryId = observer.getInquiryId();
		int userId = observer.getUserId();
		try {
			List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), OBSERVER_INSERT_SCRIPT);
			if (statements.size() < 1) {
				throw new DaoException("Invalid number of statements in " + OBSERVER_INSERT_SCRIPT + "!");
			}
			PreparedStatement statement = statements.getFirst();
			DatabaseUtilities.executeStatement(statement, inquiryId, userId);
		} catch (SQLExecutionException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof SQLiteException) {
				return ((SQLiteException) cause).getResultCode();
			}
			throw new DaoException("There was an error while inserting the observer entry!", exception);
		}
		return null;
	}

	/**
	 * This method returns and retrieves the list of {@link Observer} objects with
	 * the given inquiry id from the application database.
	 * 
	 * @param inquiryId The inquiry id
	 * @return The observers with the given id
	 */
	public static List<Observer> selectFromInquiryId(int inquiryId) {
		LOGGER.debug("Getting observers with inquiry id " + inquiryId);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), OBSERVER_SELECT_INQUIRY_ID_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + OBSERVER_SELECT_INQUIRY_ID_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		ResultSet result = DatabaseUtilities.executeStatement(statement, inquiryId);
		if (result == null) {
			throw new DaoException("The first statement in " + OBSERVER_SELECT_INQUIRY_ID_SCRIPT + " did not yeild results!");
		}
		List<Observer> list = new ArrayList<>();
		try {
			while (result.next()) {
				int userId;
				try {
					userId = result.getInt("user_id");
				} catch (SQLException exception) {
					throw new DaoException("There was an error while retrieving the observer information", exception);
				}
				Observer observer = new Observer(inquiryId, userId);
				list.add(observer);
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		return list;
	}
	
	/**
	 * This method returns and retrieves the list of {@link Observer} objects with
	 * the given user id from the application database.
	 * 
	 * @param userId The user id
	 * @return The observers with the given id
	 */
	public static List<Observer> selectFromUserId(int userId) {
		LOGGER.debug("Getting observers with user id " + userId);
		List<PreparedStatement> statements = DatabaseUtilities.loadStatements(Database.getConnection(), OBSERVER_SELECT_USER_ID_SCRIPT);
		if (statements.size() < 1) {
			throw new DaoException("Invalid number of statements in " + OBSERVER_SELECT_USER_ID_SCRIPT + "!");
		}
		PreparedStatement statement = statements.getFirst();
		ResultSet result = DatabaseUtilities.executeStatement(statement, userId);
		if (result == null) {
			throw new DaoException("The first statement in " + OBSERVER_SELECT_USER_ID_SCRIPT + " did not yeild results!");
		}
		List<Observer> list = new ArrayList<>();
		try {
			while (result.next()) {
				int inquiryId;
				try {
					inquiryId = result.getInt("inquiry_id");
				} catch (SQLException exception) {
					throw new DaoException("There was an error while retrieving the observer information", exception);
				}
				Observer observer = new Observer(inquiryId, userId);
				list.add(observer);
			}
		} catch (SQLException exception) {
			throw new DaoException("A database error occured!", exception);
		}
		return list;
	}

}
