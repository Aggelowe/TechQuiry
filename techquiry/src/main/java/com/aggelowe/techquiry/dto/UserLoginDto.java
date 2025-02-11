package com.aggelowe.techquiry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class contains the user login data to be transfered between the client
 * and server.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class UserLoginDto {

	/**
	 * The unique id of the user
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer userId;

	/**
	 * The unique username of the user
	 */
	private String username;

	/**
	 * The password of the user in plaintext
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ToString.Exclude
	private String password;

}
