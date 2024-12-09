package com.aggelowe.techquiry.database.entities;

import com.aggelowe.techquiry.common.SecurityUtils;

/**
 * The {@link UserLogin} class represents a user login of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
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
	 * @param id           The unique user id
	 * @param username     The unique username
	 * @param passwordHash The hash of the user's password
	 * @param passwordSalt The salt used in the password hash
	 */
	public UserLogin(int id, String username, byte[] passwordHash, byte[] passwordSalt) {
		this.id = id;
		this.username = username;
		this.passwordHash = passwordHash;
		this.passwordSalt = passwordSalt;
	}

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
	 * This method returns the unique id of the user
	 * 
	 * @return The user's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * This method returns the unique username of the user
	 * 
	 * @return The user's username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * This method returns the hashed password of the user
	 * 
	 * @return The user's password hash
	 */
	public byte[] getPasswordHash() {
		return passwordHash;
	}

	/**
	 * This method returns the salt used in the password hash of the user
	 * 
	 * @return The user password's salt
	 */
	public byte[] getPasswordSalt() {
		return passwordSalt;
	}

	/**
	 * This method sets the username of the user
	 * 
	 * @param username The user's username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * This method sets the hashed password of the user
	 * 
	 * @param passwordHash The user's password hash
	 */
	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}

	/**
	 * This method sets the salt used in the password hash of the user
	 * 
	 * @param passwordSalt The user password's salt
	 */
	public void setPasswordSalt(byte[] passwordSalt) {
		this.passwordSalt = passwordSalt;
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
