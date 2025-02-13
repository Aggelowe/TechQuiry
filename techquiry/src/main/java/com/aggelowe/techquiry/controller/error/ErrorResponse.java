package com.aggelowe.techquiry.controller.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class contains the error data to be transfered from the client to the
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
public class ErrorResponse {

	/**
	 * The error status code
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer status;

	/**
	 * The error message
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String message;

}
