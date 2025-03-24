package net.techquiry.app.service.action;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.database.dao.UserDataDao;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.UserData;
import net.techquiry.app.service.UserDataService;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.ForbiddenOperationException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.InvalidRequestException;
import net.techquiry.app.service.exception.ServiceException;
import net.techquiry.app.service.exception.UnauthorizedOperationException;
import net.techquiry.app.service.session.Authentication;
import net.techquiry.app.service.session.SessionHelper;

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
	 *                                        blank or if the given user id is not
	 *                                        available
	 * @throws InternalErrorException         If a database error occurs while
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
	 *                                        given user id
	 * @throws EntityNotFoundException        If the given id does not correspond to
	 *                                        user data
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        deleting the user data
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
	 *                                        given user id
	 * @throws EntityNotFoundException        If the given id does not correspond to
	 *                                        user data
	 * @throws InvalidRequestException        If the given first or last name are
	 *                                        blank
	 * @throws InternalErrorException         If a database error occurred while
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
