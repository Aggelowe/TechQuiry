package com.aggelowe.techquiry.service.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.UserDataDao;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entity.UserData;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.UserDataService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

/**
 * The {@link UserDataActionService} class is a dependency of
 * {@link UserDataService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
public class UserDataActionService {

	/**
	 * The object responsible for handling the data access for {@link UserData}
	 * objects.
	 */
	private final UserDataDao userDataDao;

	/**
	 * The object responsible for handling the data access for {@link UserLogin }
	 * objects.
	 */
	private final UserLoginDao userLoginDao;

	/**
	 * The {@link SessionHelper} containing the information of the user currently
	 * acting
	 */
	@Autowired
	private SessionHelper sessionHelper;

	/**
	 * This constructor constructs a new {@link UserDataActionService} instance that
	 * is handling the personalized user data operations of the application.
	 * 
	 * @param userDataDao  The user data data access object
	 * @param userLoginDao The user login data access object
	 */
	@Autowired
	public UserDataActionService(UserDataDao userDataDao, UserLoginDao userLoginDao) {
		this.userDataDao = userDataDao;
		this.userLoginDao = userLoginDao;
	}

	/**
	 * This method inserts the given {@link UserData} object in the database
	 *
	 * @param data The user data object to create
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given id
	 * @throws EntityNotFoundException     If the given id does not correspond to a
	 *                                     user login id
	 * @throws InvalidRequestException     If the given first or last name are empty
	 *                                     or if the given id is not available
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the user data
	 * 
	 */
	public void createData(UserData data) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != data.getId()) {
			throw new ForbiddenOperationException("The requested user data creation is forbidden!");
		}
		int id = data.getId();
		String firstName = data.getFirstName();
		String lastName = data.getLastName();
		if (firstName.isEmpty() || lastName.isEmpty()) {
			throw new InvalidRequestException("The given first and last name must not be empty!");
		}
		try {
			UserLogin userLogin = userLoginDao.select(data.getId());
			if (userLogin == null) {
				throw new EntityNotFoundException("The given id does not have a corresponding login!");
			}
			UserData userData = userDataDao.select(id);
			if (userData != null) {
				throw new EntityNotFoundException("The given id is not available!");
			}
			userDataDao.insert(data);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the user!", exception);
		}
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
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != id) {
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
	 * This method updates an existing user data with the data from the given
	 * {@link UserData} object.
	 * 
	 * @param data The user data
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given id
	 * @throws EntityNotFoundException     If the given id does not correspond to a
	 *                                     user login id
	 * @throws InvalidRequestException     If the given first or last name are empty
	 *                                     or if the given id is not available
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     updating the user data
	 * 
	 */
	public void updateData(UserData data) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != data.getId()) {
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
