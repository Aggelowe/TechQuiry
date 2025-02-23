package com.aggelowe.techquiry.service.action;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.ResponseDao;
import com.aggelowe.techquiry.database.dao.UpvoteDao;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.Response;
import com.aggelowe.techquiry.entity.Upvote;
import com.aggelowe.techquiry.service.UpvoteService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.exception.UnauthorizedOperationException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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
	 * @throws InternalErrorException         If an internal error occurs while
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
	 * @throws InternalErrorException         If an internal error occurs while
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
	 * @throws EntityNotFoundException        If the requested upvote does not exist
	 * @throws InternalErrorException         If an internal error occurred while
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
