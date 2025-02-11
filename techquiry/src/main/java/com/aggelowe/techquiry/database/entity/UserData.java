package com.aggelowe.techquiry.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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
@ToString
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
	@ToString.Exclude
	private byte[] icon;

}
