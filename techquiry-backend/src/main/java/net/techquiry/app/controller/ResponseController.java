package net.techquiry.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.techquiry.app.controller.error.ErrorResponse;
import net.techquiry.app.dto.ResponseDto;
import net.techquiry.app.dto.UserLoginDto;
import net.techquiry.app.entity.Response;
import net.techquiry.app.entity.Upvote;
import net.techquiry.app.entity.UserLogin;
import net.techquiry.app.mapper.ResponseMapper;
import net.techquiry.app.mapper.UserLoginMapper;
import net.techquiry.app.service.ResponseService;
import net.techquiry.app.service.UpvoteService;
import net.techquiry.app.service.action.ResponseActionService;
import net.techquiry.app.service.action.UpvoteActionService;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.ForbiddenOperationException;
import net.techquiry.app.service.exception.InternalErrorException;
import net.techquiry.app.service.exception.InvalidRequestException;
import net.techquiry.app.service.exception.ServiceException;
import net.techquiry.app.service.exception.UnauthorizedOperationException;

/**
 * The {@link ResponseController} class manages HTTP requests and responses for
 * response operations in the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@RestController
@RequestMapping("/response")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "response-controller", description = "Controller for handling response operations")
public class ResponseController {

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
	 * The service responsible for managing general {@link Upvote} operations in the
	 * TechQuiry application.
	 */
	private final UpvoteService upvoteService;

	/**
	 * The service responsible for managing personalized {@link Upvote} operations
	 * in the TechQuiry application.
	 */
	private final UpvoteActionService upvoteActionService;

	/**
	 * The mapper responsible for mapping {@link UserLogin} and {@link UserLoginDto}
	 * objects.
	 */
	private final UserLoginMapper userLoginMapper;

	/**
	 * This method responds to the received request with the response with the given
	 * response id.
	 * 
	 * @param responseId The response id of the response to retrieve
	 * @return The response with the requested response
	 * @throws EntityNotFoundException If the given response id does not correspond
	 *                                 to a response
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the response
	 */
	@GetMapping("/id/{responseId}")
	@Operation(summary = "Get response")
	@ApiResponse(responseCode = "200", description = "Response obtained successfully")
	@ApiResponse(responseCode = "404", description = "Response id does not correspond to response", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "500", description = "Database error occured", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<ResponseDto> getResponse(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested response (responseId=%s)".formatted(responseId));
		Response entity = responseService.getResponseByResponseId(responseId);
		ResponseDto responseDto = responseMapper.toDto(entity);
		return ResponseEntity.ok(responseDto);
	}

	/**
	 * This method deletes the response with the given response id from the
	 * database.
	 * 
	 * @param responseId The response id of the response to delete
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        user id of the response in the
	 *                                        database
	 * @throws EntityNotFoundException        If the given response id does not
	 *                                        correspond to a response
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        deleting the response
	 */
	@PostMapping("/id/{responseId}/delete")
	@Operation(summary = "Delete response")
	@ApiResponse(responseCode = "204", description = "Response deleted successfully")
	@ApiResponse(responseCode = "401", description = "User is not logged in", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "403", description = "Current user does not have user id of existing response", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "Response id does not correspond to response", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "500", description = "Database error occured", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<Void> deleteResponse(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested response deletion (responseId=%s)".formatted(responseId));
		responseActionService.deleteResponse(responseId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method updates the response with the given response id in the database.
	 * 
	 * @param responseId  The response id of the response to update
	 * @param responseDto The DTO containing the response
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        user id of the response already in the
	 *                                        database
	 * @throws EntityNotFoundException        If the given inquiry id do not
	 *                                        correspond to an inquiry
	 * @throws InvalidRequestException        If the given content is blank
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        updating the response
	 */
	@PostMapping("/id/{responseId}/update")
	@Operation(summary = "Update response")
	@ApiResponse(responseCode = "204", description = "Response updated successfully")
	@ApiResponse(responseCode = "401", description = "User is not logged in", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "403", description = "Current user does not have user id of existing response", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "Response id does not correspond to inquiry", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "400", description = "Content blank", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "500", description = "Database error occured", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<Void> updateResponse(@PathVariable int responseId, @RequestBody ResponseDto responseDto) throws ServiceException {
		log.debug("Requested response update (responseId=%s, responseDto=%s)".formatted(responseId, responseDto));
		Response original = responseService.getResponseByResponseId(responseId);
		Response response = responseMapper.updateEntity(responseDto, original);
		responseActionService.updateResponse(response);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method responds to the received request with the list of upvotes of the
	 * response with the given response id.
	 * 
	 * @param responseId The id of the response of which to get the upvotes
	 * @return The response with the requested list of user logins
	 * @throws EntityNotFoundException If the given response id does not correspond
	 *                                 to a response
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the upvotes
	 */
	@GetMapping("/id/{responseId}/upvote")
	@Operation(summary = "Get upvotes")
	@ApiResponse(responseCode = "200", description = "Upvotes obtained successfully")
	@ApiResponse(responseCode = "404", description = "Response id does not correspond to response", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "500", description = "Database error occured", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<List<UserLoginDto>> getUpvotes(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested upvotes (responseId=%s)".formatted(responseId));
		List<UserLogin> entities = upvoteService.getUpvoteUserLoginListByResponseId(responseId);
		List<UserLoginDto> list = entities.stream().map(userLoginMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method responds to the received request with the number of upvotes of
	 * the response with the given response id.
	 * 
	 * @param responseId The id of the response of which to get the upvote count
	 * @return The response with the requested upvote count
	 * @throws EntityNotFoundException If the given response id does not correspond
	 *                                 to a response
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the count
	 */
	@GetMapping("/id/{responseId}/upvote/count")
	@Operation(summary = "Get upvote count")
	@ApiResponse(responseCode = "200", description = "Upvote count obtained successfully")
	@ApiResponse(responseCode = "404", description = "Response id does not correspond to response", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "500", description = "Database error occured", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<Integer> getUpvoteCount(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested upvote count (responseId=%s)".formatted(responseId));
		int count = upvoteService.getUpvoteCountByResponseId(responseId);
		return ResponseEntity.ok(count);
	}

	/**
	 * This method responds to the received request with whether the current user is
	 * upvoting the response with the given response id.
	 * 
	 * @param responseId The id of the response to check
	 * @return The response with whether the user is upvoting the response
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given response id does not
	 *                                        correspond to a response
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        checking the upvote
	 */
	@GetMapping("/id/{responseId}/upvote/check")
	@Operation(summary = "Check upvote")
	@ApiResponse(responseCode = "200", description = "Upvote checked successfully")
	@ApiResponse(responseCode = "401", description = "User is not logged in", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "Response id does not correspond to response", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "500", description = "Database error occured", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<Boolean> checkUpvote(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested upvote check (responseId=%s)".formatted(responseId));
		boolean check = upvoteActionService.checkUpvote(responseId);
		return ResponseEntity.ok(check);
	}

	/**
	 * This method creates an upvote with the given response id and the user id of
	 * the currently logged in user in the database.
	 * 
	 * @param responseId The id of the response for which to create the upvote
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given response id does not
	 *                                        correspond to a response
	 * @throws InvalidRequestException        If the given upvote already exists
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        creating the upvote
	 */
	@PostMapping("/id/{responseId}/upvote/create")
	@Operation(summary = "Create upvote")
	@ApiResponse(responseCode = "204", description = "Upvote created successfully")
	@ApiResponse(responseCode = "401", description = "User is not logged in", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "Response id does not correspond to response", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "400", description = "Upvote already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "500", description = "Database error occured", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<Void> createUpvote(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested upvote creation (responseId=%s)".formatted(responseId));
		upvoteActionService.createUpvote(responseId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method deletes the upvote with the given response id and the user id of
	 * the currently logged in user from the database.
	 * 
	 * @param responseId The id of the response for which to delete the upvote
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws EntityNotFoundException        If the given response id does not
	 *                                        correspond to a response
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        deleting the upvote
	 */
	@PostMapping("/id/{responseId}/upvote/delete")
	@Operation(summary = "Delete upvote")
	@ApiResponse(responseCode = "204", description = "Upvote deleted successfully")
	@ApiResponse(responseCode = "401", description = "User is not logged in", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "Upvote does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "500", description = "Database error occured", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<Void> deleteUpvote(@PathVariable int responseId) throws ServiceException {
		log.debug("Requested upvote deletion (responseId=%s)".formatted(responseId));
		upvoteActionService.deleteUpvote(responseId);
		return ResponseEntity.noContent().build();
	}

}
