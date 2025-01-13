package com.aggelowe.techquiry.service.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.dao.ObserverDao;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.entity.Observer;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.ObserverService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

/**
 * The {@link ObserverActionService} class is a component of
 * {@link ObserverService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
public class ObserverActionService {

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
	 * The {@link SessionHelper} containing the information of the user currently
	 * acting
	 */
	@Autowired
	private SessionHelper sessionHelper;

	/**
	 * This constructor constructs a new {@link ObserverActionService} instance that
	 * is handling the personalized observer operations of the application.
	 * 
	 * @param observerDao  The observer data access object
	 * @param inquiryDao   The inquiry data access object
	 * @param userLoginDao The user login data access object
	 */
	@Autowired
	public ObserverActionService(ObserverDao observerDao, InquiryDao inquiryDao, UserLoginDao userLoginDao) {
		this.observerDao = observerDao;
		this.inquiryDao = inquiryDao;
		this.userLoginDao = userLoginDao;
	}

	/**
	 * This method inserts the given {@link Observer} object in the database
	 *
	 * @param response The observer object to create
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given user id
	 * @throws EntityNotFoundException     If the given user id or inquiry id do not
	 *                                     correspond to a user login or inquiry
	 *                                     respectively
	 * @throws InvalidRequestException     If the given observer already exists
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the observer
	 */
	public void createObserver(Observer observer) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != observer.getUserId()) {
			throw new ForbiddenOperationException("The requested response update is forbidden!");
		}
		try {
			UserLogin userLogin = userLoginDao.select(observer.getUserId());
			if (userLogin == null) {
				throw new EntityNotFoundException("The given user id does not have a corresponding login!");
			}
			Inquiry inquiry = inquiryDao.select(observer.getInquiryId());
			if (inquiry == null) {
				throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
			}
			if (observerDao.check(observer)) {
				throw new InvalidRequestException("The given observer already exists!");
			}
			observerDao.insert(observer);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the inquiry!", exception);
		}
	}

	/**
	 * This method deletes the observer with the specified information.
	 *
	 * @param observer The observer information
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given user id
	 * @throws EntityNotFoundException     If the requested observer does not exist
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     deleting the response
	 */
	public void deleteObserver(Observer observer) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != observer.getUserId()) {
			throw new ForbiddenOperationException("The requested response update is forbidden!");
		}
		try {
			if (!observerDao.check(observer)) {
				throw new InvalidRequestException("The requested observer does not exist!");
			}
			observerDao.delete(observer);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while deleting the observer!", exception);
		}
	}

}
