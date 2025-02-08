package com.aggelowe.techquiry.database.entity;

import com.aggelowe.techquiry.common.SecurityUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The {@link UserData} class represents a user data entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class UserData {

	/**
	 * The unique id of the user
	 */
	@NonNull
	private Integer userId;

	/**
	 * The first name of the user
	 */
	@NonNull
	private String firstName;

	/**
	 * The last name of the user
	 */
	@NonNull
	private String lastName;

	/**
	 * The icon of the user's profile
	 */
	private byte[] icon;

	/**
	 * This method returns the object as a string containing the user id, the first
	 * name, the last name and the profile icon.
	 */
	@Override
	public String toString() {
		return "UserData(id=" + userId + 
				", firstName=" + firstName + 
				", lastName=" + lastName + 
				", icon=" + (icon == null ? "null" : SecurityUtils.encodeBase64(icon)) + ")";
	}

}
