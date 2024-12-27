package com.aggelowe.techquiry.database.entities;

import com.aggelowe.techquiry.common.SecurityUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@link UserData} class represents a user data entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@Setter
@AllArgsConstructor
public class UserData {

	/**
	 * The unique id of the user
	 */
	private final int id;

	/**
	 * The first name of the user
	 */
	private String firstName;

	/**
	 * The last name of the user
	 */
	private String lastName;

	/**
	 * The icon of the user's profile
	 */
	private byte[] icon;

	/**
	 * This constructor constructs a new {@link UserData} instance with the provided
	 * parameters as the required user data information.
	 * 
	 * @param id           The unique user id
	 * @param username     The user's first name
	 * @param passwordHash The user's last name
	 */
	public UserData(int id, String firstName, String lastName) {
		this(id, firstName, lastName, null);
	}
	
	/**
	 * This method returns the last name of the user
	/**
	 * This method returns the object as a string containing the user id, the
	 * first name, the last name and the profile icon.
	 */
	@Override
	public String toString() {
		return "[User ID: " + id +
				", First Name: " + firstName +
				", Last Name: " + lastName +
				", Icon: " + (icon == null ? "NULL" : SecurityUtils.encodeBase64(icon)) + "]";
	}

}
