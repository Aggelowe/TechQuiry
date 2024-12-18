package com.aggelowe.techquiry.service.action;

import static com.aggelowe.techquiry.common.Constants.USERNAME_REGEX;

import java.util.regex.Pattern;

import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;
import com.aggelowe.techquiry.service.UserLoginService;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exceptions.InternalErrorException;
import com.aggelowe.techquiry.service.exceptions.InvalidRequestException;
import com.aggelowe.techquiry.service.exceptions.ServiceException;

/**
 * The {@link UserLoginActionService} class is a dependency of
 * {@link UserLoginService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public class UserLoginActionService {

	/**
	 * The object responsible for handling the data access for {@link UserLogin}
	 * objects.
	 */
	private final UserLoginDao userLoginDao;

	/**
	 * The {@link UserLogin} representing the user currently acting
	 */
	private final UserLogin current;

	/**
	 * This constructor constructs a new {@link UserLoginActionService} instance
	 * that is handling the personalized user login operations of the application.
	 * 
	 * @param databaseManager The object managing the application database
	 * @param current         The user initializing the operations
	 */
	public UserLoginActionService(DatabaseManager databaseManager, UserLogin current) {
		this.userLoginDao = databaseManager.getUserLoginDao();
		this.current = current;
	}

	/**
	 * This method inserts the given {@link UserLogin} object in the database
	 *
	 * @param login The user login object to create
	 * @return The id of the created {@link UserLogin}
	 * @throws ForbiddenOperationException If the user is logged-in
	 * @throws InvalidRequestException     If the given username does not abide by
	 *                                     the requirements or if the given username
	 *                                     is not available
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the user
	 * 
	 */
	public int createLogin(UserLogin login) throws ServiceException {
		if (current != null) {
			throw new ForbiddenOperationException("Creating users while logged-in is forbidden!");
		}
		String username = login.getUsername();
		Pattern pattern = Pattern.compile(USERNAME_REGEX);
		if (!pattern.matcher(username).matches()) {
			throw new InvalidRequestException("The given username does not abide by the requirements!");
		}
		try {
			UserLogin userLogin = userLoginDao.selectFromUsername(login.getUsername());
			if (userLogin != null) {
				throw new InvalidRequestException("The given username is not available!");
			}
			return userLoginDao.insert(login);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the user!", exception);
		}
	}

	/**
	 * This method deletes the user login with the specified user id.
	 *
	 * @param id The user id
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given id
	 * @throws EntityNotFoundException     If the requested user does not exist
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     deleting the user
	 */
	public void deleteLogin(int id) throws ServiceException {
		if (current == null || current.getId() != id) {
			throw new ForbiddenOperationException("The requested user deletion is forbidden!");
		}
		try {
			UserLogin login = userLoginDao.select(id);
			if (login == null) {
				throw new EntityNotFoundException("The requested user does not exist!");
			}
			userLoginDao.delete(id);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while deleting the user!", exception);
		}
	}

	/**
	 * This method updates an existing user login with the data from the given
	 * {@link UserLogin} object.
	 * 
	 * @param login The user login
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     same id as the one contained in the given
	 *                                     login.
	 * @throws EntityNotFoundException     If the requested user does not exist
	 * @throws InvalidRequestException     If the given username does not abide by
	 *                                     the requirements.
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     updating the user
	 * 
	 */
	public void updateLogin(UserLogin login) throws ServiceException {
		if (current == null || current.getId() != login.getId()) {
			throw new ForbiddenOperationException("The requested user update is forbidden!");
		}
		String username = login.getUsername();
		Pattern pattern = Pattern.compile(USERNAME_REGEX);
		if (!pattern.matcher(username).matches()) {
			throw new InvalidRequestException("The given username does not abide by the requirements!");
		}
		try {
			UserLogin idLogin = userLoginDao.select(login.getId());
			if (idLogin == null) {
				throw new EntityNotFoundException("The requested user does not exist!");
			}
			UserLogin usernameLogin = userLoginDao.selectFromUsername(login.getUsername());
			if (usernameLogin != null) {
				throw new InvalidRequestException("The given username is not available!");
			}
			userLoginDao.update(login);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user!", exception);
		}
	}
}