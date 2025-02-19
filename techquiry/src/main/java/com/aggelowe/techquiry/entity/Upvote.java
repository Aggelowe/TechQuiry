package com.aggelowe.techquiry.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The {@link Upvote} class represents an upvote entry of the TechQuiry
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
public class Upvote {

	/**
	 * The response id of the response
	 */
	@NonNull
	private Integer responseId;

	/**
	 * The user id of the upvoter
	 */
	@NonNull
	private Integer userId;

}
