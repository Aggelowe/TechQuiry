package net.techquiry.app.service.action;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.common.Constants;
import net.techquiry.app.common.Environment;
import net.techquiry.app.common.SecurityUtils;
import net.techquiry.app.database.dao.UserLoginDao;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.UserLogin;
import net.techquiry.app.service.UserLoginService;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.ForbiddenOperationException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.InvalidRequestException;
import net.techquiry.app.service.exception.ServiceException;
import net.techquiry.app.service.exception.UnauthorizedOperationException;
import net.techquiry.app.service.session.Authentication;
import net.techquiry.app.service.session.SessionHelper;

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
@Log4j2
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
	 * @throws ForbiddenOperationException If there is an active session
	 * @throws InvalidRequestException     If the given username does not abide by
	 *                                     the requirements or if the given username
	 *                                     is not available
	 * @throws InternalErrorException      If a database error occurs while creating
	 *                                     the user login
	 * 
	 */
	public int createLogin(UserLogin login) throws ServiceException {
		log.debug("Creating user login (login=%s)".formatted(login));
		Authentication current = sessionHelper.getAuthentication();
		if (current != null) {
			throw new ForbiddenOperationException("Creating users while logged-in is forbidden!");
		}
		String username = login.getUsername();
		Pattern pattern = Pattern.compile(Constants.SECURITY_USERNAME_REGEX.formatted(Environment.SEC_USERNAME_MAX_SIZE - 1));
		if (!pattern.matcher(username).matches()) {
			throw new InvalidRequestException("The given username does not abide by the requirements!");
		}
		int userId;
		try {
			UserLogin userLogin = userLoginDao.selectFromUsername(login.getUsername());
			if (userLogin != null) {
				throw new InvalidRequestException("A user login with the given username already exists!");
			}
			userId = userLoginDao.insert(login);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while creating the user login!", exception);
		}
		Authentication authentication = new Authentication(userId);
		sessionHelper.setAuthentication(authentication);
		return userId;
	}

	/**
	 * This method deletes the user login with the specified user id.
	 *
	 * @param userId The user id
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        given user id
	 * @throws EntityNotFoundException        If the given user id does not
	 *                                        correspond to an user login
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        deleting the user login
	 */
	public void deleteLogin(int userId) throws ServiceException {
		log.debug("Deleting user login (userId=%s)".formatted(userId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Deleting user logins requires an active session!");
		}
		if (current.getUserId() != userId) {
			throw new ForbiddenOperationException("The requested user login deletion is forbidden!");
		}
		try {
			UserLogin login = userLoginDao.select(userId);
			if (login == null) {
				throw new EntityNotFoundException("The given user id does not have corresponding user login!");
			}
			sessionHelper.setAuthentication(null);
			userLoginDao.delete(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while deleting the user login!", exception);
		}
	}

	/**
	 * This method updates an existing user login with the data from the given
	 * {@link UserLogin} object.
	 * 
	 * @param login The user login
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        same id as the one contained in the
	 *                                        given login.
	 * @throws EntityNotFoundException        If the given user id does not
	 *                                        correspond to an user login
	 * @throws InvalidRequestException        If the given username does not abide
	 *                                        by the requirements.
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        updating the user login
	 * 
	 */
	public void updateLogin(UserLogin login) throws ServiceException {
		log.debug("Updating user login (login=%s)".formatted(login));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Updating user logins requires an active session!");
		}
		if (current.getUserId() != login.getUserId()) {
			throw new ForbiddenOperationException("The requested user login update is forbidden!");
		}
		String username = login.getUsername();
		Pattern pattern = Pattern.compile(Constants.SECURITY_USERNAME_REGEX.formatted(Environment.SEC_USERNAME_MAX_SIZE - 1));
		if (!pattern.matcher(username).matches()) {
			throw new InvalidRequestException("The given username does not abide by the requirements!");
		}
		try {
			UserLogin idLogin = userLoginDao.select(login.getUserId());
			if (idLogin == null) {
				throw new EntityNotFoundException("The given user id does not have corresponding user login!");
			}
			UserLogin usernameLogin = userLoginDao.selectFromUsername(login.getUsername());
			if (usernameLogin != null && !usernameLogin.getUserId().equals(login.getUserId())) {
				throw new InvalidRequestException("A user login with the given username already exists!");
			}
			userLoginDao.update(login);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the user!", exception);
		}
	}

	/**
	 * This method returns the currently logged in user's {@link UserLogin}.
	 * 
	 * @return The current user login
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws InternalErrorException         If a database occurs while retrieving
	 *                                        the user login
	 */
	public UserLogin getCurrentLogin() throws ServiceException {
		log.debug("Getting current user login");
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Getting the current user login requires an active session!");
		}
		int userId = current.getUserId();
		UserLogin login;
		try {
			login = userLoginDao.select(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the user!", exception);
		}
		if (login == null) {
			throw new EntityNotFoundException("The current user id does not have corresponding user login!");
		}
		return login;
	}

	/**
	 * Sets the respective {@link Authentication} in the respective user session if
	 * the given password matches with the one contained in the database.
	 * 
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return The current {@link UserLogin}
	 * @throws ForbiddenOperationException    If there is an active session
	 * @throws InvalidRequestException        If the username or password is NULL
	 * @throws UnauthorizedOperationException If the username or password is
	 *                                        incorrect
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        authenticating
	 */
	public UserLogin authenticateUser(String username, String password) throws ServiceException {
		log.debug("Authenticating user (username=%s)".formatted(username));
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
			throw new InternalErrorException("A database error occured while authenticating!", exception);
		}
		if (login == null) {
			throw new UnauthorizedOperationException("The username and/or password is incorrect!");
		}
		byte[] salt = login.getPasswordSalt();
		byte[] hash = login.getPasswordHash();
		if (SecurityUtils.verifyPassword(password, salt, hash)) {
			int userId = login.getUserId();
			Authentication authentication = new Authentication(userId);
			sessionHelper.setAuthentication(authentication);
			return login;
		} else {
			throw new UnauthorizedOperationException("The username and/or password is incorrect!");
		}
	}

	/**
	 * Sets the {@link Authentication} in the respective user session to NULL.
	 * 
	 * @throws UnauthorizedOperationException If there is no active session
	 */
	public void logoutUser() throws ServiceException {
		log.debug("Logging out user");
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Logging out requires an active session!");
		}
		sessionHelper.setAuthentication(null);
	}

}