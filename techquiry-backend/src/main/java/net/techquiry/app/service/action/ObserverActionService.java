package net.techquiry.app.service.action;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.techquiry.app.database.dao.InquiryDao;
import net.techquiry.app.database.dao.ObserverDao;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.Inquiry;
import net.techquiry.app.entity.Observer;
import net.techquiry.app.service.ObserverService;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.InvalidRequestException;
import net.techquiry.app.service.exception.ServiceException;
import net.techquiry.app.service.exception.UnauthorizedOperationException;
import net.techquiry.app.service.session.Authentication;
import net.techquiry.app.service.session.SessionHelper;

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
@Log4j2
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
	 * This method returns whether the user that is currently logged in is observing
	 * the inquiry with the given inquiry id.
	 * 
	 * @param inquiryId The inquiry id of the inquiry to check
	 * @return Whether the logged in user is observing the given inquiry
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given inquiry id does not
	 *                                        correspond to an inquiry
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        checking the observer
	 */
	public boolean checkObserver(int inquiryId) throws ServiceException {
		log.debug("Checking observer (inquiryId=%s)".formatted(inquiryId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Checking observers requires an active session!");
		}
		Observer observer = new Observer(inquiryId, current.getUserId());
		try {
			Inquiry inquiry = inquiryDao.select(inquiryId);
			if (inquiry == null) {
				throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
			}
			return observerDao.check(observer);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while checking the observer!", exception);
		}
	}

	/**
	 * This method inserts the {@link Observer} object in the database with the
	 * given inquiry id and the user id of the current user session.
	 *
	 * @param inquiryId The inquiry id of the inquiry to observe
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given inquiry id does not
	 *                                        correspond to an inquiry
	 * @throws InvalidRequestException        If the given observer already exists
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        creating the observer
	 */
	public void createObserver(int inquiryId) throws ServiceException {
		log.debug("Creating observer (inquiryId=%s)".formatted(inquiryId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Creating observers requires an active session!");
		}
		try {
			Inquiry inquiry = inquiryDao.select(inquiryId);
			if (inquiry == null) {
				throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
			}
			Observer observer = new Observer(inquiryId, current.getUserId());
			if (observerDao.check(observer)) {
				throw new InvalidRequestException("An observer with the given information already exists!");
			}
			observerDao.insert(observer);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while creating the observer!", exception);
		}
	}

	/**
	 * This method deletes the {@link Observer} object in the database with the
	 * given inquiry id and the user id of the current user session.
	 *
	 * @param inquiryId The inquiry id of the inquiry to stop observing
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the requested observer does not
	 *                                        exist
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        deleting the observer
	 */
	public void deleteObserver(int inquiryId) throws ServiceException {
		log.debug("Deleting observer (inquiryId=%s)".formatted(inquiryId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Deleting observers requires an active session!");
		}
		Observer observer = new Observer(inquiryId, current.getUserId());
		try {
			if (!observerDao.check(observer)) {
				throw new EntityNotFoundException("The given observer information does not have a corresponding observer!");
			}
			observerDao.delete(observer);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while deleting the observer!", exception);
		}
	}

}
