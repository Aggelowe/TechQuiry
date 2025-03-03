package com.aggelowe.techquiry.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aggelowe.techquiry.dto.InquiryDto;
import com.aggelowe.techquiry.dto.ResponseDto;
import com.aggelowe.techquiry.dto.UserDataDto;
import com.aggelowe.techquiry.dto.UserLoginDto;
import com.aggelowe.techquiry.entity.Inquiry;
import com.aggelowe.techquiry.entity.Observer;
import com.aggelowe.techquiry.entity.Response;
import com.aggelowe.techquiry.entity.Upvote;
import com.aggelowe.techquiry.entity.UserData;
import com.aggelowe.techquiry.entity.UserLogin;
import com.aggelowe.techquiry.mapper.InquiryMapper;
import com.aggelowe.techquiry.mapper.ResponseMapper;
import com.aggelowe.techquiry.mapper.UserDataMapper;
import com.aggelowe.techquiry.mapper.UserLoginMapper;
import com.aggelowe.techquiry.mapper.exception.MapperException;
import com.aggelowe.techquiry.mapper.exception.MissingValueException;
import com.aggelowe.techquiry.service.ObserverService;
import com.aggelowe.techquiry.service.UpvoteService;
import com.aggelowe.techquiry.service.UserDataService;
import com.aggelowe.techquiry.service.UserLoginService;
import com.aggelowe.techquiry.service.action.InquiryActionService;
import com.aggelowe.techquiry.service.action.UserDataActionService;
import com.aggelowe.techquiry.service.action.UserLoginActionService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InternalErrorException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.exception.UnauthorizedOperationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link UserController} class manages HTTP requests and responses for user
 * operations in the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {

	/**
	 * The service responsible for managing general {@link UserLogin} operations in
	 * the TechQuiry application.
	 */
	private final UserLoginService userLoginService;

	/**
	 * The service responsible for managing personalized {@link UserLogin}
	 * operations in the TechQuiry application.
	 */
	private final UserLoginActionService userLoginActionService;

	/**
	 * The mapper responsible for mapping {@link UserLogin} and {@link UserLoginDto}
	 * objects.
	 */
	private final UserLoginMapper userLoginMapper;

	/**
	 * The service responsible for managing general {@link UserData} operations in
	 * the TechQuiry application.
	 */
	private final UserDataService userDataService;

	/**
	 * The service responsible for managing personalized {@link UserData} operations
	 * in the TechQuiry application.
	 */
	private final UserDataActionService userDataActionService;

	/**
	 * The mapper responsible for mapping {@link UserData} and {@link UserDataDto}
	 * objects.
	 */
	private final UserDataMapper userDataMapper;

	/**
	 * The service responsible for managing general {@link Observer} operations in
	 * the TechQuiry application.
	 */
	private final ObserverService observerService;

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
	 * The service responsible for managing general {@link Upvote} operations in the
	 * TechQuiry application.
	 */
	private final UpvoteService upvoteService;

	/**
	 * The mapper responsible for mapping {@link Response} and {@link ResponseDto}
	 * objects.
	 */
	private final ResponseMapper responseMapper;

	/**
	 * This method responds to the received request with the number of user logins
	 * in the database.
	 * 
	 * @return The response with the user count
	 * @throws InternalErrorException If a database error occurs while retrieving
	 *                                the count
	 */
	@GetMapping("/count")
	public ResponseEntity<Integer> getCount() throws ServiceException {
		log.debug("Requested user login count");
		int count = userLoginService.getLoginCount();
		return ResponseEntity.ok(count);
	}

	/**
	 * This method responds to the received request with the requested range of user
	 * logins.
	 * 
	 * @param count The count of user logins in the range
	 * @param page  The page of user logins
	 * @return The response with the requested user range
	 * @throws InvalidRequestException If the count/page is smaller than 0
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the user
	 */
	@GetMapping("/range/{count}/{page}")
	public ResponseEntity<List<UserLoginDto>> getRange(@PathVariable int count, @PathVariable int page) throws ServiceException {
		log.debug("Requested user login range (count=%s, page=%s)".formatted(count, page));
		List<UserLogin> entities = userLoginService.getLoginRange(count, page);
		List<UserLoginDto> range = entities.stream().map(userLoginMapper::toDto).toList();
		return ResponseEntity.ok(range);
	}

	/**
	 * This method creates the user with the given information to the database and
	 * will respond with the user id of the newly constructed user.
	 * 
	 * @param userLoginDto The DTO containing the user data
	 * @return The user id of the user login
	 * @throws ForbiddenOperationException If the user is logged in
	 * @throws InvalidRequestException     If the given username does not abide by
	 *                                     the requirements or if the given username
	 *                                     is not available
	 * @throws MissingValueException       If the username or password in the DTO
	 *                                     are missing
	 * @throws InternalErrorException      If a database error occurs while creating
	 *                                     the user
	 */
	@PostMapping("/create")
	public ResponseEntity<Integer> createUserLogin(@RequestBody UserLoginDto userLoginDto) throws ServiceException, MapperException {
		log.debug("Requested user login creation (userLoginDto=%s)".formatted(userLoginDto));
		UserLogin login = userLoginMapper.toEntity(userLoginDto);
		int userId = userLoginActionService.createLogin(login);
		return ResponseEntity.ok(userId);
	}

	/**
	 * This method responds to the received request with the current user's login.
	 * 
	 * @return The response with the requested user login
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws InternalErrorException         If a database occurs while retrieving
	 *                                        the user
	 */
	@GetMapping("/current")
	public ResponseEntity<UserLoginDto> getCurrentLogin() throws ServiceException {
		log.debug("Requested current user login");
		UserLogin entity = userLoginActionService.getCurrentLogin();
		UserLoginDto loginDto = userLoginMapper.toDto(entity);
		return ResponseEntity.ok(loginDto);
	}

	/**
	 * This method login the user with the given user credentials to the server.
	 * 
	 * @param userLoginDto The DTO containing the user credentials
	 * @throws ForbiddenOperationException If there is an active session
	 * @throws InvalidRequestException     If the username or password is incorrect
	 * @throws InternalErrorException      If a database error occurs while
	 *                                     authenticating
	 */
	@PostMapping("/login")
	public ResponseEntity<UserLoginDto> login(@RequestBody UserLoginDto userLoginDto) throws ServiceException {
		log.debug("Session login requested (userLoginDto=%s)".formatted(userLoginDto));
		String username = userLoginDto.getUsername();
		String password = userLoginDto.getPassword();
		UserLogin entity = userLoginActionService.authenticateUser(username, password);
		UserLoginDto loginDto = userLoginMapper.toDto(entity);
		return ResponseEntity.ok(loginDto);
	}

	/**
	 * This method logs out the currently logged in user from the server.
	 * 
	 * @throws UnauthorizedOperationException If there is no active user session
	 */
	@PostMapping("/logout")
	public ResponseEntity<Void> logout() throws ServiceException {
		log.debug("Session logout requested");
		userLoginActionService.logoutUser();
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method creates the user data with the given information in the database.
	 * 
	 * @param userDataDto The DTO containing the user data
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws InvalidRequestException        If the given first or last name are
	 *                                        blank or if the given user id is not
	 *                                        available
	 * @throws MissingValueException          If the first or last name in the DTO
	 *                                        are missing
	 * @throws InternalErrorException         If a database error occurs while
	 *                                        creating the user data
	 */
	@PostMapping("/data/create")
	public ResponseEntity<Void> createUserData(@RequestBody UserDataDto userDataDto) throws ServiceException, MapperException {
		log.debug("Requested user data creation (userDataDto=%s)".formatted(userDataDto));
		UserData data = userDataMapper.toEntity(userDataDto);
		userDataActionService.createData(data);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method responds to the received request with the user login with the
	 * given username.
	 * 
	 * @param username The username of the user login to retrieve
	 * @return The response with the requested user login
	 * @throws EntityNotFoundException If the given username does not correspond to
	 *                                 an user login
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the user
	 */
	@GetMapping("/u/{username}")
	public ResponseEntity<UserLoginDto> getUserLogin(@PathVariable String username) throws ServiceException {
		log.debug("Requested user login (username=%s)".formatted(username));
		UserLogin entity = userLoginService.getLoginByUsername(username);
		UserLoginDto loginDto = userLoginMapper.toDto(entity);
		return ResponseEntity.ok(loginDto);
	}

	/**
	 * This method responds to the received request with the user login with the
	 * given user id.
	 * 
	 * @param userId The user id of the user login to retrieve
	 * @return The response with the requested user login
	 * @throws EntityNotFoundException If the given user id does not correspond to
	 *                                 an user login
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the user
	 */
	@GetMapping("/id/{userId}")
	public ResponseEntity<UserLoginDto> getUserLogin(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user login (userId=%s)".formatted(userId));
		UserLogin entity = userLoginService.getLoginByUserId(userId);
		UserLoginDto loginDto = userLoginMapper.toDto(entity);
		return ResponseEntity.ok(loginDto);
	}

	/**
	 * This method deletes the user with the given user id from the database.
	 * 
	 * @param userId The user id of the user login to delete
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        given id
	 * @throws EntityNotFoundException        If the given user id does not
	 *                                        correspond to an user login
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        deleting the user
	 */
	@PostMapping("/id/{userId}/delete")
	public ResponseEntity<Void> deleteUserLogin(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user login deletion (userId=%s)".formatted(userId));
		userLoginActionService.deleteLogin(userId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method updates the user login with the given user id in the database.
	 * 
	 * @param userId       The user id of the user login to update
	 * @param userLoginDto The DTO containing the user login
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        same id as the one contained in the
	 *                                        given login
	 * @throws EntityNotFoundException        If the given user id does not
	 *                                        correspond to an user login
	 * @throws InvalidRequestException        If the given username does not abide
	 *                                        by the requirements
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        updating the user
	 */
	@PostMapping("/id/{userId}/update")
	public ResponseEntity<Void> updateUserLogin(@PathVariable int userId, @RequestBody UserLoginDto userLoginDto) throws ServiceException {
		log.debug("Requested user login update (userId=%s, userLoginDto=%s)".formatted(userId, userLoginDto));
		UserLogin original = userLoginService.getLoginByUserId(userId);
		UserLogin login = userLoginMapper.updateEntity(userLoginDto, original);
		userLoginActionService.updateLogin(login);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method responds to the received request with the list of inquiries that
	 * the user with the given user id has posted.
	 * 
	 * @param userId The id of the user to get the inquiries
	 * @return The response with the requested list of inquiries
	 * @throws EntityNotFoundException If the given user id does not correspond to a
	 *                                 user login
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the inquiry
	 */
	@GetMapping("/id/{userId}/inquiries")
	public ResponseEntity<List<InquiryDto>> getInquiries(@PathVariable int userId) throws ServiceException {
		log.debug("Requested posted inquiries (userId=%s)".formatted(userId));
		List<Inquiry> entities = inquiryActionService.getInquiryListByUserId(userId);
		List<InquiryDto> list = entities.stream().map(inquiryMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method responds to the received request with the list of inquiries that
	 * the user with the given user id is observing.
	 * 
	 * @param userId The id of the user of which to get the observed inquiries
	 * @return The response with the requested list of inquiries
	 * @throws EntityNotFoundException If the given user id does not correspond to a
	 *                                 user login
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the observed inquiries
	 */
	@GetMapping("/id/{userId}/observed")
	public ResponseEntity<List<InquiryDto>> getObservedInquiries(@PathVariable int userId) throws ServiceException {
		log.debug("Requested observed inquiries (userId=%s)".formatted(userId));
		List<Inquiry> entities = observerService.getObservedInquiryListByUserId(userId);
		List<InquiryDto> list = entities.stream().map(inquiryMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method responds to the received request with the list of responses that
	 * the user with the given user id has upvoted.
	 * 
	 * @param userId The id of the user of which to get the upvoted responses
	 * @return The response with the requested list of responses
	 * @throws EntityNotFoundException If the given user id does not correspond to a
	 *                                 user login
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the upvoted responses
	 */
	@GetMapping("/id/{userId}/upvotes")
	public ResponseEntity<List<ResponseDto>> getUpvotedResponses(@PathVariable int userId) throws ServiceException {
		log.debug("Requested upvoted responses (userId=%s)".formatted(userId));
		List<Response> entities = upvoteService.getUpvotedResponseListByUserId(userId);
		List<ResponseDto> list = entities.stream().map(responseMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method responds to the received request with the user data with the
	 * given user id.
	 * 
	 * @param userId The user id of the user data to retrieve
	 * @return The response with the requested user data
	 * @throws EntityNotFoundException If the given id does not correspond to user
	 *                                 data
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the user data
	 */
	@GetMapping("/id/{userId}/data")
	public ResponseEntity<UserDataDto> getUserData(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user data (userId=%s)".formatted(userId));
		UserData entity = userDataService.getDataByUserId(userId);
		UserDataDto dataDto = userDataMapper.toDto(entity);
		return ResponseEntity.ok(dataDto);
	}

	/**
	 * This method updates the user data with the given user id in the database.
	 * 
	 * @param userId      The user id of the user data to update
	 * @param userDataDto The DTO containing the user data
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        given user id
	 * @throws EntityNotFoundException        If the given id does not correspond to
	 *                                        user data
	 * @throws InvalidRequestException        If the given first or last name are
	 *                                        blank or if the given id is not
	 *                                        available
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        updating the user data
	 */
	@PostMapping("/id/{userId}/data/update")
	public ResponseEntity<Void> updateUserData(@PathVariable int userId, @RequestBody UserDataDto userDataDto) throws ServiceException {
		log.debug("Requested user data update (userId=%s, userDataDto=%s)".formatted(userId, userDataDto));
		UserData original = userDataService.getDataByUserId(userId);
		UserData data = userDataMapper.updateEntity(userDataDto, original);
		userDataActionService.updateData(data);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method deletes the user data with the given user id from the database.
	 * 
	 * @param userId The user id of the user data to delete
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        given user id
	 * @throws EntityNotFoundException        If the given id does not correspond to
	 *                                        user data
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        deleting the user data
	 */
	@PostMapping("/id/{userId}/data/delete")
	public ResponseEntity<Void> deleteUserData(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user data deletion (userId=%s)".formatted(userId));
		userDataActionService.deleteData(userId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method responds to the received request with the user icon with the
	 * given user id.
	 * 
	 * @param userId The user id of the user icon to retrieve
	 * @return The response with the requested user icon
	 * @throws EntityNotFoundException If the given id does not correspond to user
	 *                                 data
	 * @throws InternalErrorException  If a database error occurs while retrieving
	 *                                 the user data
	 */
	@GetMapping("/id/{userId}/data/icon")
	public ResponseEntity<byte[]> getUserIcon(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user icon (userId=%s)".formatted(userId));
		UserData entity = userDataService.getDataByUserId(userId);
		byte[] image = entity.getIcon();
		HttpHeaders headers = new HttpHeaders();
		if (image == null) {
			headers.setLocation(URI.create("/static/user-default.png"));
			return new ResponseEntity<>(headers, HttpStatus.FOUND);
		} else {
			headers.setContentType(MediaType.IMAGE_PNG);
			return new ResponseEntity<>(image, headers, HttpStatus.OK);
		}
	}

	/**
	 * This method updates the user icon with the given user id in the database.
	 * 
	 * @param userId The user id of the user icon to update
	 * @param icon   The user icon binary data
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        given user id
	 * @throws EntityNotFoundException        If the given id does not correspond to
	 *                                        user data
	 * @throws InvalidRequestException        If the given first or last name are
	 *                                        blank
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        updating the user data
	 */
	@PostMapping("/id/{userId}/data/icon/update")
	public ResponseEntity<Void> updateUserIcon(@PathVariable int userId, @RequestBody byte[] icon) throws ServiceException {
		log.debug("Requested user icon update (userId=%s)".formatted(userId));
		UserData original = userDataService.getDataByUserId(userId);
		UserData data = original.toBuilder().icon(icon).build();
		userDataActionService.updateData(data);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method deletes the user icon with the given user id from the database.
	 * 
	 * @param userId The user id of the user icon to delete
	 * @throws UnauthorizedOperationException If the current user is not logged in
	 * @throws ForbiddenOperationException    If the current user does not have the
	 *                                        given user id
	 * @throws EntityNotFoundException        If the given id does not correspond to
	 *                                        user data
	 * @throws InvalidRequestException        If the given first or last name are
	 *                                        blank
	 * @throws InternalErrorException         If a database error occurred while
	 *                                        updating the user data
	 */
	@PostMapping("/id/{userId}/data/icon/delete")
	public ResponseEntity<Void> deleteUserIcon(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user icon deletion (userId=%s)".formatted(userId));
		UserData original = userDataService.getDataByUserId(userId);
		UserData data = original.toBuilder().icon(null).build();
		userDataActionService.updateData(data);
		return ResponseEntity.noContent().build();
	}

}
