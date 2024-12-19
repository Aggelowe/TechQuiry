package com.aggelowe.techquiry.service;

import java.util.List;

import com.aggelowe.techquiry.common.SecurityUtils;
import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;
import com.aggelowe.techquiry.service.action.UserLoginActionService;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.InternalErrorException;
import com.aggelowe.techquiry.service.exceptions.InvalidRequestException;
import com.aggelowe.techquiry.service.exceptions.ServiceException;

/**
 * The {@link UserLoginService} class provides methods for managing user login
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public class UserLoginService {

	/**
	 * The object responsible for handling the data access for {@link UserLogin}
	 * objects.
	 */
	private final UserLoginDao userLoginDao;

	/**
	 * The object responsible for managing the database of the application.
	 */
	private final DatabaseManager databaseManager;

	/**
	 * This constructor constructs a new {@link UserLoginService} instance that is
	 * handling the user login operations of the application.
	 * 
	 * @param databaseManager The object managing the application database
	 */
	public UserLoginService(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
		this.userLoginDao = databaseManager.getUserLoginDao();
	}

	/**
	 * This method retrieves and returns the total count of user logins.
	 *
	 * @return The total number of user logins
	 * @throws InternalErrorException If an internal error occurs while retrieving
	 *                                the count
	 */
	public int getLoginCount() throws ServiceException {
		try {
			return userLoginDao.count();
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user count!", exception);
		}
	}

	/**
	 * This method returns the range of user logins based on the given count of
	 * logins per page and the page number.
	 *
	 * @param count The number of user logins per page
	 * @param page  The page number of user logins to return
	 * @return The requested page of user logins
	 * @throws InvalidRequestException If the count/page is smaller than 0
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the user
	 */
	public List<UserLogin> getLoginRange(int count, int page) throws ServiceException {
		if (count < 0 || page < 0) {
			throw new InvalidRequestException("The given count/page must be larger than 0!");
		}
		List<UserLogin> range;
		try {
			range = userLoginDao.range(count, count * page);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user!", exception);
		}
		return range;
	}

	/**
	 * This method returns the user login with the given user id.
	 *
	 * @param id The user id
	 * @return The user login with the given id
	 * @throws EntityNotFoundException If the requested user does not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the user
	 */
	public UserLogin findLoginByUserId(int id) throws ServiceException {
		UserLogin login;
		try {
			login = userLoginDao.select(id);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user!", exception);
		}
		if (login == null) {
			throw new EntityNotFoundException("The requested user does not exist!");
		}
		return login;
	}

	/**
	 * This method returns the user login with the given username.
	 *
	 * @param username The user's username
	 * @return The user login with the given username
	 * @throws EntityNotFoundException If the requested user does not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the user
	 */
	public UserLogin findLoginByUsername(String username) throws ServiceException {
		UserLogin login;
		try {
			login = userLoginDao.selectFromUsername(username);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user!", exception);
		}
		if (login == null) {
			throw new EntityNotFoundException("The requested user does not exist!");
		}
		return login;
	}

	/**
	 * Returns the {@link UserLogin} with the given username if the given password
	 * matches with the one contained.
	 * 
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return The user login with the give username
	 * @throws InvalidRequestException If the username or password is incorrect
	 * @throws InternalErrorException  If an internal error occurs while
	 *                                 authenticating
	 */
	public UserLogin authenticateUser(String username, String password) throws ServiceException {
		UserLogin login;
		try {
			login = userLoginDao.selectFromUsername(username);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while authenticating!", exception);
		}
		if (login == null) {
			throw new InvalidRequestException("The username or password is incorrect!");
		}
		byte[] salt = login.getPasswordSalt();
		byte[] hash = login.getPasswordHash();
		if (SecurityUtils.verifyPassword(password, salt, hash)) {
			return login;
		}
		throw new InvalidRequestException("The username or password is incorrect!");
	}

	/**
	 * This method constructs and returns the personalized service for the given
	 * user.
	 *
	 * @param current The currently logged-in user
	 * @return The service instance for making the personalized operations
	 */
	public UserLoginActionService createActionService(UserLogin current) {
		return new UserLoginActionService(databaseManager, current);
	}

}
