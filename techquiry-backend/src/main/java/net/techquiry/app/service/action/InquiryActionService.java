package net.techquiry.app.service.action;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.database.dao.InquiryDao;
import net.techquiry.app.database.dao.UserLoginDao;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.Inquiry;
import net.techquiry.app.entity.UserLogin;
import net.techquiry.app.service.InquiryService;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.ForbiddenOperationException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.InvalidRequestException;
import net.techquiry.app.service.exception.ServiceException;
import net.techquiry.app.service.exception.UnauthorizedOperationException;
import net.techquiry.app.service.session.Authentication;
import net.techquiry.app.service.session.SessionHelper;

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
	 * @throws InvalidRequestException        If the given title or content are
	 *                                        blank
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        creating the user data
	 * 
	 */
	public int createInquiry(Inquiry inquiry) throws ServiceException {
		log.debug("Creating inquiry (inquiry=%s)".formatted(inquiry));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Creating inquiries requires an active session!");
		}
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		if (title.isBlank() || content.isBlank()) {
			throw new InvalidRequestException("The given title and content must not be blank!");
		}
		Inquiry copy = inquiry.toBuilder().userId(current.getUserId()).build();
		try {
			return inquiryDao.insert(copy);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while creating the inquiry!", exception);
		}
	}

	/**
	 * This method deletes the inquiry with the specified inquiry id.
	 *
	 * @param inquiryId The inquiry id
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        user id of the inquiry in the database
	 * @throws EntityNotFoundException        If the given inquiry id does not
	 *                                        correspond to an inquiry
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        deleting the inquiry
	 */
	public void deleteInquiry(int inquiryId) throws ServiceException {
		log.debug("Deleting inquiry (inquiryId=%s)".formatted(inquiryId));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Deleting inquiries requires an active session!");
		}
		try {
			Inquiry inquiry = inquiryDao.select(inquiryId);
			if (inquiry == null) {
				throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
			}
			if (current.getUserId() != inquiry.getUserId()) {
				throw new ForbiddenOperationException("The requested inquiry deletion is forbidden!");
			}
			inquiryDao.delete(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while deleting the inquiry!", exception);
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
	 *                                        user id of the inquiry in the database
	 * @throws EntityNotFoundException        If the given inquiry id does not
	 *                                        correspond to an inquiry
	 * @throws InvalidRequestException        If the given title or content are
	 *                                        blank
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        updating the inquiry
	 */
	public void updateInquiry(Inquiry inquiry) throws ServiceException {
		log.debug("Updating inquiry (inquiry=%s)".formatted(inquiry));
		Authentication current = sessionHelper.getAuthentication();
		if (current == null) {
			throw new UnauthorizedOperationException("Updating inquiries requires an active session!");
		}
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		if (title.isBlank() || content.isBlank()) {
			throw new InvalidRequestException("The given title and content must not be blank!");
		}
		try {
			Inquiry previous = inquiryDao.select(inquiry.getInquiryId());
			if (previous == null) {
				throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
			}
			if (current.getUserId() != previous.getUserId()) {
				throw new ForbiddenOperationException("The requested inquiry update is forbidden!");
			}
			Inquiry copy = inquiry.toBuilder().userId(current.getUserId()).build();
			inquiryDao.update(copy);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while creating the inquiry!", exception);
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
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the inquiry
	 */
	public List<Inquiry> getInquiryListByUserId(int userId) throws ServiceException {
		log.debug("Getting inquiry list (userId=%s)".formatted(userId));
		List<Inquiry> inquiries;
		try {
			UserLogin userLogin = userLoginDao.select(userId);
			if (userLogin == null) {
				throw new EntityNotFoundException("The given user id does not have a corresponding user login!");
			}
			Authentication current = sessionHelper.getAuthentication();
			if (current == null || current.getUserId() != userId) {
				inquiries = inquiryDao.selectFromUserIdNonAnonymous(userId);
			} else {
				inquiries = inquiryDao.selectFromUserId(userId);
			}
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the inquiry!", exception);
		}
		return inquiries;
	}

}
