package com.aggelowe.techquiry.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;

/**
 * The {@link InquiryService} class provides methods for managing inquiry
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
public class InquiryService {

	/**
	 * The object responsible for handling the data access for {@link Inquiry}
	 * objects.
	 */
	private final InquiryDao inquiryDao;

	/**
	 * This constructor constructs a new {@link InquiryService} instance that is
	 * handling the inquiry operations of the application.
	 * 
	 * @param inquiryDao The inquiry data access object
	 */
	@Autowired
	public InquiryService(InquiryDao inquiryDao) {
		this.inquiryDao = inquiryDao;
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
	public Inquiry getInquiryByInquiryId(int id) throws ServiceException {
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

}
