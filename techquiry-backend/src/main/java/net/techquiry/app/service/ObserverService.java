package net.techquiry.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.database.dao.InquiryDao;
import net.techquiry.app.database.dao.ObserverDao;
import net.techquiry.app.database.dao.UserLoginDao;
import net.techquiry.app.database.exception.DatabaseException;
import net.techquiry.app.entity.Inquiry;
import net.techquiry.app.entity.Observer;
import net.techquiry.app.entity.UserLogin;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.ServiceException;

/**
 * The {@link ObserverService} class provides methods for managing observer
 * operations in the TechQuiry application.
 *
 * @author Aggelowe
 * @since 0.0.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ObserverService {

	/**
	 * The object responsible for handling the data access for {@link Observer}
	 * objects.
	 */
	private final ObserverDao observerDao;

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
	 * This method returns the total count of observers of the inquiry with the
	 * given inquiry id.
	 *
	 * @param inquiryId The inquiry id
	 * @return The total number of observers of the inquiry
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the count
	 */
	public int getObserverCountByInquiryId(int inquiryId) throws ServiceException {
		log.debug("Getting observer count (inquiryId=%s)".formatted(inquiryId));
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
			return observerDao.countFromInquiryId(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the observer count!", exception);
		}
	}

	/**
	 * This method returns the list of user logins who are observing the inquiry
	 * with the given inquiry id.
	 *
	 * @param inquiryId The inquiry id
	 * @return The observer user logins
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the observer entries
	 */
	public List<UserLogin> getObserverUserLoginListByInquiryId(int inquiryId) throws ServiceException {
		log.debug("Getting observer user login list (inquiryId=%s)".formatted(inquiryId));
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
			return observerDao.selectFromInquiryId(inquiryId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the observer user logins!", exception);
		}
	}

	/**
	 * This method returns the list of inquiries who are being observed by the user
	 * login with the given user id.
	 *
	 * @param userId The user id
	 * @return The observed inquiries
	 * @throws EntityNotFoundException If the given user id does not correspond to a
	 *                                 user login
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the observer entries
	 */
	public List<Inquiry> getObservedInquiryListByUserId(int userId) throws ServiceException {
		log.debug("Getting observed inquiry list (userId=%s)".formatted(userId));
		UserLogin userLogin;
		try {
			userLogin = userLoginDao.select(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the user login!", exception);
		}
		if (userLogin == null) {
			throw new EntityNotFoundException("The given user id does not have a corresponding user login!");
		}
		try {
			return observerDao.selectFromUserId(userId);
		} catch (DatabaseException exception) {
			throw new InternalErrorException("A database error occured while getting the observed inquiries!", exception);
		}
	}

}
