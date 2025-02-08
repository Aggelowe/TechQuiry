package com.aggelowe.techquiry.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.dao.ObserverDao;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.entity.Observer;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.ServiceException;

import lombok.RequiredArgsConstructor;

/**
 * The {@link ObserverService} class provides methods for managing observer
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
public class ObserverService {

	/**
	 * The object responsible for handling the data access for {@link Observer}
	 * objects.
	 */
	private final ObserverDao observerDao;

	/**
	 * The object responsible for handling the data access for {@link Inquiry}
	 * objects.
	 */
	private final InquiryDao inquiryDao;

	/**
	 * The object responsible for handling the data access for {@link UserLogin}
	 * objects.
	 */
	private final UserLoginDao userLoginDao;

	/**
	 * This method retrieves and returns the total count of observers to the inquiry
	 * with the given inquiry id.
	 *
	 * @param inquiryId The id of the inquiry
	 * @return The total number of observers of the inquiry
	 * @throws EntityNotFoundException If the requested inquiry does not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the count
	 */
	public int getObserverCountByInquiryId(int inquiryId) throws ServiceException {
		Inquiry inquiry;
		try {
			inquiry = inquiryDao.select(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the inquiry!", exception);
		}
		if (inquiry == null) {
			throw new EntityNotFoundException("The requested inquiry does not exist!");
		}
		try {
			return observerDao.countFromInquiryId(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the observer count!", exception);
		}
	}

	/**
	 * This method returns the list of user logins who are observing the inquiry
	 * with the given inquiry id.
	 *
	 * @param inquiryId The id of the inquiry
	 * @return The observer user logins
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the observer entries
	 */
	public List<UserLogin> getObserverUserLoginListByInquiryId(int inquiryId) throws ServiceException {
		Inquiry inquiry;
		try {
			inquiry = inquiryDao.select(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the inquiry!", exception);
		}
		if (inquiry == null) {
			throw new EntityNotFoundException("The requested inquiry does not exist!");
		}
		try {
			return observerDao.selectFromInquiryId(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the observer user logins!", exception);
		}
	}

	/**
	 * This method returns the list of inquiries who are being observed by the user
	 * login with the given user id.
	 *
	 * @param userId The id of the user login
	 * @return The observed inquiries
	 * @throws EntityNotFoundException If the given user id does not correspond to a
	 *                                 user login
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the observer entries
	 */
	public List<Inquiry> getObservedInquiryListByUserId(int userId) throws ServiceException {
		UserLogin userLogin;
		try {
			userLogin = userLoginDao.select(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the user login!", exception);
		}
		if (userLogin == null) {
			throw new EntityNotFoundException("The given user id does not have a corresponding login!");
		}
		try {
			return observerDao.selectFromUserId(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the observed inquiries!", exception);
		}
	}

}
