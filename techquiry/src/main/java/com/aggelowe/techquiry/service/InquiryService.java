package com.aggelowe.techquiry.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.Inquiry;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link InquiryService} class provides methods for managing inquiry
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class InquiryService {

	/**
	 * The object responsible for handling the data access for {@link Inquiry}
	 * objects.
	 */
	private final InquiryDao inquiryDao;

	/**
	 * This method returns the total count of inquiries.
	 *
	 * @return The total number of inquiries
	 * @throws InternalErrorException If a database error occurs while retrieving
	 *                                the count
	 */
	public int getInquiryCount() throws ServiceException {
		log.debug("Getting inquiry count");
		try {
			return inquiryDao.count();
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the inquiry count!", exception);
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
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the inquiries
	 */
	public List<Inquiry> getInquiryRange(int count, int page) throws ServiceException {
		log.debug("Getting inquiry range (count=%s, page=%s)".formatted(count, page));
		if (count < 0 || page < 0) {
			throw new InvalidRequestException("The given count/page must be larger than 0!");
		}
		List<Inquiry> range;
		try {
			range = inquiryDao.range(count, count * page);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the inquiries!", exception);
		}
		return range;
	}

	/**
	 * This method returns the inquiry with the given inquiry id.
	 *
	 * @param inquiryId The inquiry id
	 * @return The inquiry with the given inquiry id
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the inquiry
	 */
	public Inquiry getInquiryByInquiryId(int inquiryId) throws ServiceException {
		log.debug("Getting inquiry (inquiryId=%s)".formatted(inquiryId));
		Inquiry inquiry;
		try {
			inquiry = inquiryDao.select(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the inquiry!", exception);
		}
		if (inquiry == null) {
			throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
		}
		return inquiry;
	}

}
