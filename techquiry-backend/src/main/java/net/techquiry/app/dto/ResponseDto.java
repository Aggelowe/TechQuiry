package net.techquiry.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class contains the response data to be transfered between the client and
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
@Schema(description = "Represents a response")
public class ResponseDto {

	/**
	 * The unique id of the response
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(description = "Unique response id", example = "1")
	private Integer responseId;

	/**
	 * The id of the parent inquiry
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(description = "Parent inquiry id", example = "1")
	private Integer inquiryId;

	/**
	 * The user id of the author
	 */
	@ToString.Exclude
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "Author user id", example = "1")
	private Integer userId;

	/**
	 * Whether the author is anonymous
	 */
	@Schema(description = "Response anonymous flag", example = "false")
	private Boolean anonymous;

	/**
	 * The content of the response
	 */
	@Schema(description = "Response content", example = "This is an example response content")
	private String content;

}
