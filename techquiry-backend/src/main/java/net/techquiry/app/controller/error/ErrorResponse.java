package net.techquiry.app.controller.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class contains the error data to be transfered from the server to the
 * client.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Schema(description = "Contains server error data")
public class ErrorResponse {

	/**
	 * The error status code.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(description = "HTTP error code", example = "500")
	private Integer status;

	/**
	 * The error message.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(description = "Error message", example = "Example error occured!")
	private String message;

}
