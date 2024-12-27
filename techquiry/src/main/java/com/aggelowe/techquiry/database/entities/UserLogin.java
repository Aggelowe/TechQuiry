package com.aggelowe.techquiry.database.entities;

import com.aggelowe.techquiry.common.SecurityUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
public class UserLogin {

	/**
	 * The unique id of the user
	 */
	private final int id;

	/**
	 * The unique username chosen by the user
	 */
	private String username;

	/**
	 * The hashed password of the user
	 */
	private byte[] passwordHash;

	/**
	 * The random salt used in hashing the password
	 */
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
	 * This method returns the object as a string containing the user id, the
	 * username, the password hash and the password salt.
	 */
	@Override
	public String toString() {
		return "[User ID: " + id +
				", Username: " + username +
				", Password Hash: " + SecurityUtils.encodeBase64(passwordHash) +
				", Password Salt: " + SecurityUtils.encodeBase64(passwordSalt) + "]";
	}

}
