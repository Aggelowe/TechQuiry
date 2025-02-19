package com.aggelowe.techquiry.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aggelowe.techquiry.database.entity.Response;
import com.aggelowe.techquiry.database.entity.Upvote;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.dto.ResponseDto;
import com.aggelowe.techquiry.dto.UserLoginDto;
import com.aggelowe.techquiry.mapper.ResponseMapper;
import com.aggelowe.techquiry.mapper.UserLoginMapper;
import com.aggelowe.techquiry.service.ResponseService;
import com.aggelowe.techquiry.service.UpvoteService;
import com.aggelowe.techquiry.service.action.ResponseActionService;
import com.aggelowe.techquiry.service.action.UpvoteActionService;
import com.aggelowe.techquiry.service.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link ResponseController} class manages HTTP requests and responses for
 * response operations in the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Controller
@RequestMapping("/api/response")
@RequiredArgsConstructor
@Log4j2
public class ResponseController {

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
	 * The service responsible for managing general {@link Upvote} operations in the
	 * TechQuiry application.
	 */
	private final UpvoteService upvoteService;

	/**
	 * The service responsible for managing personal {@link Upvote} operations in
	 * the TechQuiry application.
	 */
	private final UpvoteActionService upvoteActionService;

	/**
	 * The mapper responsible for mapping {@link UserLogin} and {@link UserLoginDto}
	 * objects.
	 */
	private final UserLoginMapper userLoginMapper;

	/**
	 * This method will respond to the received request with the response with the
	 * given response id.
	 * 
	 * @param responseId The response id of the response to select
	 * @return The response with the requested response
	 * @throws ServiceException If an exception occurs while getting the response
	 */
	@PostMapping("/id/{responseId}")
	public ResponseEntity<ResponseDto> getResponse(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested response with id " + responseId);
		Response entity = responseService.getResponseByResponseId(responseId);
		ResponseDto responseDto = responseMapper.toDto(entity);
		return ResponseEntity.ok(responseDto);
	}

	/**
	 * This method will delete the response with the given response id from the
	 * server.
	 * 
	 * @param responseId The response id of the response to delete
	 * @throws ServiceException If an exception occurs while deleting the response
	 */
	@PostMapping("/id/{responseId}/delete")
	public ResponseEntity<Void> deleteResponse(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested response deletion with response id " + responseId);
		responseActionService.deleteResponse(responseId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will update the response with the given response id in the
	 * server.
	 * 
	 * @param responseId  The response id of the response to update
	 * @param responseDto The DTO containing the response
	 * @throws ServiceException If an exception occurs while deleting the response
	 */
	@PostMapping("/id/{responseId}/update")
	public ResponseEntity<Void> updateResponse(@PathVariable int responseId, @RequestBody ResponseDto responseDto) throws ServiceException {
		log.debug("Requested response update with response id " + responseId + " and data " + responseDto);
		Response original = responseService.getResponseByResponseId(responseId);
		Response response = responseMapper.updateEntity(responseDto, original);
		responseActionService.updateResponse(response);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will respond to the received request with the list of upvotes of
	 * the response with the given response id.
	 * 
	 * @param responseId The id of the response to get the upvotes
	 * @return The response with the requested list of user logins
	 * @throws ServiceException If an exception occurs while getting the upvotes
	 */
	@PostMapping("/id/{responseId}/upvote")
	public ResponseEntity<List<UserLoginDto>> getUpvotes(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested upvotes of response " + responseId);
		List<UserLogin> entities = upvoteService.getUpvoteUserLoginListByResponseId(responseId);
		List<UserLoginDto> list = entities.stream().map(userLoginMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method will respond to the received request with the number of upvotes
	 * of the response with the given response id.
	 * 
	 * @param responseId The id of the response to get the upvote count
	 * @return The response with the requested upvote count
	 * @throws ServiceException If an exception occurs while getting the upvotes
	 */
	@PostMapping("/id/{responseId}/upvote/count")
	public ResponseEntity<Integer> getUpvoteCount(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested upvote count of response " + responseId);
		int count = upvoteService.getUpvoteCountByResponseId(responseId);
		return ResponseEntity.ok(count);
	}

	/**
	 * This method will respond to the received request with whether the current
	 * user is upvoting the response with the given response id.
	 * 
	 * @param responseId The id of the response to check
	 * @return The response with whether the user is upvoting the response
	 * @throws ServiceException If an exception occurs while checking the upvote
	 */
	@PostMapping("/id/{responseId}/upvote/check")
	public ResponseEntity<Boolean> checkUpvote(@PathVariable int responseId) throws ServiceException {
		log.debug("Checking upvote of response " + responseId);
		boolean check = upvoteActionService.checkUpvote(responseId);
		return ResponseEntity.ok(check);
	}

	/**
	 * This method will create an upvote with the given response id and the id of
	 * the currently logged in user in the database.
	 * 
	 * @param responseId The id of the response to create the upvote for
	 * @throws ServiceException If an exception occurs while creating the upvote
	 */
	@PostMapping("/id/{responseId}/upvote/create")
	public ResponseEntity<Void> createUpvote(@PathVariable int responseId) throws ServiceException {
		log.debug("Creating upvote of response " + responseId);
		upvoteActionService.createUpvote(responseId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will delete the upvote with the given response id and the id of
	 * the currently logged in user from the database.
	 * 
	 * @param responseId The id of the response to delete the upvote for
	 * @throws ServiceException If an exception occurs while deleting the upvote
	 */
	@PostMapping("/id/{responseId}/upvote/delete")
	public ResponseEntity<Void> deleteUpvote(@PathVariable int responseId) throws ServiceException {
		log.debug("Deleting upvote of response " + responseId);
		upvoteActionService.deleteUpvote(responseId);
		return ResponseEntity.noContent().build();
	}

}
