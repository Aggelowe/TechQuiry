package com.aggelowe.techquiry.database.entities;

/**
 * The {@link Upvote} class represents an upvote entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
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
	 * This constructor constructs a new {@link Upvote} instance with the provided
	 * parameters as the required upvote information.
	 * 
	 * @param responseId The response id
	 * @param userId     The upvoter's user id
	 */
	public Upvote(int responseId, int userId) {
		this.responseId = responseId;
		this.userId = userId;
	}

	/**
	 * This method returns the id of the upvoted response
	 * 
	 * @return The response's id
	 */
	public int getResponseId() {
		return responseId;
	}

	/**
	 * This method returns the user id of the upvoter
	 * 
	 * @return The user id
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * This method returns the object as a string containing the response id and the
	 * upvoter's user id.
	 */
	@Override
	public String toString() {
		return "[Response ID: " + responseId + ", User ID: " + userId + "]";
	}

}
