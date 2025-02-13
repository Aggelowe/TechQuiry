package com.aggelowe.techquiry.service.action;

import static com.aggelowe.techquiry.common.Constants.USERNAME_REGEX;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.common.SecurityUtils;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.UserLoginService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

import lombok.RequiredArgsConstructor;

/**
 * The {@link UserLoginActionService} class is a component of
 * {@link UserLoginService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
public class UserLoginActionService {

	/**
	 * The object responsible for handling the data access for {@link UserLogin}
	 * objects.
	 */
	private final UserLoginDao userLoginDao;

	/**
	 * The {@link SessionHelper} containing the information of the user currently
	 * acting
	 */
	private final SessionHelper sessionHelper;

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
		Authentication current = sessionHelper.getAuthentication();
		if (current != null) {
			throw new ForbiddenOperationException("Creating users while logged-in is forbidden!");
		}
		String username = login.getUsername();
		Pattern pattern = Pattern.compile(USERNAME_REGEX);
		if (!pattern.matcher(username).matches()) {
			throw new InvalidRequestException("The given username does not abide by the requirements!");
		}
		int userId;
		try {
			UserLogin userLogin = userLoginDao.selectFromUsername(login.getUsername());
			if (userLogin != null) {
				throw new InvalidRequestException("The given username is not available!");
			}
			userId = userLoginDao.insert(login);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the user!", exception);
		}
		Authentication authentication = new Authentication(userId);
		sessionHelper.setAuthentication(authentication);
		return userId;
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
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != id) {
			throw new ForbiddenOperationException("The requested user deletion is forbidden!");
		}
		try {
			UserLogin login = userLoginDao.select(id);
			if (login == null) {
				throw new EntityNotFoundException("The requested user does not exist!");
			}
			sessionHelper.setAuthentication(null);
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
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != login.getUserId()) {
			throw new ForbiddenOperationException("The requested user update is forbidden!");
		}
		String username = login.getUsername();
		Pattern pattern = Pattern.compile(USERNAME_REGEX);
		if (!pattern.matcher(username).matches()) {
			throw new InvalidRequestException("The given username does not abide by the requirements!");
		}
		try {
			UserLogin idLogin = userLoginDao.select(login.getUserId());
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

	/**
	 * Sets the respective {@link Authentication} in the respective user session if
	 * the given password matches with the one contained in the database.
	 * 
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return The id of the logged in {@link UserLogin}
	 * @throws ForbiddenOperationException If there is an active session
	 * @throws InvalidRequestException     If the username or password is incorrect
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     authenticating
	 */
	public int authenticateUser(String username, String password) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current != null) {
			throw new ForbiddenOperationException("Logging in with an active session is forbidden!");
		}
		if (username == null || password == null) {
			throw new InvalidRequestException("The username and/or password is NULL!");
		}
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
			int userId = login.getUserId();
			Authentication authentication = new Authentication(userId);
			sessionHelper.setAuthentication(authentication);
			return userId;
		} else {
			throw new InvalidRequestException("The username or password is incorrect!");
		}
	}

	/**
	 * Sets the {@link Authentication} in the respective user session to NULL.
	 * 
	 * @throws ForbiddenOperationException If there is no active session
	 */
	public void logoutUser() throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new ForbiddenOperationException("Logging out with no active session is forbidden!");
		}
		sessionHelper.setAuthentication(null);
	}

}