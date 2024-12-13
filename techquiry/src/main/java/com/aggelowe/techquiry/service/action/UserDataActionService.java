package com.aggelowe.techquiry.service.action;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import com.aggelowe.techquiry.database.dao.UserDataDao;
import com.aggelowe.techquiry.database.entities.UserData;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerExecuteException;
import com.aggelowe.techquiry.service.UserDataService;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exceptions.InternalErrorException;
import com.aggelowe.techquiry.service.exceptions.InvalidRequestException;
import com.aggelowe.techquiry.service.exceptions.ServiceException;

/**
 * The {@link UserDataActionService} class is a dependency of
 * {@link UserDataService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public class UserDataActionService {

	/**
	 * The object responsible for handling the data access for {@link UserData}
	 * objects.
	 */
	private final UserDataDao userDataDao;

	/**
	 * The {@link UserLogin} representing the user currently acting
	 */
	private final UserLogin current;

	/**
	 * This constructor constructs a new {@link UserDataActionService} instance that
	 * is handling the personalized user data operations of the application.
	 * 
	 * @param inquiryDao The data access object for user data
	 * @param current    The user initializing the operations
	 */
	public UserDataActionService(UserDataDao userDataDao, UserLogin current) {
		this.userDataDao = userDataDao;
		this.current = current;
	}

	/**
	 * This method deletes the user data with the specified user id.
	 *
	 * @param id The user id
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given id
	 * @throws EntityNotFoundException     If the requested user data do not exist
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     deleting the user
	 */
	public void deleteData(int id) throws ServiceException {
		if (current == null || current.getId() != id) {
			throw new ForbiddenOperationException("The requested user data deletion is forbidden!");
		}
		try {
			UserData data = userDataDao.select(id);
			if (data == null) {
				throw new EntityNotFoundException("The requested user data do not exist!");
			}
			userDataDao.delete(id);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while deleting the user data!", exception);
		}
	}

	/**
	 * This method inserts the given {@link UserData} object in the database
	 *
	 * @param data The user data object to create
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given id
	 * @throws InvalidRequestException     If the given first or last name are empty
	 *                                     or if the given id is not available or
	 *                                     does not correspond to a user login id
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the user data
	 * 
	 */
	public void createData(UserData data) throws ServiceException {
		if (current == null || current.getId() != data.getId()) {
			throw new ForbiddenOperationException("The requested user data update is forbidden!");
		}
		int id = data.getId();
		String firstName = data.getFirstName();
		String lastName = data.getLastName();
		if (firstName.isEmpty() || lastName.isEmpty()) {
			throw new InvalidRequestException("The given first and last name must not be empty!");
		}
		try {
			UserData userData = userDataDao.select(id);
			if (userData != null) {
				throw new InvalidRequestException("The given id is not available!");
			}
			userDataDao.insert(data);
		} catch (SQLRunnerExecuteException exception) {
			if (exception.getCause() instanceof SQLiteException) {
				SQLiteException cause = (SQLiteException) exception.getCause();
				SQLiteErrorCode code = SQLiteErrorCode.getErrorCode(cause.getErrorCode());
				if (code == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY) {
					throw new InvalidRequestException("The user login with the given id does not exist!", exception);
				}
			}
			throw new InternalErrorException("An internal error occured while creating the user!", exception);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the user!", exception);
		}
	}

	/**
	 * This method updates an existing user data with the data from the given
	 * {@link UserData} object.
	 * 
	 * @param data The user data
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given id
	 * @throws InvalidRequestException     If the given first or last name are empty
	 *                                     or if the given id is not available
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     updating the user data
	 * 
	 */
	public void updateLogin(UserData data) throws ServiceException {
		if (current == null || current.getId() != data.getId()) {
			throw new ForbiddenOperationException("The requested user update is forbidden!");
		}
		String firstName = data.getFirstName();
		String lastName = data.getLastName();
		if (firstName.isEmpty() || lastName.isEmpty()) {
			throw new InvalidRequestException("The given first and last name must not be empty!");
		}

		try {
			UserData userData = userDataDao.select(data.getId());
			if (userData == null) {
				throw new EntityNotFoundException("The requested user does not exist!");
			}
			userDataDao.update(data);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user!", exception);
		}
	}

}
