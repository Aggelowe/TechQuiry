package com.aggelowe.techquiry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class contains the user data to be transfered between the client and
 * server.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Schema(description = "Represents user data")
public class UserDataDto {

	/**
	 * The unique id of the user
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(description = "Unique user id", example = "1")
	private Integer userId;

	/**
	 * The first name of the user
	 */
	@Schema(description = "User first name", example = "Alice")
	private String firstName;

	/**
	 * The last name of the user
	 */
	@Schema(description = "User last name", example = "Smith")
	private String lastName;

}
