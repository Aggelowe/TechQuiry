package com.aggelowe.techquiry.service;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.UserDataDao;
import com.aggelowe.techquiry.database.entity.UserData;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.ServiceException;

/**
 * The {@link UserDataService} class provides methods for managing user data
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
public class UserDataService {

	/**
	 * The object responsible for handling the data access for {@link UserData}
	 * objects.
	 */
	private final UserDataDao userDataDao;

	/**
	 * This constructor constructs a new {@link UserDataDao} instance that is
	 * handling the user data operations of the application.
	 * 
	 * @param userDataDao The user data data access object
	 */
	public UserDataService(UserDataDao userDataDao) {
		this.userDataDao = userDataDao;
	}

	/**
	 * This method returns the user data with the given user id.
	 *
	 * @param id The user id
	 * @return The user data with the given id
	 * @throws EntityNotFoundException If the requested user data do not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the user data
	 */
	public UserData findDataByUserId(int id) throws ServiceException {
		UserData data;
		try {
			data = userDataDao.select(id);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user data!", exception);
		}
		if (data == null) {
			throw new EntityNotFoundException("The requested user data do not exist!");
		}
		return data;
	}

}
