package com.aggelowe.techquiry.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aggelowe.techquiry.dto.InquiryDto;
import com.aggelowe.techquiry.dto.ResponseDto;
import com.aggelowe.techquiry.dto.UserLoginDto;
import com.aggelowe.techquiry.entity.Inquiry;
import com.aggelowe.techquiry.entity.Observer;
import com.aggelowe.techquiry.entity.Response;
import com.aggelowe.techquiry.entity.UserLogin;
import com.aggelowe.techquiry.mapper.InquiryMapper;
import com.aggelowe.techquiry.mapper.ResponseMapper;
import com.aggelowe.techquiry.mapper.UserLoginMapper;
import com.aggelowe.techquiry.mapper.exception.MapperException;
import com.aggelowe.techquiry.mapper.exception.MissingValueException;
import com.aggelowe.techquiry.service.InquiryService;
import com.aggelowe.techquiry.service.ObserverService;
import com.aggelowe.techquiry.service.ResponseService;
import com.aggelowe.techquiry.service.action.InquiryActionService;
import com.aggelowe.techquiry.service.action.ObserverActionService;
import com.aggelowe.techquiry.service.action.ResponseActionService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.exception.UnauthorizedOperationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link InquiryController} class manages HTTP requests and responses for
 * inquiry operations in the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@RestController
@RequestMapping("/api/inquiry")
@RequiredArgsConstructor
@Log4j2
public class InquiryController {

	/**
	 * The service for managing general {@link Inquiry} operations in the TechQuiry
	 * application.
	 */
	private final InquiryService inquiryService;

	/**
	 * The service responsible for managing personalized {@link Inquiry} operations
	 * in the TechQuiry application.
	 */
	private final InquiryActionService inquiryActionService;

	/**
	 * The mapper responsible for mapping {@link Inquiry} and {@link InquiryDto}
	 * objects.
	 */
	private final InquiryMapper inquiryMapper;

	/**
	 * The service responsible for managing general {@link Response} operations in
	 * the TechQuiry application.
	 */
	private final ResponseService responseService;

	/**
	 * The service responsible for managing personalized {@link Response} operations
	 * in the TechQuiry application.
	 */
	private final ResponseActionService responseActionService;

	/**
	 * The mapper responsible for mapping {@link Response} and {@link ResponseDto}
	 * objects.
	 */
	private final ResponseMapper responseMapper;

	/**
	 * The service responsible for managing general {@link Observer} operations in
	 * the TechQuiry application.
	 */
	private final ObserverService observerService;

	/**
	 * The service responsible for managing personalized {@link Observer} operations
	 * in the TechQuiry application.
	 */
	private final ObserverActionService observerActionService;

	/**
	 * The mapper responsible for mapping {@link UserLogin} and {@link UserLoginDto}
	 * objects.
	 */
	private final UserLoginMapper userLoginMapper;

	/**
	 * This method responds to the received request with the number of inquiries in
	 * the database.
	 * 
	 * @return The response with the inquiry count
	 * @throws InternalErrorException If a database error occurs while getting the
	 *                                inquiry count
	 */
	@GetMapping("/count")
	public ResponseEntity<Integer> getCount() throws ServiceException {
		log.debug("Requested inquiry count");
		int count = inquiryService.getInquiryCount();
		return ResponseEntity.ok(count);
	}

	/**
	 * This method responds to the received request with the requested range of
	 * inquiries.
	 * 
	 * @param count The count of inquiries in the range
	 * @param page  The page of inquiries
	 * @return The response with the requested inquiry range
	 * @throws InvalidRequestException If the count/page is smaller than 0
	 * @throws InternalErrorException  If an internal error occurs while retrieving
	 *                                 the inquiries
	 */
	@GetMapping("/range/{count}/{page}")
	public ResponseEntity<List<InquiryDto>> getRange(@PathVariable int count, @PathVariable int page) throws ServiceException {
		log.debug("Requested inquiry range (count=%s, page=%s)".formatted(count, page));
		List<Inquiry> entities = inquiryService.getInquiryRange(count, page);
		List<InquiryDto> range = entities.stream().map(inquiryMapper::toDto).toList();
		return ResponseEntity.ok(range);
	}

	/**
	 * This method creates the inquiry with the given information in the database
	 * and responds with the inquiry id of the newly constructed inquiry.
	 * 
	 * @param inquiryDto The DTO containing the inquiry data
	 * @return The response with the inquiry id of the inquiry
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given user id does not
	 *                                        correspond to a user login
	 * @throws InvalidRequestException        If the given title or content are
	 *                                        blank
	 * @throws MissingValueException          If the title, content or anonymous
	 *                                        flag in the DTO are missing
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        creating the user data
	 */
	@PostMapping("/create")
	public ResponseEntity<Integer> createInquiry(@RequestBody InquiryDto inquiryDto) throws ServiceException, MapperException {
		log.debug("Requested inquiry creation (inquiryDto=%s)".formatted(inquiryDto));
		Inquiry inquiry = inquiryMapper.toEntity(inquiryDto);
		int inquiryId = inquiryActionService.createInquiry(inquiry);
		return ResponseEntity.ok(inquiryId);
	}

	/**
	 * This method responds to the received request with the inquiry with the given
	 * inquiry id.
	 * 
	 * @param inquiryId The inquiry id of the inquiry to retrieve
	 * @return The response with the requested inquiry
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the inquiry
	 */
	@GetMapping("/id/{inquiryId}")
	public ResponseEntity<InquiryDto> getInquiry(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested inquiry (inquiryId=%s)".formatted(inquiryId));
		Inquiry entity = inquiryService.getInquiryByInquiryId(inquiryId);
		InquiryDto inquiryDto = inquiryMapper.toDto(entity);
		return ResponseEntity.ok(inquiryDto);
	}

	/**
	 * This method deletes the inquiry with the given inquiry id from the database.
	 * 
	 * @param inquiryId The inquiry id of the inquiry to delete
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        user id of the inquiry in the database
	 * @throws EntityNotFoundException        If the given inquiry id does not
	 *                                        correspond to an inquiry
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        deleting the inquiry
	 */
	@PostMapping("/id/{inquiryId}/delete")
	public ResponseEntity<Void> deleteInquiry(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested inquiry deletion (inquiryId=%s)".formatted(inquiryId));
		inquiryActionService.deleteInquiry(inquiryId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method updates the inquiry with the given inquiry id in the database.
	 * 
	 * @param inquiryId  The inquiry id of the inquiry to update
	 * @param inquiryDto The DTO containing the inquiry
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        user id of the inquiry already in the
	 *                                        database
	 * @throws EntityNotFoundException        If the given inquiry id does not
	 *                                        correspond to an inquiry
	 * @throws InvalidRequestException        If the given title or content are
	 *                                        blank
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        updating the inquiry
	 */
	@PostMapping("/id/{inquiryId}/update")
	public ResponseEntity<Void> updateInquiry(@PathVariable int inquiryId, @RequestBody InquiryDto inquiryDto) throws ServiceException {
		log.debug("Requested inquiry update (inquiryId=%s, inquiryDto=%s)".formatted(inquiryId, inquiryDto));
		Inquiry original = inquiryService.getInquiryByInquiryId(inquiryId);
		Inquiry inquiry = inquiryMapper.updateEntity(inquiryDto, original);
		inquiryActionService.updateInquiry(inquiry);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method responds to the received request with the list of responses that
	 * the inquiry with the given inquiry id has.
	 * 
	 * @param inquiryId The id of the inquiry of which to get the responses
	 * @return The response with the requested list of responses
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the responses
	 */
	@GetMapping("/id/{inquiryId}/response")
	public ResponseEntity<List<ResponseDto>> getResponses(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested responses (inquiryId=%s)".formatted(inquiryId));
		List<Response> entities = responseService.getResponseListByInquiryId(inquiryId);
		List<ResponseDto> list = entities.stream().map(responseMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method responds to the received request with the number of responses
	 * that the inquiry with the given inquiry id has.
	 * 
	 * @param inquiryId The id of the inquiry of which to get the response count
	 * @return The response with the requested responses count
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the count
	 */
	@GetMapping("/id/{inquiryId}/response/count")
	public ResponseEntity<Integer> getResponseCount(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested response count (inquiryId=%s)".formatted(inquiryId));
		int count = responseService.getResponseCountByInquiryId(inquiryId);
		return ResponseEntity.ok(count);
	}

	/**
	 * This method creates the response with the given information to the inquiry
	 * with the given inquiry id in the database and responds with the response id
	 * of the newly constructed response.
	 * 
	 * @param inquiryId   The inquiry id of the inquiry
	 * @param responseDto The DTO containing the response data
	 * @return The response id of the response
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given inquiry id does not
	 *                                        correspond to a inquiry
	 * @throws InvalidRequestException        If the given content is blank
	 * @throws MissingValueException          If the content or anonymous flag in
	 *                                        the DTO are missing
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        creating the response
	 */
	@PostMapping("/id/{inquiryId}/response/create")
	public ResponseEntity<Integer> createResponse(@PathVariable int inquiryId, @RequestBody ResponseDto responseDto) throws ServiceException, MapperException {
		log.debug("Requested response creation (inquiryId=%s, responseDto=%s)".formatted(inquiryId, responseDto));
		Response response = responseMapper.toEntity(responseDto);
		Response updated = response.toBuilder().inquiryId(inquiryId).build();
		int responseId = responseActionService.createResponse(updated);
		return ResponseEntity.ok(responseId);
	}

	/**
	 * This method responds to the received request with the list of observers of
	 * the inquiry with the given inquiry id.
	 * 
	 * @param inquiryId The id of the inquiry of which to get the observers
	 * @return The response with the requested list of user logins
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the observers
	 */
	@GetMapping("/id/{inquiryId}/observer")
	public ResponseEntity<List<UserLoginDto>> getObservers(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested observers (inquiryId=%s)".formatted(inquiryId));
		List<UserLogin> entities = observerService.getObserverUserLoginListByInquiryId(inquiryId);
		List<UserLoginDto> list = entities.stream().map(userLoginMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method responds to the received request with the number of observers of
	 * the inquiry with the given inquiry id.
	 * 
	 * @param inquiryId The id of the inquiry of which to get the observer count
	 * @return The response with the requested observer count
	 * @throws EntityNotFoundException If the given inquiry id does not correspond
	 *                                 to an inquiry
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the count
	 */
	@GetMapping("/id/{inquiryId}/observer/count")
	public ResponseEntity<Integer> getObserverCount(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested observer count (inquiryId=%s)".formatted(inquiryId));
		int count = observerService.getObserverCountByInquiryId(inquiryId);
		return ResponseEntity.ok(count);
	}

	/**
	 * This method responds to the received request with whether the current user is
	 * observing the inquiry with the given inquiry id.
	 * 
	 * @param inquiryId The inquiry id of the inquiry to check
	 * @return The response with whether the user is observing the inquiry
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        checking the observer
	 */
	@GetMapping("/id/{inquiryId}/observer/check")
	public ResponseEntity<Boolean> checkObserver(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested observer check (inquiryId=%s)".formatted(inquiryId));
		boolean check = observerActionService.checkObserver(inquiryId);
		return ResponseEntity.ok(check);
	}

	/**
	 * This method creates an observer with the given inquiry id and the user id of
	 * the currently logged in user in the database.
	 * 
	 * @param inquiryId The id of the inquiry for which to create the observer
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given inquiry id does not
	 *                                        correspond to an inquiry
	 * @throws InvalidRequestException        If the given observer already exists
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        creating the observer
	 */
	@PostMapping("/id/{inquiryId}/observer/create")
	public ResponseEntity<Void> createObserver(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested observer creation (inquiryId=%s)".formatted(inquiryId));
		observerActionService.createObserver(inquiryId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method deletes the observer with the given inquiry id and the user id of
	 * the currently logged in user from the database.
	 * 
	 * @param inquiryId The id of the inquiry from which to delete the observer
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the requested observer does not
	 *                                        exist
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        deleting the observer
	 */
	@PostMapping("/id/{inquiryId}/observer/delete")
	public ResponseEntity<Void> deleteObserver(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested observer deletion (inquiryId=%s)".formatted(inquiryId));
		observerActionService.deleteObserver(inquiryId);
		return ResponseEntity.noContent().build();
	}

}
