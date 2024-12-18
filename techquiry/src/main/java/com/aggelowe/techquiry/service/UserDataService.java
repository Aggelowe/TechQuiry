package com.aggelowe.techquiry.service;

import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.dao.UserDataDao;
import com.aggelowe.techquiry.database.entities.UserData;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;
import com.aggelowe.techquiry.service.action.UserDataActionService;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.InternalErrorException;
import com.aggelowe.techquiry.service.exceptions.ServiceException;

/**
 * The {@link UserDataService} class provides methods for managing user data
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public class UserDataService {

	/**
	 * The object responsible for handling the data access for {@link UserData}
	 * objects.
	 */
	private final UserDataDao userDataDao;

	/**
	 * The object responsible for managing the database of the application.
	 */
	private final DatabaseManager databaseManager;

	/**
	 * This constructor constructs a new {@link UserDataDao} instance that is
	 * handling the user data operations of the application.
	 * 
	 * @param databaseManager The object managing the application database
	 */
	public UserDataService(DatabaseManager manager) {
		this.databaseManager = manager;
		this.userDataDao = manager.getUserDataDao();
	}

	/**
	 * This method returns the user data with the given user id.
	 *
	 * @param id The user id
	 * @return The user data with the given id
	 * @throws EntityNotFoundException If the requested user data do not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the user data
	 */
	public UserData findDataByUserId(int id) throws ServiceException {
		UserData data;
		try {
			data = userDataDao.select(id);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user data!", exception);
		}
		if (data == null) {
			throw new EntityNotFoundException("The requested user data do not exist!");
		}
		return data;
	}

	/**
	 * This method constructs and returns the personalized service for the given
	 * user.
	 *
	 * @param current The currently logged-in user
	 * @return The service instance for making the personalized operations
	 */
	public UserDataActionService createActionService(UserLogin current) {
		return new UserDataActionService(databaseManager, current);
	}

}
