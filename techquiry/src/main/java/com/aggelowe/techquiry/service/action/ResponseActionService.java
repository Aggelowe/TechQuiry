package com.aggelowe.techquiry.service.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.dao.ResponseDao;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.entity.Response;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.ResponseService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

/**
 * The {@link ResponseActionService} class is a component of
 * {@link ResponseService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
public class ResponseActionService {

	/**
	 * The object responsible for handling the data access for {@link Response}
	 * objects.
	 */
	private final ResponseDao responseDao;

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
	 * This constructor constructs a new {@link ResponseActionService} instance that
	 * is handling the personalized response operations of the application.
	 * 
	 * @param responseDao  The response data access object
	 * @param inquiryDao   The inquiry data access object
	 * @param userLoginDao The user login data access object
	 */
	@Autowired
	public ResponseActionService(ResponseDao responseDao, InquiryDao inquiryDao, UserLoginDao userLoginDao) {
		this.responseDao = responseDao;
		this.inquiryDao = inquiryDao;
		this.userLoginDao = userLoginDao;
	}

	/**
	 * This method inserts the given {@link Response} object in the database
	 *
	 * @param response The response object to create
	 * @return The response id of the created {@link Response}
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given user id
	 * @throws EntityNotFoundException     If the given user id or inquiry id do not
	 *                                     correspond to a user login or inquiry
	 *                                     respectively
	 * @throws InvalidRequestException     If the given content is empty
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the response
	 * 
	 */
	public int createResponse(Response response) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != response.getUserId()) {
			throw new ForbiddenOperationException("The requested response creation is forbidden!");
		}
		String content = response.getContent();
		if (content.isEmpty()) {
			throw new InvalidRequestException("The given content name must not be empty!");
		}
		try {
			UserLogin userLogin = userLoginDao.select(response.getUserId());
			if (userLogin == null) {
				throw new EntityNotFoundException("The given user id does not have a corresponding login!");
			}
			Inquiry inquiry = inquiryDao.select(response.getInquiryId());
			if (inquiry == null) {
				throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
			}
			return responseDao.insert(response);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the response!", exception);
		}
	}

	/**
	 * This method deletes the response with the specified response id.
	 *
	 * @param responseId The response id
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given user id
	 * @throws EntityNotFoundException     If the requested response does not exist
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     deleting the response
	 */
	public void deleteResponse(int responseId) throws ServiceException {
		try {
			Response response = responseDao.select(responseId);
			if (response == null) {
				throw new EntityNotFoundException("The requested response does not exist!");
			}
			Authentication current = sessionHelper.getAuthentication();
			if (current == null || current.getUserId() != response.getUserId()) {
				throw new ForbiddenOperationException("The requested response deletion is forbidden!");
			}
			responseDao.delete(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while deleting the response!", exception);
		}
	}

	/**
	 * This method updates an existing response with the data from the given
	 * {@link Response} object.
	 * 
	 * @param response The response object
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     user id contained in the given response
	 *                                     or the response contained in the database
	 * @throws EntityNotFoundException     If the given user id, inquiry id or
	 *                                     response id do not correspond to a user
	 *                                     login, inquiry or response respectively
	 * @throws InvalidRequestException     If the given content is empty
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     updating the response
	 */
	public void updateResponse(Response response) throws ServiceException {
		Authentication current = sessionHelper.getAuthentication();
		if (current == null || current.getUserId() != response.getUserId()) {
			throw new ForbiddenOperationException("The requested response update is forbidden!");
		}
		String content = response.getContent();
		if (content.isEmpty()) {
			throw new InvalidRequestException("The given content name must not be empty!");
		}
		try {
			Response previous = responseDao.select(response.getId());
			if (previous == null) {
				throw new EntityNotFoundException("The requested response does not exist!");
			}
			if (current.getUserId() != previous.getUserId()) {
				throw new ForbiddenOperationException("The requested response update is forbidden!");
			}
			UserLogin userLogin = userLoginDao.select(response.getUserId());
			if (userLogin == null) {
				throw new EntityNotFoundException("The given user id does not have a corresponding login!");
			}
			Inquiry inquiry = inquiryDao.select(response.getInquiryId());
			if (inquiry == null) {
				throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
			}
			responseDao.update(response);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the inquiry!", exception);
		}
	}

}
