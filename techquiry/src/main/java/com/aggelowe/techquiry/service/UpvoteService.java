package com.aggelowe.techquiry.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.ResponseDao;
import com.aggelowe.techquiry.database.dao.UpvoteDao;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.Response;
import com.aggelowe.techquiry.entity.Upvote;
import com.aggelowe.techquiry.entity.UserLogin;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link UpvoteService} class provides methods for managing upvote
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UpvoteService {

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
	 * This method retrieves and returns the total count of upvotes to the response
	 * with the given response id.
	 *
	 * @param responseId The id of the response
	 * @return The total number of upvotes of the response
	 * @throws EntityNotFoundException If the requested response does not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the count
	 */
	public int getUpvoteCountByResponseId(int responseId) throws ServiceException {
		log.debug("Getting upvote count (responseId=%s)".formatted(responseId));
		Response response;
		try {
			response = responseDao.select(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the response!", exception);
		}
		if (response == null) {
			throw new EntityNotFoundException("The requested response does not exist!");
		}
		try {
			return upvoteDao.countFromResponseId(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the upvote count!", exception);
		}
	}

	/**
	 * This method returns the list of user logins who have upvoted the response
	 * with the given response id.
	 *
	 * @param responseId The id of the response
	 * @return The upvoter user logins
	 * @throws EntityNotFoundException If the given response id does not correspond
	 *                                 to an response
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the upvote entries
	 */
	public List<UserLogin> getUpvoteUserLoginListByResponseId(int responseId) throws ServiceException {
		log.debug("Getting upvoter user login list (responseId=%s)".formatted(responseId));
		Response response;
		try {
			response = responseDao.select(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the response!", exception);
		}
		if (response == null) {
			throw new EntityNotFoundException("The requested response does not exist!");
		}
		try {
			return upvoteDao.selectFromResponseId(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the upvoter user logins!", exception);
		}
	}

	/**
	 * This method returns the list of responses who have been upvoted by the user
	 * login with the given user id.
	 *
	 * @param userId The id of the user login
	 * @return The upvoted responses
	 * @throws EntityNotFoundException If the given user id does not correspond to a
	 *                                 user login
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the upvote entries
	 */
	public List<Response> getUpvotedResponseListByUserId(int userId) throws ServiceException {
		log.debug("Getting upvoted response list (userId=%s)".formatted(userId));
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
			return upvoteDao.selectFromUserId(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the upvoted responses!", exception);
		}
	}

}
