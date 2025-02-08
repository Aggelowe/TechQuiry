package com.aggelowe.techquiry.service.action;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.dao.ObserverDao;
import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.entity.Observer;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.ObserverService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

import lombok.RequiredArgsConstructor;

/**
 * The {@link ObserverActionService} class is a component of
 * {@link ObserverService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
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
	 * The {@link SessionHelper} containing the information of the user currently
	 * acting
	 */
	private final SessionHelper sessionHelper;

	/**
	 * This method inserts the {@link Observer} object in the database with the
	 * given inquiry id and the user id of the current user session.
	 *
	 * @param inquiryId The inquiry id of the inquiry to observe
	 * @throws ForbiddenOperationException If the current user is not logged in
	 * @throws EntityNotFoundException     If the given inquiry id does not
	 *                                     correspond to an inquiry
	 * @throws InvalidRequestException     If the given observer already exists
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the observer
	 */
	public void createObserver(int inquiryId) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new ForbiddenOperationException("The requested observer creation is forbidden!");
		}
		try {
			Inquiry inquiry = inquiryDao.select(inquiryId);
			if (inquiry == null) {
				throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
			}
			Observer observer = new Observer(inquiryId, current.getUserId());
			if (observerDao.check(observer)) {
				throw new InvalidRequestException("The given observer already exists!");
			}
			observerDao.insert(observer);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the observer!", exception);
		}
	}

	/**
	 * This method deletes the {@link Observer} object in the database with the
	 * given inquiry id and the user id of the current user session.
	 *
	 * @param inquiryId The inquiry id of the inquiry to stop observing
	 * @throws ForbiddenOperationException If the current user is not logged in
	 * @throws EntityNotFoundException     If the requested observer does not exist
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     deleting the observer
	 */
	public void deleteObserver(int inquiryId) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new ForbiddenOperationException("The requested observer deletion is forbidden!");
		}
		Observer observer = new Observer(inquiryId, current.getUserId());
		try {
			if (!observerDao.check(observer)) {
				throw new EntityNotFoundException("The requested observer does not exist!");
			}
			observerDao.delete(observer);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while deleting the observer!", exception);
		}
	}

}
