package com.aggelowe.techquiry.service;

import java.util.List;

import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.entities.Inquiry;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.DatabaseException;
import com.aggelowe.techquiry.service.action.InquiryActionService;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.InternalErrorException;
import com.aggelowe.techquiry.service.exceptions.InvalidRequestException;
import com.aggelowe.techquiry.service.exceptions.ServiceException;

/**
 * The {@link InquiryService} class provides methods for managing inquiry
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
public class InquiryService {

	/**
	 * The object responsible for handling the data access for {@link Inquiry}
	 * objects.
	 */
	private final InquiryDao inquiryDao;

	/**
	 * The object responsible for managing the database of the application.
	 */
	private final DatabaseManager databaseManager;

	/**
	 * This constructor constructs a new {@link InquiryService} instance that is
	 * handling the inquiry operations of the application.
	 * 
	 * @param databaseManager The object managing the application database
	 */
	public InquiryService(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
		this.inquiryDao = databaseManager.getInquiryDao();
	}

	/**
	 * This method retrieves and returns the total count of inquiries.
	 *
	 * @return The total number of of inquiries
	 * @throws InternalErrorException If an internal error occurs while retrieving
	 *                                the count
	 */
	public int getInquiryCount() throws ServiceException {
		try {
			return inquiryDao.count();
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the inquiry count!", exception);
		}
	}

	/**
	 * This method returns the range of inquiries based on the given count of
	 * inquiries per page and the page number.
	 *
	 * @param count The number of inquiries per page
	 * @param page  The page number of inquiries to return
	 * @return The requested page of inquiries
	 * @throws InvalidRequestException If the count/page is smaller than 0
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the inquiries
	 */
	public List<Inquiry> getInquiryRange(int count, int page) throws ServiceException {
		if (count < 0 || page < 0) {
			throw new InvalidRequestException("The given count/page must be larger than 0!");
		}
		List<Inquiry> range;
		try {
			range = inquiryDao.range(count, count * page);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the inquiries!", exception);
		}
		return range;
	}

	/**
	 * This method returns the inquiry with the given inquiry id.
	 *
	 * @param id The inquiry id
	 * @return The inquiry with the given id
	 * @throws EntityNotFoundException If the requested inquiry does not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the inquiry
	 */
	public Inquiry findInquiryByInquiryId(int id) throws ServiceException {
		Inquiry inquiry;
		try {
			inquiry = inquiryDao.select(id);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the inquiry!", exception);
		}
		if (inquiry == null) {
			throw new EntityNotFoundException("The requested inquiry does not exist!");
		}
		return inquiry;
	}

	/**
	 * This method constructs and returns the personalized service for the given
	 * user.
	 *
	 * @param current The currently logged-in user
	 * @return The service instance for making the personalized operations
	 */
	public InquiryActionService createActionService(UserLogin current) {
		return new InquiryActionService(databaseManager, current);
	}

}
