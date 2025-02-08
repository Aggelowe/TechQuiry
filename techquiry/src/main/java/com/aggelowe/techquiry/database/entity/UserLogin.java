package com.aggelowe.techquiry.database.entity;

import com.aggelowe.techquiry.common.SecurityUtils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@link UserLogin} class represents a user login of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserLogin {

	/**
	 * The unique id of the user
	 */
	private int id;

	/**
	 * The unique username chosen by the user
	 */
	private String username;

	/**
	 * The hashed password of the user
	 */
	@ToString.Exclude
	private byte[] passwordHash;

	/**
	 * The random salt used in hashing the password
	 */
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
		this.id = id;
		this.username = username;
		this.passwordSalt = SecurityUtils.generateSalt();
		this.passwordHash = SecurityUtils.hashPassword(password, passwordSalt);
	}

	/**
	 * This method creates a new shallow copy of the current {@link UserLogin}
	 * object.
	 */
	public UserLogin copy() {
		return new UserLogin(id, username, passwordHash, passwordSalt);
	}

}
