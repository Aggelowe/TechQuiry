package com.aggelowe.techquiry.entity;

import com.aggelowe.techquiry.common.SecurityUtils;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The {@link UserLogin} class represents a user login of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class UserLogin {

	/**
	 * The unique id of the user
	 */
	@NonNull
	private Integer userId;

	/**
	 * The unique username chosen by the user
	 */
	@NonNull
	private String username;

	/**
	 * The hashed password of the user
	 */
	@NonNull
	@ToString.Exclude
	private byte[] passwordHash;

	/**
	 * The random salt used in hashing the password
	 */
	@NonNull
	@ToString.Exclude
	private byte[] passwordSalt;

	/**
	 * This constructor constructs a new {@link UserLogin} instance with the
	 * provided parameters as the required user login information.
	 * 
	 * @param id       The unique user id
	 * @param username The unique username
	 * @param password The user's password
	 */
	public UserLogin(int id, String username, String password) {
		this.userId = id;
		this.username = username;
		this.passwordSalt = SecurityUtils.generateSalt();
		this.passwordHash = SecurityUtils.hashPassword(password, passwordSalt);
	}

}
