package com.aggelowe.techquiry.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.UserLogin;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link UserLoginService} class provides methods for managing user login
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UserLoginService {

	/**
	 * The object responsible for handling the data access for {@link UserLogin}
	 * objects.
	 */
	private final UserLoginDao userLoginDao;

	/**
	 * This method retrieves and returns the total count of user logins.
	 *
	 * @return The total number of user logins
	 * @throws InternalErrorException If an internal error occurs while retrieving
	 *                                the count
	 */
	public int getLoginCount() throws ServiceException {
		log.debug("Getting user login count");
		try {
			return userLoginDao.count();
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the user login count!", exception);
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
		log.debug("Getting user login range (count=%s, page=%s)".formatted(count, page));
		if (count < 0 || page < 0) {
			throw new InvalidRequestException("The given count/page must be larger than 0!");
		}
		List<UserLogin> range;
		try {
			range = userLoginDao.range(count, count * page);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the user login!", exception);
		}
		return range;
	}

	/**
	 * This method returns the user login with the given user id.
	 *
	 * @param userId The user id
	 * @return The user login with the given id
	 * @throws EntityNotFoundException If the requested user does not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the user
	 */
	public UserLogin getLoginByUserId(int userId) throws ServiceException {
		log.debug("Getting user login (userId=%s)".formatted(userId));
		UserLogin login;
		try {
			login = userLoginDao.select(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the user login!", exception);
		}
		if (login == null) {
			throw new EntityNotFoundException("The given user id does not have a corresponding user login!");
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
	public UserLogin getLoginByUsername(String username) throws ServiceException {
		log.debug("Getting user login (username=%s)".formatted(username));
		UserLogin login;
		try {
			login = userLoginDao.selectFromUsername(username);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the user login!", exception);
		}
		if (login == null) {
			throw new EntityNotFoundException("The given username does not have a corresponding user login!");
		}
		return login;
	}

}
