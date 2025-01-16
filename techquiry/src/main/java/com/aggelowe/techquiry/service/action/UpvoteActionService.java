package com.aggelowe.techquiry.service.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.ResponseDao;
import com.aggelowe.techquiry.database.dao.UpvoteDao;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entity.Response;
import com.aggelowe.techquiry.database.entity.Upvote;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.UpvoteService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

/**
 * The {@link UpvoteActionService} class is a component of {@link UpvoteService}
 * whose methods provide different functionality for different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
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
	 * is handling the personalized upvote operations of the application.
	 * 
	 * @param upvoteDao    The upvote data access object
	 * @param responseDao  The response data access object
	 * @param userLoginDao The user login data access object
	 */
	@Autowired
	public UpvoteActionService(UpvoteDao upvoteDao, ResponseDao responseDao, UserLoginDao userLoginDao) {
		this.upvoteDao = upvoteDao;
		this.responseDao = responseDao;
		this.userLoginDao = userLoginDao;
	}

	/**
	 * This method inserts the given {@link Upvote} object in the database
	 *
	 * @param upvote The upvote object to create
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given user id
	 * @throws EntityNotFoundException     If the given user id or response id do
	 *                                     not correspond to a user login or
	 *                                     response respectively
	 * @throws InvalidRequestException     If the given upvote already exists
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the upvote
	 */
	public void createUpvote(Upvote upvote) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != upvote.getUserId()) {
			throw new ForbiddenOperationException("The requested upvote creation is forbidden!");
		}
		try {
			UserLogin userLogin = userLoginDao.select(upvote.getUserId());
			if (userLogin == null) {
				throw new EntityNotFoundException("The given user id does not have a corresponding login!");
			}
			Response response = responseDao.select(upvote.getResponseId());
			if (response == null) {
				throw new EntityNotFoundException("The given response id does not have a corresponding response!");
			}
			if (upvoteDao.check(upvote)) {
				throw new InvalidRequestException("The given upvote already exists!");
			}
			upvoteDao.insert(upvote);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the upvote!", exception);
		}
	}

	/**
	 * This method deletes the upvote with the specified information.
	 *
	 * @param upvote The upvote information
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given user id
	 * @throws EntityNotFoundException     If the requested upvote does not exist
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     deleting the upvote
	 */
	public void deleteUpvote(Upvote upvote) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != upvote.getUserId()) {
			throw new ForbiddenOperationException("The requested upvote deletion is forbidden!");
		}
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
