package com.aggelowe.techquiry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Represents a user login")
public class UserLoginDto {

	/**
	 * The unique id of the user
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(description = "Unique user id", example = "1")
	private Integer userId;

	/**
	 * The unique username of the user
	 */
	@Schema(description = "User username", example = "alice")
	private String username;

	/**
	 * The password of the user in plaintext
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ToString.Exclude
	@Schema(description = "User password", example = "password")
	private String password;

}
