package com.aggelowe.techquiry.database.entities;

import com.aggelowe.techquiry.common.Utilities;
import com.aggelowe.techquiry.database.exceptions.EntityException;

/**
 * The {@link UserData} class represents a user data entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
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
		if (firstName == null || lastName == null) {
			throw new EntityException("The provided user data information should not be NULL!");
		}
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
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
	 * This method returns the first name of the user
	 * 
	 * @return The user's first name
	 */
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * This method returns the last name of the user
	 * 
	 * @return The user's last name
	 */
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * This method returns the icon of the user
	 * 
	 * @return The user's icon
	 */
	public byte[] getIcon() {
		return icon;
	}
	
	/**
	 * This method sets the first name of the user
	 * 
	 * @param firstName The user's first name
	 */
	public void setFirstName(String firstName) {
		if (firstName == null) {
			throw new EntityException("The provided first name should not be NULL!");
		}
		this.firstName = firstName;
	}

	/**
	 * This method sets the last name of the user
	 * 
	 * @param lastName The user's last name
	 */
	public void setLastName(String lastName) {
		if (lastName == null) {
			throw new EntityException("The provided last name should not be NULL!");
		}
		this.lastName = lastName;
	}
	
	/**
	 * This method sets the profile icon of the user
	 * 
	 * @param icon The user's profile icon
	 */
	public void setIcon(byte[] icon) {
		this.icon = icon;
	}

	/**
	 * This method returns the object as a string containing the user id, the
	 * first name, the last name and the profile icon.
	 */
	@Override
	public String toString() {
		return "[User ID: " + id +
				", First Name: " + firstName +
				", Last Name: " + lastName +
				", Icon: " + (icon == null ? "NULL" : Utilities.encodeBase64(icon)) + "]";
	}

}
