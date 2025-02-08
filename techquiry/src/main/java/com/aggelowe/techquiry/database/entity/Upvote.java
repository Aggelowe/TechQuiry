package com.aggelowe.techquiry.database.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The {@link Upvote} class represents an upvote entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Upvote {

	/**
	 * The response id of the response
	 */
	private final int responseId;

	/**
	 * The user id of the upvoter
	 */
	private final int userId;

}
