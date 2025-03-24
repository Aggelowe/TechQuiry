package net.techquiry.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.database.dao.InquiryDao;
import net.techquiry.app.database.dao.ResponseDao;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.Inquiry;
import net.techquiry.app.entity.Response;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.ServiceException;

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
	 * This method returns the total count of responses to the inquiry with the
	 * given inquiry id.
	 *
	 * @param inquiryId The inquiry id
	 * @return The total number of responses of the inquiry
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the count
	 */
	public int getResponseCountByInquiryId(int inquiryId) throws ServiceException {
		log.debug("Getting response count (inquiryId=%s)".formatted(inquiryId));
		Inquiry inquiry;
		try {
			inquiry = inquiryDao.select(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the inquiry!", exception);
		}
		if (inquiry == null) {
			throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
		}
		try {
			return responseDao.countFromInquiryId(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the response count!", exception);
		}
	}

	/**
	 * This method returns the list of responses to the inquiry with the given
	 * inquiry id.
	 *
	 * @param inquiryId The inquiry id
	 * @return The responses with the given inquiry id
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the responses
	 */
	public List<Response> getResponseListByInquiryId(int inquiryId) throws ServiceException {
		log.debug("Getting response list (inquiryId=%s)".formatted(inquiryId));
		Inquiry inquiry;
		try {
			inquiry = inquiryDao.select(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the inquiry!", exception);
		}
		if (inquiry == null) {
			throw new EntityNotFoundException("The given inquiry id does not have a corresponding inquiry!");
		}
		try {
			return responseDao.selectFromInquiryId(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the responses!", exception);
		}
	}

	/**
	 * This method returns the response with the given response id.
	 *
	 * @param responseId The response id
	 * @return The response with the given id
	 * @throws EntityNotFoundException If the given response id does not correspond
	 *                                 to a response
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the response
	 */
	public Response getResponseByResponseId(int responseId) throws ServiceException {
		log.debug("Getting response (responseId=%s)".formatted(responseId));
		Response response;
		try {
			response = responseDao.select(responseId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the response!", exception);
		}
		if (response == null) {
			throw new EntityNotFoundException("The given response id does not have a corresponding response!");
		}
		return response;
	}

}
