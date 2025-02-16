package com.aggelowe.techquiry.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.entity.Observer;
import com.aggelowe.techquiry.database.entity.Response;
import com.aggelowe.techquiry.database.entity.Upvote;
import com.aggelowe.techquiry.database.entity.UserData;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.dto.InquiryDto;
import com.aggelowe.techquiry.dto.ResponseDto;
import com.aggelowe.techquiry.dto.UserDataDto;
import com.aggelowe.techquiry.dto.UserLoginDto;
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
import com.aggelowe.techquiry.service.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link UserController} class manages HTTP requests and responses for user
 * operations in the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Controller
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
	 * The service responsible for managing personal {@link UserLogin} operations in
	 * the TechQuiry application.
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
	 * The service responsible for managing personal {@link UserData} operations in
	 * the TechQuiry application.
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
	 * This method will respond to the received request with the number of user
	 * logins in the database.
	 * 
	 * @return The response with the user count.
	 * @throws ServiceException If an error occurs while getting the login count
	 */
	@PostMapping("/count")
	public ResponseEntity<Integer> getCount() throws ServiceException {
		log.debug("Requested user login count");
		int count = userLoginService.getLoginCount();
		return ResponseEntity.ok(count);
	}

	/**
	 * This method will respond to the received request with the requested range of
	 * user logins.
	 * 
	 * @param count The count of user logins in the range
	 * @param page  The page of user logins
	 * @return The response with the requested user range.
	 * @throws ServiceException If an exception occurs while getting the range
	 */
	@PostMapping("/range/{count}/{page}")
	public ResponseEntity<List<UserLoginDto>> getRange(@PathVariable int count, @PathVariable int page) throws ServiceException {
		log.debug("Requested " + count + " user login range at page " + page);
		List<UserLogin> entities = userLoginService.getLoginRange(count, page);
		List<UserLoginDto> range = entities.stream().map(userLoginMapper::toDto).toList();
		return ResponseEntity.ok(range);
	}

	/**
	 * This method will create the user with the given information to the database
	 * and will respond with the id of the newly constructed user.
	 * 
	 * @param userLoginDto The DTO containing the user data
	 * @return The user id of the user login
	 * @throws ServiceException If an exception occurs while creating the user login
	 * @throws MapperException  If the required data contained in the received user
	 *                          login DTO are missing.
	 */
	@PostMapping("/create")
	public ResponseEntity<Integer> createUserLogin(@RequestBody UserLoginDto userLoginDto) throws ServiceException, MapperException {
		log.debug("Requested user login creation with " + userLoginDto);
		UserLogin login = userLoginMapper.toEntity(userLoginDto);
		int userId = userLoginActionService.createLogin(login);
		return ResponseEntity.ok(userId);
	}

	/**
	 * This method will login to the server with the given user credentials.
	 * 
	 * @param userLoginDto The DTO containing the user credentials
	 * @throws ServiceException      If an exception occurs while authenticating
	 * @throws MissingValueException If the data provided to the method are
	 *                               incomplete
	 */
	@PostMapping("/login")
	public ResponseEntity<Integer> login(@RequestBody UserLoginDto userLoginDto) throws ServiceException {
		log.debug("Login requested with credentials " + userLoginDto);
		String username = userLoginDto.getUsername();
		String password = userLoginDto.getPassword();
		int userId = userLoginActionService.authenticateUser(username, password);
		return ResponseEntity.ok(userId);
	}

	/**
	 * This method will logout the currently logged in user from the server.
	 * 
	 * @throws ServiceException If an exception occurs while logging out
	 */
	@PostMapping("/logout")
	public ResponseEntity<Void> logout() throws ServiceException {
		log.debug("Logout requested");
		userLoginActionService.logoutUser();
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will respond to the received request with the user login with the
	 * given username.
	 * 
	 * @param username The username of the user login to select
	 * @return The response with the requested user login
	 * @throws ServiceException If an exception occurs while getting the user login
	 */
	@PostMapping("/u/{username}")
	public ResponseEntity<UserLoginDto> getUserLogin(@PathVariable String username) throws ServiceException {
		log.debug("Requested user login with username " + username);
		UserLogin entity = userLoginService.getLoginByUsername(username);
		UserLoginDto loginDto = userLoginMapper.toDto(entity);
		return ResponseEntity.ok(loginDto);
	}

	/**
	 * This method will respond to the received request with the user login with the
	 * given user id.
	 * 
	 * @param userId The user id of the user login to select
	 * @return The response with the requested user login
	 * @throws ServiceException If an exception occurs while getting the user login
	 */
	@PostMapping("/id/{userId}")
	public ResponseEntity<UserLoginDto> getUserLogin(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user login with id " + userId);
		UserLogin entity = userLoginService.getLoginByUserId(userId);
		UserLoginDto loginDto = userLoginMapper.toDto(entity);
		return ResponseEntity.ok(loginDto);
	}

	/**
	 * This method will delete the user with the given user id from the server.
	 * 
	 * @param userId The user id of the user login to delete
	 * @throws ServiceException If an exception occurs while deleting the user
	 */
	@PostMapping("/id/{userId}/delete")
	public ResponseEntity<Void> deleteUserLogin(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user login deletion with user id " + userId);
		userLoginActionService.deleteLogin(userId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will update the user login with the given user id in the server.
	 * 
	 * @param userId       The user id of the user login to update
	 * @param userLoginDto The DTO containing the user login
	 * @throws ServiceException If an exception occurs while deleting the user
	 */
	@PostMapping("/id/{userId}/update")
	public ResponseEntity<Void> updateUserLogin(@PathVariable int userId, @RequestBody UserLoginDto userLoginDto) throws ServiceException {
		log.debug("Requested user login update with user id " + userId + " and data " + userLoginDto);
		UserLogin original = userLoginService.getLoginByUserId(userId);
		UserLogin login = userLoginMapper.updateEntity(userLoginDto, original);
		userLoginActionService.updateLogin(login);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will respond to the received request with the list of inquiries
	 * that the user with the given user id has posted.
	 * 
	 * @param userId The id of the user to get the inquiries
	 * @return The response with the requested list of inquiries
	 * @throws ServiceException If an exception occurs while getting the inquiries
	 */
	@PostMapping("/id/{userId}/inquiries")
	public ResponseEntity<List<InquiryDto>> getInquiries(@PathVariable int userId) throws ServiceException {
		log.debug("Requested posted inquiries of user " + userId);
		List<Inquiry> entities = inquiryActionService.getInquiryListByUserId(userId);
		List<InquiryDto> list = entities.stream().map(inquiryMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method will respond to the received request with the list of inquiries
	 * that the user with the given user id is observing.
	 * 
	 * @param userId The id of the user to get the observed inquiries
	 * @return The response with the requested list of inquiries
	 * @throws ServiceException If an exception occurs while getting the inquiries
	 */
	@PostMapping("/id/{userId}/observed")
	public ResponseEntity<List<InquiryDto>> getObservedInquiries(@PathVariable int userId) throws ServiceException {
		log.debug("Requested observed inquiries of user " + userId);
		List<Inquiry> entities = observerService.getObservedInquiryListByUserId(userId);
		List<InquiryDto> list = entities.stream().map(inquiryMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method will respond to the received request with the list of responses
	 * that the user with the given user id has upvoted.
	 * 
	 * @param userId The id of the user to get the upvoted responses
	 * @return The response with the requested list of responses
	 * @throws ServiceException If an exception occurs while getting the responses
	 */
	@PostMapping("/id/{userId}/upvotes")
	public ResponseEntity<List<ResponseDto>> getUpvotedResponses(@PathVariable int userId) throws ServiceException {
		log.debug("Requested upvoted responses of user " + userId);
		List<Response> entities = upvoteService.getUpvotedResponseListByUserId(userId);
		List<ResponseDto> list = entities.stream().map(responseMapper::toDto).toList();
		return ResponseEntity.ok(list);
	}

	/**
	 * This method will create the user data with the given information and user id
	 * in the database.
	 * 
	 * @param userDataDto The DTO containing the user data
	 * @throws ServiceException      If an exception occurs while creating the user
	 *                               login
	 * @throws MapperException       If the required data contained in the received
	 *                               user data DTO are missing
	 * @throws MissingValueException If the data provided to the method are
	 *                               incomplete
	 */
	@PostMapping("/data/create")
	public ResponseEntity<Void> createUserData(@RequestBody UserDataDto userDataDto) throws ServiceException, MapperException {
		log.debug("Requested user data creation with " + userDataDto);
		UserData data = userDataMapper.toEntity(userDataDto);
		userDataActionService.createData(data);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will respond to the received request with the user data with the
	 * given user id.
	 * 
	 * @param userId The user id of the user data to select
	 * @return The response with the requested user data
	 * @throws ServiceException If an exception occurs while getting the user data
	 */
	@PostMapping("/data/id/{userId}")
	public ResponseEntity<UserDataDto> getUserData(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user data with id " + userId);
		UserData entity = userDataService.getDataByUserId(userId);
		UserDataDto dataDto = userDataMapper.toDto(entity);
		return ResponseEntity.ok(dataDto);
	}

	/**
	 * This method will update the user data with the given user id in the server.
	 * 
	 * @param userId      The user id of the user data to update
	 * @param userDataDto The DTO containing the user data
	 * @throws ServiceException If an exception occurs while updating the user data
	 */
	@PostMapping("/data/id/{userId}/update")
	public ResponseEntity<Void> updateUserData(@PathVariable int userId, @RequestBody UserDataDto userDataDto) throws ServiceException {
		log.debug("Requested user data update with user id " + userId + " and data " + userDataDto);
		UserData original = userDataService.getDataByUserId(userId);
		UserData data = userDataMapper.updateEntity(userDataDto, original);
		userDataActionService.updateData(data);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will delete the user data with the given user id from the server.
	 * 
	 * @param userId The user id of the user data to delete
	 * @throws ServiceException If an exception occurs while deleting the user data
	 */
	@PostMapping("/data/id/{userId}/delete")
	public ResponseEntity<Void> deleteUserData(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user data deletion with user id " + userId);
		userDataActionService.deleteData(userId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will respond to the received request with the user icon with the
	 * given user id.
	 * 
	 * @param userId The user id of the user data to select
	 * @return The response with the requested user icon
	 * @throws ServiceException If an exception occurs while getting the user icon
	 */
	@GetMapping("/icon/id/{userId}")
	public ResponseEntity<byte[]> getUserIcon(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user icon with id " + userId);
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
	 * This method will update the user icon with the given user id in the server.
	 * 
	 * @param userId The user id of the user icon to update
	 * @param icon   The user icon binary data
	 * @throws ServiceException If an exception occurs while updating the user icon
	 */
	@PostMapping("/icon/id/{userId}/update")
	public ResponseEntity<Void> updateUserIcon(@PathVariable int userId, @RequestBody byte[] icon) throws ServiceException {
		log.debug("Requested user icon update with user id " + userId);
		UserData original = userDataService.getDataByUserId(userId);
		UserData data = original.toBuilder().icon(icon).build();
		userDataActionService.updateData(data);
		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will delete the user icon with the given user id from the server.
	 * 
	 * @param userId The user id of the user icon to delete
	 * @throws ServiceException If an exception occurs while deleting the user icon
	 */
	@PostMapping("/icon/id/{userId}/delete")
	public ResponseEntity<Void> deleteUserIcon(@PathVariable int userId) throws ServiceException {
		log.debug("Requested user icon deletion with user id " + userId);
		UserData original = userDataService.getDataByUserId(userId);
		UserData data = original.toBuilder().icon(null).build();
		userDataActionService.updateData(data);
		return ResponseEntity.noContent().build();
	}

}
