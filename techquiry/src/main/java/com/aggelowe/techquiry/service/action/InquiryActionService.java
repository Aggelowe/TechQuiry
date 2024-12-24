package com.aggelowe.techquiry.service.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.dao.UserLoginDao;
import com.aggelowe.techquiry.database.entities.Inquiry;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;
import com.aggelowe.techquiry.helper.UserSessionHelper;
import com.aggelowe.techquiry.service.Authentication;
import com.aggelowe.techquiry.service.InquiryService;
import com.aggelowe.techquiry.service.exceptions.*;

/**
 * The {@link InquiryActionService} class is a dependency of
 * {@link InquiryService} whose methods provide different functionality for
 * different users.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
public class InquiryActionService {

    private final UserSessionHelper userSessionHelper; 

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
	 * This constructor constructs a new {@link InquiryActionService} instance that
	 * is handling the personalized inquiry operations of the application.
	 * 
	 * @param databaseManager The object managing the application database
	 * @param current         The user initializing the operations
	 */
	@Autowired
    public InquiryActionService(final UserSessionHelper userSessionHelper, final InquiryDao inquiryDao, final UserLoginDao userLoginDao) {
        super();
        this.userSessionHelper = userSessionHelper;
        this.inquiryDao = inquiryDao;
        this.userLoginDao = userLoginDao;
    }


	/**
	 * This method inserts the given {@link Inquiry} object in the database
	 *
	 * @param inquiry The inquiry object to create
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given user id
	 * @throws EntityNotFoundException     If the given user id does not correspond
	 *                                     to a user login
	 * @throws InvalidRequestException     If the given title or content are empty
	 * @throws InternalErrorException      If an internal error occurs while
	 *                                     creating the user data
	 * 
	 */
	public int createInquiry(Inquiry inquiry) throws ServiceException {
        Authentication current =  userSessionHelper.getAuthentication();
		if (current == null || current.getId() != inquiry.getUserId()) {
			throw new ForbiddenOperationException("The requested inquiry creation is forbidden!");
		}
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		if (title.isEmpty() || content.isEmpty()) {
			throw new InvalidRequestException("The given title and content name must not be empty!");
		}
		try {
			UserLogin userLogin = userLoginDao.select(inquiry.getUserId());
			if (userLogin == null) {
				throw new EntityNotFoundException("The given user id does not have a corresponding login!");
			}
			return inquiryDao.insert(inquiry);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the inquiry!", exception);
		}
	}

    /**
	 * This method deletes the inquiry with the specified inquiry id.
	 *
	 * @param id The inquiry id
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     given user id
	 * @throws EntityNotFoundException     If the requested inquiry does not exist
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     deleting the inquiry
	 */
	public void deleteInquiry(int id) throws ServiceException {
        Authentication current =  userSessionHelper.getAuthentication();
		try {
			Inquiry inquiry = inquiryDao.select(id);
			if (inquiry == null) {
				throw new EntityNotFoundException("The requested inquiry does not exist!");
			}
			if (current == null || current.getId() != inquiry.getUserId()) {
				throw new ForbiddenOperationException("The requested inquiry deletion is forbidden!");
			}
			inquiryDao.delete(id);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while deleting the inquiry!", exception);
		}
	}

	/**
	 * This method updates an existing inquiry with the data from the given
	 * {@link Inquiry} object.
	 * 
	 * @param inquiry The inquiry object
	 * @throws ForbiddenOperationException If the current user does not have the
	 *                                     user id contained in the given inquiry or
	 *                                     the inquiry contained in the database
	 * @throws EntityNotFoundException     If the given id does not correspond to an
	 *                                     inquiry id
	 * @throws InvalidRequestException     If the given title or content are empty
	 * @throws InternalErrorException      If an internal error occurred while
	 *                                     updating the inquiry
	 */
	public void updateInquiry(Inquiry inquiry) throws ServiceException {
        Authentication current =  userSessionHelper.getAuthentication();
		if (current == null || current.getId() != inquiry.getUserId()) {
			throw new ForbiddenOperationException("The requested inquiry update is forbidden!");
		}
		String title = inquiry.getTitle();
		String content = inquiry.getContent();
		if (title.isEmpty() || content.isEmpty()) {
			throw new InvalidRequestException("The given title and content name must not be empty!");
		}
		try {
			Inquiry idInquiry = inquiryDao.select(inquiry.getId());
			if (idInquiry == null) {
				throw new EntityNotFoundException("The requested inquiry does not exist!");
			}
			if (current.getId() != idInquiry.getUserId()) {
				throw new ForbiddenOperationException("The requested inquiry update is forbidden!");
			}
			inquiryDao.update(inquiry);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while creating the inquiry!", exception);
		}
	}

	/**
	 * This method returns the list of inquiries with the given user id. If the
	 * current user does not have the user id, the list will be limited to
	 * non-anonymous inquiries.
	 *
	 * @param id The user id
	 * @return The inquiries with the given user id
	 * @throws EntityNotFoundException If the given user id does not correspond to a
	 *                                 user login
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the inquiry
	 */
	public List<Inquiry> getInquiryListByUserId(int id) throws ServiceException {
        Authentication current =  userSessionHelper.getAuthentication();
		List<Inquiry> inquiries;
		try {
			UserLogin userLogin = userLoginDao.select(id);
			if (userLogin == null) {
				throw new EntityNotFoundException("The given user id does not have a corresponding login!");
			}
			if (current == null || current.getId() != id) {
				inquiries = inquiryDao.selectFromUserIdNonAnonymous(id);
			} else {
				inquiries = inquiryDao.selectFromUserId(id);
			}
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the inquiry!", exception);
		}
		return inquiries;
	}

}
