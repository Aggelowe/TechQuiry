package com.aggelowe.techquiry.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.entity.Observer;
import com.aggelowe.techquiry.database.entity.Response;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.dto.InquiryDto;
import com.aggelowe.techquiry.dto.ResponseDto;
import com.aggelowe.techquiry.dto.UserLoginDto;
import com.aggelowe.techquiry.mapper.InquiryMapper;
import com.aggelowe.techquiry.mapper.ResponseMapper;
import com.aggelowe.techquiry.mapper.UserLoginMapper;
import com.aggelowe.techquiry.mapper.exception.MapperException;
import com.aggelowe.techquiry.service.InquiryService;
import com.aggelowe.techquiry.service.ObserverService;
import com.aggelowe.techquiry.service.ResponseService;
import com.aggelowe.techquiry.service.action.InquiryActionService;
import com.aggelowe.techquiry.service.action.ObserverActionService;
import com.aggelowe.techquiry.service.action.ResponseActionService;
import com.aggelowe.techquiry.service.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link InquiryController} class manages HTTP requests and responses for
 * inquiry operations in the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Controller
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
	 * The service responsible for managing personal {@link Inquiry} operations in
	 * the TechQuiry application.
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
	 * The service responsible for managing personal {@link Response} operations in
	 * the TechQuiry application.
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
	 * The service responsible for managing personal {@link Observer} operations in
	 * the TechQuiry application.
	 */
	private final ObserverActionService observerActionService;

	/**
	 * The mapper responsible for mapping {@link UserLogin} and {@link UserLoginDto}
	 * objects.
	 */
	private final UserLoginMapper userLoginMapper;

	/**
	 * This method will respond to the received request with the number of inquiries
	 * in the database.
	 * 
	 * @return The response with the inquiry count
	 * @throws ServiceException If an error occurs while getting the inquiry count
	 */
	@PostMapping("/count")
	public ResponseEntity<Integer> getCount() throws ServiceException {
		log.debug("Requested inquiry count");
		int count = inquiryService.getInquiryCount();
		return ResponseEntity.ok(count);
	}

	/**
	 * This method will respond to the received request with the requested range of
	 * inquiries.
	 * 
	 * @param count The count of inquiries in the range
	 * @param page  The page of inquiries
	 * @return The response with the requested inquiry range.
	 * @throws ServiceException If an exception occurs while getting the range
	 */
	@PostMapping("/range/{count}/{page}")
	public ResponseEntity<List<InquiryDto>> getRange(@PathVariable int count, @PathVariable int page) throws ServiceException {
		log.debug("Requested " + count + " inquiry range at page " + page);
		List<Inquiry> entities = inquiryService.getInquiryRange(count, page);
		List<InquiryDto> range = entities.stream().map(inquiryMapper::toDto).toList();
		return ResponseEntity.ok(range);
	}

	/**
	 * This method will create the inquiry with the given information in the
	 * database and will respond with the id of the newly constructed inquiry.
	 * 
	 * @param inquiryDto The DTO containing the inquiry data
	 * @return The inquiry id of the inquiry
	 * @throws ServiceException If an exception occurs while creating the inquiry
	 * @throws MapperException  If the required data contained in the received
	 *                          inquiry DTO are missing.
	 */
	@PostMapping("/create")
	public ResponseEntity<Integer> createInquiry(@RequestBody InquiryDto inquiryDto) throws ServiceException, MapperException {
		log.debug("Requested inquiry creation with " + inquiryDto);
		Inquiry inquiry = inquiryMapper.toEntity(inquiryDto);
		int inquiryId = inquiryActionService.createInquiry(inquiry);
		return ResponseEntity.ok(inquiryId);
	}

	/**
	 * This method will respond to the received request with the inquiry with the
	 * given inquiry id.
	 * 
	 * @param inquiryId The inquiry id of the inquiry to select
	 * @return The response with the requested inquiry
	 * @throws ServiceException If an exception occurs while getting the inquiry
	 */
	@PostMapping("/id/{inquiryId}")
	public ResponseEntity<InquiryDto> getInquiry(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested inquiry with id " + inquiryId);
		Inquiry entity = inquiryService.getInquiryByInquiryId(inquiryId);
		InquiryDto inquiryDto = inquiryMapper.toDto(entity);
		return ResponseEntity.ok(inquiryDto);
	}

	/**
	 * This method will delete the inquiry with the given inquiry id from the
	 * server.
	 * 
	 * @param inquiryId The inquiry id of the inquiry to delete
	 * @throws ServiceException If an exception occurs while deleting the inquiry
	 */
	@PostMapping("/id/{inquiryId}/delete")
	public ResponseEntity<Void> deleteInquiry(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested inquiry deletion with inquiry id " + inquiryId);
		inquiryActionService.deleteInquiry(inquiryId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will update the inquiry with the given inquiry id in the server.
	 * 
	 * @param inquiryId  The inquiry id of the inquiry to update
	 * @param inquiryDto The DTO containing the inquiry
	 * @throws ServiceException If an exception occurs while deleting the inquiry
	 */
	@PostMapping("/id/{inquiryId}/update")
	public ResponseEntity<Void> updateInquiry(@PathVariable int inquiryId, @RequestBody InquiryDto inquiryDto) throws ServiceException {
		log.debug("Requested inquiry update with inquiry id " + inquiryId + " and data " + inquiryDto);
		Inquiry original = inquiryService.getInquiryByInquiryId(inquiryId);
		Inquiry inquiry = inquiryMapper.updateEntity(inquiryDto, original);
		inquiryActionService.updateInquiry(inquiry);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will respond to the received request with the list of responses
	 * that the inquiry with the given inquiry id has.
	 * 
	 * @param inquiryId The id of the inquiry to get the responses
	 * @return The response with the requested list of responses
	 * @throws ServiceException If an exception occurs while getting the responses
	 */
	@PostMapping("/id/{inquiryId}/response")
	public ResponseEntity<List<ResponseDto>> getResponses(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested posted responses of inquiry " + inquiryId);
		List<Response> entities = responseService.getResponseListByInquiryId(inquiryId);
		List<ResponseDto> list = entities.stream().map(responseMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method will respond to the received request with the number of responses
	 * that the inquiry with the given inquiry id has.
	 * 
	 * @param inquiryId The id of the inquiry to get the response count
	 * @return The response with the requested responses count
	 * @throws ServiceException If an exception occurs while getting the responses
	 */
	@PostMapping("/id/{inquiryId}/response/count")
	public ResponseEntity<Integer> getResponseCount(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested inquiry count of inquiry " + inquiryId);
		int count = responseService.getResponseCountByInquiryId(inquiryId);
		return ResponseEntity.ok(count);
	}

	/**
	 * This method will create the response with the given information to the
	 * inquiry with the given inquiry id in the database and will respond with the
	 * id of the newly constructed response.
	 * 
	 * @param inquiryId   The inquiry id of the inquiry
	 * @param responseDto The DTO containing the response data
	 * @return The response id of the response
	 * @throws ServiceException If an exception occurs while creating the response
	 * @throws MapperException  If the required data contained in the received
	 *                          response DTO are missing.
	 */
	@PostMapping("/id/{inquiryId}/response/create")
	public ResponseEntity<Integer> createResponse(@PathVariable int inquiryId, @RequestBody ResponseDto responseDto) throws ServiceException, MapperException {
		log.debug("Requested response creation to " + inquiryId + " with " + responseDto);
		Response response = responseMapper.toEntity(responseDto);
		Response updated = response.toBuilder().inquiryId(inquiryId).build();
		int responseId = responseActionService.createResponse(updated);
		return ResponseEntity.ok(responseId);
	}

	/**
	 * This method will respond to the received request with the list of observers
	 * of the inquiry with the given inquiry id.
	 * 
	 * @param inquiryId The id of the inquiry to get the observers
	 * @return The response with the requested list of user logins
	 * @throws ServiceException If an exception occurs while getting the observers
	 */
	@PostMapping("/id/{inquiryId}/observer")
	public ResponseEntity<List<UserLoginDto>> getObservers(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested observers of inquiry " + inquiryId);
		List<UserLogin> entities = observerService.getObserverUserLoginListByInquiryId(inquiryId);
		List<UserLoginDto> list = entities.stream().map(userLoginMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method will respond to the received request with the number of observers
	 * of the inquiry with the given inquiry id.
	 * 
	 * @param inquiryId The id of the inquiry to get the observer count
	 * @return The response with the requested observer count
	 * @throws ServiceException If an exception occurs while getting the observers
	 */
	@PostMapping("/id/{inquiryId}/observer/count")
	public ResponseEntity<Integer> getObserverCount(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Requested observer count of inquiry " + inquiryId);
		int count = observerService.getObserverCountByInquiryId(inquiryId);
		return ResponseEntity.ok(count);
	}

	/**
	 * This method will respond to the received request with whether the current
	 * user is observing the inquiry with the given inquiry id.
	 * 
	 * @param inquiryId The id of the inquiry to check
	 * @return The response with whether the user is observing the inquiry
	 * @throws ServiceException If an exception occurs while checking the observer
	 */
	@PostMapping("/id/{inquiryId}/observer/check")
	public ResponseEntity<Boolean> checkObserver(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Checking observer of inquiry " + inquiryId);
		boolean check = observerActionService.checkObserver(inquiryId);
		return ResponseEntity.ok(check);
	}

	/**
	 * This method will create an observer with the given inquiry id and the id of
	 * the currently logged in user in the database.
	 * 
	 * @param inquiryId The id of the inquiry to create the observer for
	 * @throws ServiceException If an exception occurs while creating the observer
	 */
	@PostMapping("/id/{inquiryId}/observer/create")
	public ResponseEntity<Void> createObserver(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Creating observer of inquiry " + inquiryId);
		observerActionService.createObserver(inquiryId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will delete the observer with the given inquiry id and the id of
	 * the currently logged in user from the database.
	 * 
	 * @param inquiryId The id of the inquiry to delete the observer for
	 * @throws ServiceException If an exception occurs while deleting the observer
	 */
	@PostMapping("/id/{inquiryId}/observer/delete")
	public ResponseEntity<Void> deleteObserver(@PathVariable int inquiryId) throws ServiceException {
		log.debug("Deleting observer of inquiry " + inquiryId);
		observerActionService.deleteObserver(inquiryId);
		return ResponseEntity.noContent().build();
	}

}
