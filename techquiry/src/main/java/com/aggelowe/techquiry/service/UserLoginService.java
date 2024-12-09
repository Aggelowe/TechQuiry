package com.aggelowe.techquiry.service;

import static com.aggelowe.techquiry.common.Constants.USERNAME_REGEX;

import java.util.List;
import java.util.regex.Pattern;

import com.aggelowe.techquiry.common.SecurityUtils;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.ForbiddenOperationException;
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
	 * This constructor constructs a new {@link UserLoginService} instance that is
	 * handling the user login operations of the application.
	 * 
	 * @param userLoginDao The {@link UserLogin} data access object
	 */
	public UserLoginService(UserLoginDao userLoginDao) {
		this.userLoginDao = userLoginDao;
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
	 * This method returns the range of user logins based on the given count and
	 * offset.
	 *
	 * @param count  The number of user logins
	 * @param offset The number of user logins to offset
	 * @return The requested range of user logins
	 * @throws InvalidRequestException If the count/offset is smaller than 0
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the user
	 */
	public List<UserLogin> getLoginRange(int count, int offset) throws ServiceException {
		if (count < 0 || offset < 0) {
			throw new InvalidRequestException("The given count/offset must be larger than 0!");
		}
		List<UserLogin> range;
		try {
			range = userLoginDao.range(count, offset);
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
	public UserLoginServicePersonalized asUser(UserLogin current) {
		return new UserLoginServicePersonalized(current);
	}

	/**
	 * The {@link UserLoginServicePersonalized} class is a subclass of
	 * {@link UserLoginService} whose methods provide different functionality for
	 * different users.
	 *
	 * @author Aggelowe
	 * @since 0.0.1
	 */
	class UserLoginServicePersonalized {

		/**
		 * The {@link UserLogin} representing the user currently acting
		 */
		private final UserLogin current;

		/**
		 * This constructor constructs a new {@link UserLoginServicePersonalized}
		 * instance that is handling the personalized user login operations of the
		 * application.
		 * 
		 * @param current The user initializing the operations
		 */
		public UserLoginServicePersonalized(UserLogin current) {
			this.current = current;
		}

		/**
		 * This method inserts the given {@link UserLogin} object in the database
		 *
		 * @param login The user login object to create
		 * @throws ForbiddenOperationException If the user is logged-in
		 * @throws InvalidRequestException     If the given username does not abide by
		 *                                     the requirements
		 * @throws InvalidRequestException     If the given username is not available
		 * @throws InternalErrorException      If an internal error occurs while
		 *                                     creating the user
		 * 
		 */
		public void createLogin(UserLogin login) throws ServiceException {
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
				userLoginDao.insert(login);
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
		 * @throws EntityNotFoundException     If the requested user does not exits
		 * @throws InternalErrorException      If an internal error occurred while
		 *                                     deleting the user
		 */
		public void deleteLogin(int id) throws ServiceException {
			if (current != null && current.getId() != id) {
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
			if (current != null && current.getId() != login.getId()) {
				throw new ForbiddenOperationException("The requested user deletion is forbidden!");
			}
			String username = login.getUsername();
			Pattern pattern = Pattern.compile(USERNAME_REGEX);
			if (!pattern.matcher(username).matches()) {
				throw new InvalidRequestException("The given username does not abide by the requirements!");
			}
			try {
				UserLogin userLogin = userLoginDao.select(login.getId());
				if (userLogin == null) {
					throw new EntityNotFoundException("The requested user does not exist!");
				}
				userLoginDao.update(userLogin);
			} catch (DatabaseException exception) {
				throw new InternalErrorException("An internal error occured while getting the user!", exception);
			}
		}
	}

}
