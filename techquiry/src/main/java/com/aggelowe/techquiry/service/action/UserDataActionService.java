package com.aggelowe.techquiry.service.action;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.UserDataDao;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.UserData;
import com.aggelowe.techquiry.service.UserDataService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.exception.UnauthorizedOperationException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link UserDataActionService} class is a dependency of
 * {@link UserDataService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UserDataActionService {

	/**
	 * The object responsible for handling the data access for {@link UserData}
	 * objects.
	 */
	private final UserDataDao userDataDao;

	/**
	 * The {@link SessionHelper} containing the information of the user currently
	 * acting
	 */
	private final SessionHelper sessionHelper;

	/**
	 * This method inserts the given {@link UserData} object in the database. The
	 * user id is automatically selected.
	 *
	 * @param data The user data object to create
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws InvalidRequestException        If the given first or last name are
	 *                                        empty or if the given id is not
	 *                                        available
	 * @throws InternalErrorException         If an internal error occurs while
	 *                                        creating the user data
	 * 
	 */
	public void createData(UserData data) throws ServiceException {
		log.debug("Creating user data (data=%s)".formatted(data));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Creating user data requires an active session!");
		}
		String firstName = data.getFirstName();
		String lastName = data.getLastName();
		if (firstName.isBlank() || lastName.isBlank()) {
			throw new InvalidRequestException("The given first and last name must not be blank!");
		}
		try {
			UserData userData = userDataDao.select(current.getUserId());
			if (userData != null) {
				throw new InvalidRequestException("User data with the given user id already exist!");
			}
			UserData copy = data.toBuilder().userId(current.getUserId()).build();
			userDataDao.insert(copy);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while creating the user data!", exception);
		}
	}

	/**
	 * This method deletes the user data with the specified user id.
	 *
	 * @param userId The user id
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        given id
	 * @throws EntityNotFoundException        If the requested user data do not
	 *                                        exist
	 * @throws InternalErrorException         If an internal error occurred while
	 *                                        deleting the user
	 */
	public void deleteData(int userId) throws ServiceException {
		log.debug("Deleting user data (userId=%s)".formatted(userId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Deleting user data requires an active session!");
		}
		if (current.getUserId() != userId) {
			throw new ForbiddenOperationException("The requested user data deletion is forbidden!");
		}
		try {
			UserData data = userDataDao.select(userId);
			if (data == null) {
				throw new EntityNotFoundException("The given user id does not have corresponding user data!");
			}
			userDataDao.delete(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while deleting the user data!", exception);
		}
	}

	/**
	 * This method updates an existing user data with the data from the given
	 * {@link UserData} object.
	 * 
	 * @param data The user data
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        given id
	 * @throws EntityNotFoundException        If the given id does not correspond to
	 *                                        a user login id
	 * @throws InvalidRequestException        If the given first or last name are
	 *                                        empty or if the given id is not
	 *                                        available
	 * @throws InternalErrorException         If an internal error occurred while
	 *                                        updating the user data
	 * 
	 */
	public void updateData(UserData data) throws ServiceException {
		log.debug("Updating user data (data=%s)".formatted(data));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Updating user data requires an active session!");
		}
		if (current.getUserId() != data.getUserId()) {
			throw new ForbiddenOperationException("The requested user data update is forbidden!");
		}
		String firstName = data.getFirstName();
		String lastName = data.getLastName();
		if (firstName.isBlank() || lastName.isBlank()) {
			throw new InvalidRequestException("The given first and last name must not be blank!");
		}
		try {
			UserData userData = userDataDao.select(data.getUserId());
			if (userData == null) {
				throw new EntityNotFoundException("The given user id does not have corresponding user data!");
			}
			userDataDao.update(data);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while updating the user data!", exception);
		}
	}

}
