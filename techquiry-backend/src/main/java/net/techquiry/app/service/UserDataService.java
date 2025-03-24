package net.techquiry.app.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.database.dao.UserDataDao;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.UserData;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.ServiceException;

/**
 * The {@link UserDataService} class provides methods for managing user data
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UserDataService {

	/**
	 * The object responsible for handling the data access for {@link UserData}
	 * objects.
	 */
	private final UserDataDao userDataDao;

	/**
	 * This method returns the user data with the given user id.
	 *
	 * @param userId The user id
	 * @return The user data with the given id
	 * @throws EntityNotFoundException If the given id does not correspond to user
	 *                                 data
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the user data
	 */
	public UserData getDataByUserId(int userId) throws ServiceException {
		log.debug("Getting user data (userId=%s)".formatted(userId));
		UserData data;
		try {
			data = userDataDao.select(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the user data!", exception);
		}
		if (data == null) {
			throw new EntityNotFoundException("The given user id does not have corresponding user data!");
		}
		return data;
	}

}
