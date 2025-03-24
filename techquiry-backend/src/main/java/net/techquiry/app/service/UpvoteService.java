package net.techquiry.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.database.dao.ResponseDao;
import net.techquiry.app.database.dao.UpvoteDao;
import net.techquiry.app.database.dao.UserLoginDao;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.Response;
import net.techquiry.app.entity.Upvote;
import net.techquiry.app.entity.UserLogin;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.ServiceException;

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
	 * This method returns the total count of upvotes to the response with the given
	 * response id.
	 *
	 * @param responseId The response id
	 * @return The total number of upvotes of the response
	 * @throws EntityNotFoundException If the given response id does not correspond
	 *                                 to a response
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the count
	 */
	public int getUpvoteCountByResponseId(int responseId) throws ServiceException {
		log.debug("Getting upvote count (responseId=%s)".formatted(responseId));
		Response response;
		try {
			response = responseDao.select(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the response!", exception);
		}
		if (response == null) {
			throw new EntityNotFoundException("The given response id does not have a corresponding response!");
		}
		try {
			return upvoteDao.countFromResponseId(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the upvote count!", exception);
		}
	}

	/**
	 * This method returns the list of user logins who have upvoted the response
	 * with the given response id.
	 *
	 * @param responseId The response id
	 * @return The upvoter user logins
	 * @throws EntityNotFoundException If the given response id does not correspond
	 *                                 to a response
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the upvote entries
	 */
	public List<UserLogin> getUpvoteUserLoginListByResponseId(int responseId) throws ServiceException {
		log.debug("Getting upvoter user login list (responseId=%s)".formatted(responseId));
		Response response;
		try {
			response = responseDao.select(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the response!", exception);
		}
		if (response == null) {
			throw new EntityNotFoundException("The given response id does not have a corresponding response!");
		}
		try {
			return upvoteDao.selectFromResponseId(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the upvote user logins!", exception);
		}
	}

	/**
	 * This method returns the list of responses who have been upvoted by the user
	 * login with the given user id.
	 *
	 * @param userId The user id
	 * @return The upvoted responses
	 * @throws EntityNotFoundException If the given user id does not correspond to a
	 *                                 user login
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the upvote entries
	 */
	public List<Response> getUpvotedResponseListByUserId(int userId) throws ServiceException {
		log.debug("Getting upvoted response list (userId=%s)".formatted(userId));
		UserLogin userLogin;
		try {
			userLogin = userLoginDao.select(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the user login!", exception);
		}
		if (userLogin == null) {
			throw new EntityNotFoundException("The given user id does not have a corresponding user login!");
		}
		try {
			return upvoteDao.selectFromUserId(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the upvoted responses!", exception);
		}
	}

}
