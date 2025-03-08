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
 * This class contains the inquiry data to be transfered between the client and
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
@Schema(description = "Represents an inquiry")
public class InquiryDto {

	/**
	 * The unique id of the inquiry
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(description = "Unique inquiry id", example = "1")
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
	 * The title of the inquiry
	 */
	@Schema(description = "Inquiry title", example = "Example title")
	private String title;

	/**
	 * The content of the inquiry
	 */
	@Schema(description = "Inquiry content", example = "This is an example inquiry content")
	private String content;

	/**
	 * Whether the author is anonymous
	 */
	@Schema(description = "Inquiry anonymous flag", example = "false")
	private Boolean anonymous;

}
