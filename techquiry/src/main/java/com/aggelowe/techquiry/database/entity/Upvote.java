package com.aggelowe.techquiry.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The {@link Upvote} class represents an upvote entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
public class Upvote {

	/**
	 * The response id of the response
	 */
	private final int responseId;

	/**
	 * The user id of the upvoter
	 */
	private final int userId;
	
	/**
	 * This method returns the object as a string containing the response id and the
	 * upvoter's user id.
	 */
	@Override
	public String toString() {
		return "[Response ID: " + responseId
				+ ", User ID: " + userId + "]";
	}

}
