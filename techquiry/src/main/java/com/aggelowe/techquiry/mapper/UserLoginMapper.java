package com.aggelowe.techquiry.mapper;

import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.common.SecurityUtils;
import com.aggelowe.techquiry.dto.UserLoginDto;
import com.aggelowe.techquiry.entity.UserLogin;
import com.aggelowe.techquiry.entity.UserLogin.UserLoginBuilder;
import com.aggelowe.techquiry.mapper.exception.MapperException;
import com.aggelowe.techquiry.mapper.exception.MissingValueException;

/**
 * The {@link UserLoginMapper} class is responsible for mapping between
 * {@link UserLogin} and {@link UserLoginDto} objects.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
public class UserLoginMapper {

	/**
	 * This method maps the given {@link UserLogin} to a {@link UserLoginDto}
	 * object.
	 * 
	 * @param userLogin The user login entity to map
	 * @return The user login DTO
	 */
	public UserLoginDto toDto(UserLogin userLogin) {
		Integer userId = userLogin.getUserId();
		String username = userLogin.getUsername();
		return UserLoginDto.builder().userId(userId).username(username).build();
	}

	/**
	 * This method creates a new {@link UserLogin} object based on the data of the
	 * given {@link UserLoginDto}.
	 * 
	 * @param userLoginDto The data transfer object to map
	 * @return The new user login entity
	 * @throws MissingValueException If the username or password in the DTO are
	 *                               missing
	 */
	public UserLogin toEntity(UserLoginDto userLoginDto) throws MapperException {
		String username = userLoginDto.getUsername();
		String password = userLoginDto.getPassword();
		if (username == null || password == null) {
			throw new MissingValueException("The username and/or password value is missing!");
		}
		byte[] passwordSalt = SecurityUtils.generateSalt();
		byte[] passwordHash = SecurityUtils.hashPassword(password, passwordSalt);
		return new UserLogin(0, username, passwordHash, passwordSalt);
	}

	/**
	 * This method creates a new {@link UserLogin} object whose data are a copy of
	 * the original entity and whose data are changed according to the given
	 * {@link UserLoginDto}.
	 * 
	 * @param userLoginDto The data transfer object to map
	 * @param original     The entity to draw the original data from
	 * @return The new user login entity
	 */
	public UserLogin updateEntity(UserLoginDto userLoginDto, UserLogin original) {
		String username = userLoginDto.getUsername();
		String password = userLoginDto.getPassword();
		UserLoginBuilder builder = original.toBuilder();
		if (username != null) {
			builder.username(username);
		}
		if (password != null) {
			byte[] salt = SecurityUtils.generateSalt();
			byte[] hash = SecurityUtils.hashPassword(password, salt);
			builder.passwordSalt(salt).passwordHash(hash);
		}
		return builder.build();
	}

}
