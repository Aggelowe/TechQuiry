package com.aggelowe.techquiry.service.action;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.dao.ResponseDao;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.Inquiry;
import com.aggelowe.techquiry.entity.Response;
import com.aggelowe.techquiry.service.ResponseService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.exception.UnauthorizedOperationException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link ResponseActionService} class is a component of
 * {@link ResponseService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
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
	 * The {@link SessionHelper} containing the information of the user currently
	 * acting
	 */
	private final SessionHelper sessionHelper;

	/**
	 * This method inserts the given {@link Response} object in the database. The
	 * response id and user id are automatically selected and are not carried over
	 * to the database.
	 *
	 * @param response The response object to create
	 * @return The response id of the created {@link Response}
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given inquiry id does not
	 *                                        correspond to a inquiry
	 * @throws InvalidRequestException        If the given content is blank
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        creating the response
	 * 
	 */
	public int createResponse(Response response) throws ServiceException {
		log.debug("Creating response (response=%s)".formatted(response));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Creating responses requires an active session!");
		}
		String content = response.getContent();
		if (content.isBlank()) {
			throw new InvalidRequestException("The given content must not be blank!");
		}
		try {
			Inquiry inquiry = inquiryDao.select(response.getInquiryId());
			if (inquiry == null) {
				throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
			}
			Response copy = response.toBuilder().userId(current.getUserId()).build();
			return responseDao.insert(copy);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while creating the response!", exception);
		}
	}

	/**
	 * This method deletes the response with the specified response id.
	 *
	 * @param responseId The response id
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        user id of the response in the
	 *                                        database
	 * @throws EntityNotFoundException        If the given response id does not
	 *                                        correspond to a response
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        deleting the response
	 */
	public void deleteResponse(int responseId) throws ServiceException {
		log.debug("Deleting response (responseId=%s)".formatted(responseId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Deleting responses requires an active session!");
		}
		try {
			Response response = responseDao.select(responseId);
			if (response == null) {
				throw new EntityNotFoundException("The given response id does not have a corresponding response!");
			}
			if (current.getUserId() != response.getUserId()) {
				throw new ForbiddenOperationException("The requested response deletion is forbidden!");
			}
			responseDao.delete(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while deleting the response!", exception);
		}
	}

	/**
	 * This method updates an existing response with the data from the given
	 * {@link Response} object. The inquiry id and user id are automatically
	 * selected and is not carried over to the database.
	 * 
	 * @param response The response object
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        user id of the response in the
	 *                                        database
	 * @throws EntityNotFoundException        If the given inquiry id do not
	 *                                        correspond to an inquiry
	 * @throws InvalidRequestException        If the given content is blank
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        updating the response
	 */
	public void updateResponse(Response response) throws ServiceException {
		log.debug("Updating response (response=%s)".formatted(response));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Updating responses requires an active session!");
		}
		String content = response.getContent();
		if (content.isBlank()) {
			throw new InvalidRequestException("The given content must not be empty!");
		}
		try {
			Response previous = responseDao.select(response.getResponseId());
			if (previous == null) {
				throw new EntityNotFoundException("The given response id does not have a corresponding response!");
			}
			if (current.getUserId() != previous.getUserId()) {
				throw new ForbiddenOperationException("The requested response update is forbidden!");
			}
			Response copy = response.toBuilder().userId(current.getUserId()).inquiryId(previous.getResponseId()).build();
			responseDao.update(copy);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while creating the inquiry!", exception);
		}
	}

}
