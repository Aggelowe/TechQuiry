package net.techquiry.app.service.action;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.database.dao.ResponseDao;
import net.techquiry.app.database.dao.UpvoteDao;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.Response;
import net.techquiry.app.entity.Upvote;
import net.techquiry.app.service.UpvoteService;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.InvalidRequestException;
import net.techquiry.app.service.exception.ServiceException;
import net.techquiry.app.service.exception.UnauthorizedOperationException;
import net.techquiry.app.service.session.Authentication;
import net.techquiry.app.service.session.SessionHelper;

/**
 * The {@link UpvoteActionService} class is a component of {@link UpvoteService}
 * whose methods provide different functionality for different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UpvoteActionService {

	/**
	 * The object responsible for handling the data access for {@link Upvote}
	 * objects.
	 */
	private final UpvoteDao upvoteDao;

	/**
	 * The object responsible for handling the data access for {@link Response}
	 * objects.
	 */
	private final ResponseDao responseDao;

	/**
	 * The {@link SessionHelper} containing the information of the user currently
	 * acting
	 */
	private final SessionHelper sessionHelper;

	/**
	 * This method returns whether the user that is currently logged in is upvoting
	 * the response with the given response id.
	 * 
	 * @param responseId The response id of the response to check
	 * @return Whether the logged in user is upvoting the given response
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given response id does not
	 *                                        correspond to a responses
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        checking the upvote
	 */
	public boolean checkUpvote(int responseId) throws ServiceException {
		log.debug("Checking upvote (responseId=%s)".formatted(responseId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Checking upvotes requires an active session!");
		}
		Upvote upvote = new Upvote(responseId, current.getUserId());
		try {
			Response response = responseDao.select(responseId);
			if (response == null) {
				throw new EntityNotFoundException("The given response id does not have a corresponding response!");
			}
			return upvoteDao.check(upvote);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while checking the upvote!", exception);
		}
	}

	/**
	 * This method inserts the {@link Upvote} object in the database with the given
	 * response id and the user id of the current user session.
	 *
	 * @param responseId The responseId of the response to upvote
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given response id does not
	 *                                        correspond to a response
	 * @throws InvalidRequestException        If the given upvote already exists
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        creating the upvote
	 */
	public void createUpvote(int responseId) throws ServiceException {
		log.debug("Creating upvote (responseId=%s)".formatted(responseId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Creating upvotes requires an active session!");
		}
		try {
			Response response = responseDao.select(responseId);
			if (response == null) {
				throw new EntityNotFoundException("The given response id does not have a corresponding response!");
			}
			Upvote upvote = new Upvote(responseId, current.getUserId());
			if (upvoteDao.check(upvote)) {
				throw new InvalidRequestException("An upvote with the given information already exists!");
			}
			upvoteDao.insert(upvote);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while creating the upvote!", exception);
		}
	}

	/**
	 * This method deletes the {@link Upvote} object in the database with the given
	 * response id and the user id of the current user session.
	 *
	 * @param responseId The responseId of the response to stop upvoting
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given response id does not
	 *                                        correspond to a response
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        deleting the upvote
	 */
	public void deleteUpvote(int responseId) throws ServiceException {
		log.debug("Deleting upvote (responseId=%s)".formatted(responseId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Deleting upvotes requires an active session!");
		}
		Upvote upvote = new Upvote(responseId, current.getUserId());
		try {
			if (!upvoteDao.check(upvote)) {
				throw new EntityNotFoundException("The given upvote information does not have a corresponding upvote!");
			}
			upvoteDao.delete(upvote);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while deleting the upvote!", exception);
		}
	}

}
