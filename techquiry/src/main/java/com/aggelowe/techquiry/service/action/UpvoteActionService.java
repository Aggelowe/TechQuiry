package com.aggelowe.techquiry.service.action;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.ResponseDao;
import com.aggelowe.techquiry.database.dao.UpvoteDao;
import com.aggelowe.techquiry.database.entity.Response;
import com.aggelowe.techquiry.database.entity.Upvote;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.UpvoteService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

import lombok.RequiredArgsConstructor;

/**
 * The {@link UpvoteActionService} class is a component of {@link UpvoteService}
 * whose methods provide different functionality for different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
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
	 * This method inserts the {@link Upvote} object in the database with the given
	 * response id and the user id of the current user session.
	 *
	 * @param responseId The responseId of the response to upvote
	 * @throws ForbiddenOperationException If the current user is not logged in
	 * @throws EntityNotFoundException     If the given response id does not
	 *                                     correspond to a response
	 * @throws InvalidRequestException     If the given upvote already exists
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the upvote
	 */
	public void createUpvote(int responseId) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new ForbiddenOperationException("The requested upvote creation is forbidden!");
		}
		try {
			Response response = responseDao.select(responseId);
			if (response == null) {
				throw new EntityNotFoundException("The given response id does not have a corresponding response!");
			}
			Upvote upvote = new Upvote(responseId, current.getUserId());
			if (upvoteDao.check(upvote)) {
				throw new InvalidRequestException("The given upvote already exists!");
			}
			upvoteDao.insert(upvote);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the upvote!", exception);
		}
	}

	/**
	 * This method deletes the {@link Upvote} object in the database with the given
	 * response id and the user id of the current user session.
	 *
	 * @param responseId The responseId of the response to stop upvoting
	 * @throws ForbiddenOperationException If the current user is not logged in
	 * @throws EntityNotFoundException     If the requested upvote does not exist
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     deleting the upvote
	 */
	public void deleteUpvote(int responseId) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new ForbiddenOperationException("The requested upvote deletion is forbidden!");
		}
		Upvote upvote = new Upvote(responseId, current.getUserId());
		try {
			if (!upvoteDao.check(upvote)) {
				throw new EntityNotFoundException("The requested upvote does not exist!");
			}
			upvoteDao.delete(upvote);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while deleting the upvote!", exception);
		}
	}

}
