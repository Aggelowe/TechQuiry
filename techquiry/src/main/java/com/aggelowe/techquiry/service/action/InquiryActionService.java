package com.aggelowe.techquiry.service.action;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.Inquiry;
import com.aggelowe.techquiry.entity.UserLogin;
import com.aggelowe.techquiry.service.InquiryService;
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
 * The {@link InquiryActionService} class is a component of
 * {@link InquiryService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class InquiryActionService {

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
	private final SessionHelper sessionHelper;

	/**
	 * This method inserts the given {@link Inquiry} object in the database. The
	 * inquiry id and user id are automatically selected and are not carried over to
	 * the database.
	 * 
	 * @param inquiry The inquiry object to create
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given user id does not
	 *                                        correspond to a user login
	 * @throws InvalidRequestException        If the given title or content are
	 *                                        empty
	 * @throws InternalErrorException         If an internal error occurs while
	 *                                        creating the user data
	 * 
	 */
	public int createInquiry(Inquiry inquiry) throws ServiceException {
		log.debug("Creating inquiry (inquiry=%s)".formatted(inquiry));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("The requested inquiry creation is unauthorized!");
		}
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		if (title.isEmpty() || content.isEmpty()) {
			throw new InvalidRequestException("The given title and content name must not be empty!");
		}
		Inquiry copy = inquiry.toBuilder().userId(current.getUserId()).build();
		try {
			return inquiryDao.insert(copy);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the inquiry!", exception);
		}
	}

	/**
	 * This method deletes the inquiry with the specified inquiry id.
	 *
	 * @param inquiryId The inquiry id
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        given user id
	 * @throws EntityNotFoundException        If the requested inquiry does not
	 *                                        exist
	 * @throws InternalErrorException         If an internal error occurred while
	 *                                        deleting the inquiry
	 */
	public void deleteInquiry(int inquiryId) throws ServiceException {
		log.debug("Deleting inquiry (inquiryId=%s)".formatted(inquiryId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("The requested inquiry creation is unauthorized!");
		}
		try {
			Inquiry inquiry = inquiryDao.select(inquiryId);
			if (inquiry == null) {
				throw new EntityNotFoundException("The requested inquiry does not exist!");
			}
			if (current.getUserId() != inquiry.getUserId()) {
				throw new ForbiddenOperationException("The requested inquiry deletion is forbidden!");
			}
			inquiryDao.delete(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while deleting the inquiry!", exception);
		}
	}

	/**
	 * This method updates an existing inquiry with the data from the given
	 * {@link Inquiry} object. The user id is automatically selected and is not
	 * carried over to the database.
	 * 
	 * @param inquiry The inquiry object
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        user id contained in the inquiry
	 *                                        contained in the database
	 * @throws EntityNotFoundException        If the given id does not correspond to
	 *                                        an inquiry id
	 * @throws InvalidRequestException        If the given title or content are
	 *                                        empty
	 * @throws InternalErrorException         If an internal error occurred while
	 *                                        updating the inquiry
	 */
	public void updateInquiry(Inquiry inquiry) throws ServiceException {
		log.debug("Updating inquiry (inquiry=%s)".formatted(inquiry));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("The requested inquiry update is unauthorized!");
		}
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		if (title.isEmpty() || content.isEmpty()) {
			throw new InvalidRequestException("The given title and content name must not be empty!");
		}
		try {
			Inquiry previous = inquiryDao.select(inquiry.getInquiryId());
			if (previous == null) {
				throw new EntityNotFoundException("The requested inquiry does not exist!");
			}
			if (current.getUserId() != previous.getUserId()) {
				throw new ForbiddenOperationException("The requested inquiry update is forbidden!");
			}
			Inquiry copy = inquiry.toBuilder().userId(current.getUserId()).build();
			inquiryDao.update(copy);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the inquiry!", exception);
		}
	}

	/**
	 * This method returns the list of inquiries with the given user id. If the
	 * current user does not have the user id, the list will be limited to
	 * non-anonymous inquiries.
	 *
	 * @param userId The user id
	 * @return The inquiries with the given user id
	 * @throws EntityNotFoundException If the given user id does not correspond to a
	 *                                 user login
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the inquiry
	 */
	public List<Inquiry> getInquiryListByUserId(int userId) throws ServiceException {
		log.debug("Getting inquiry list (userId=%s)".formatted(userId));
		List<Inquiry> inquiries;
		try {
			UserLogin userLogin = userLoginDao.select(userId);
			if (userLogin == null) {
				throw new EntityNotFoundException("The given user id does not have a corresponding login!");
			}
			Authentication current = sessionHelper.getAuthentication();
			if (current == null || current.getUserId() != userId) {
				inquiries = inquiryDao.selectFromUserIdNonAnonymous(userId);
			} else {
				inquiries = inquiryDao.selectFromUserId(userId);
			}
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the inquiry!", exception);
		}
		return inquiries;
	}

}
