package com.aggelowe.techquiry.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aggelowe.techquiry.database.dao.InquiryDao;
import com.aggelowe.techquiry.database.dao.ResponseDao;
import com.aggelowe.techquiry.database.exception.DatabaseException;
import com.aggelowe.techquiry.entity.Inquiry;
import com.aggelowe.techquiry.entity.Response;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link ResponseService} class provides methods for managing response
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ResponseService {

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
	 * This method retrieves and returns the total count of responses to the inquiry
	 * with the given inquiry id.
	 *
	 * @param inquiryId The id of the inquiry
	 * @return The total number of responses of the inquiry
	 * @throws EntityNotFoundException If the requested inquiry does not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the count
	 */
	public int getResponseCountByInquiryId(int inquiryId) throws ServiceException {
		log.debug("Getting response count (inquiryId=%s)".formatted(inquiryId));
		Inquiry inquiry;
		try {
			inquiry = inquiryDao.select(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the inquiry!", exception);
		}
		if (inquiry == null) {
			throw new EntityNotFoundException("The requested inquiry does not exist!");
		}
		try {
			return responseDao.countFromInquiryId(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the response count!", exception);
		}
	}

	/**
	 * This method returns the list of responses to the inquiry with the given
	 * inquiry id.
	 *
	 * @param inquiryId The id of the inquiry
	 * @return The responses with the given inquiry id
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the responses
	 */
	public List<Response> getResponseListByInquiryId(int inquiryId) throws ServiceException {
		log.debug("Getting response list (inquiryId=%s)".formatted(inquiryId));
		Inquiry inquiry;
		try {
			inquiry = inquiryDao.select(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the inquiry!", exception);
		}
		if (inquiry == null) {
			throw new EntityNotFoundException("The requested inquiry does not exist!");
		}
		try {
			return responseDao.selectFromInquiryId(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the responses!", exception);
		}
	}

	/**
	 * This method returns the response with the given response id.
	 *
	 * @param responseId The response id
	 * @return The response with the given id
	 * @throws EntityNotFoundException If the requested response does not exist
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the response
	 */
	public Response getResponseByResponseId(int responseId) throws ServiceException {
		log.debug("Getting response (responseId=%s)".formatted(responseId));
		Response response;
		try {
			response = responseDao.select(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("An internal error occured while getting the response!", exception);
		}
		if (response == null) {
			throw new EntityNotFoundException("The requested response does not exist!");
		}
		return response;
	}

}
