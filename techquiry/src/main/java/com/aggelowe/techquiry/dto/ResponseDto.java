package com.aggelowe.techquiry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ResponseDto {

	/**
	 * The unique id of the response
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer responseId;

	/**
	 * The id of the parent inquiry
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer inquiryId;

	/**
	 * The user id of the author
	 */
	@ToString.Exclude
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer userId;

	/**
	 * Whether the author is anonymous
	 */
	private Boolean anonymous;

	/**
	 * The content of the response
	 */
	private String content;

}
