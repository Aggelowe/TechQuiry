package com.aggelowe.techquiry.database.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The {@link Response} class represents an response entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class Response {

	/**
	 * The unique id of the response
	 */
	@NonNull
	private Integer responseId;

	/**
	 * The id of the parent inquiry
	 */
	@NonNull
	private Integer inquiryId;

	/**
	 * The user id of the author
	 */
	@NonNull
	@ToString.Exclude
	private Integer userId;

	/**
	 * Whether the author is anonymous
	 */
	@NonNull
	private Boolean anonymous;

	/**
	 * The content of the response
	 */
	@NonNull
	private String content;

}
