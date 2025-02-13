package com.aggelowe.techquiry.service.action;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.UserDataDao;
import com.aggelowe.techquiry.database.entity.UserData;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.UserDataService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

import lombok.RequiredArgsConstructor;

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
	 * @throws ForbiddenOperationException If the current user is not logged in
	 * @throws InvalidRequestException     If the given first or last name are empty
	 *                                     or if the given id is not available
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the user data
	 * 
	 */
	public void createData(UserData data) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new ForbiddenOperationException("The requested user data creation is forbidden!");
		}
		String firstName = data.getFirstName();
		String lastName = data.getLastName();
		if (firstName.isEmpty() || lastName.isEmpty()) {
			throw new InvalidRequestException("The given first and last name must not be empty!");
		}
		try {
			UserData userData = userDataDao.select(current.getUserId());
			if (userData != null) {
				throw new InvalidRequestException("The given id is not available!");
			}
			UserData copy = data.toBuilder().userId(current.getUserId()).build();
			userDataDao.insert(copy);
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
	 * {@link UserData} object. The user id is automatically selected.
	 * 
	 * @param data The user data
	 * @throws ForbiddenOperationException If the current user is not logged in
	 * @throws EntityNotFoundException     If the current user id does not match
	 *                                     with a user data entry
	 * @throws InvalidRequestException     If the given first or last name are empty
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     updating the user data
	 * 
	 */
	public void updateData(UserData data) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new ForbiddenOperationException("The requested user update is forbidden!");
		}
		String firstName = data.getFirstName();
		String lastName = data.getLastName();
		if (firstName.isEmpty() || lastName.isEmpty()) {
			throw new InvalidRequestException("The given first and last name must not be empty!");
		}
		try {
			UserData userData = userDataDao.select(current.getUserId());
			if (userData == null) {
				throw new EntityNotFoundException("The requested user data do not exist!");
			}
			UserData copy = data.toBuilder().userId(current.getUserId()).build();
			userDataDao.update(copy);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user!", exception);
		}
	}

}
