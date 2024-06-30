package com.aggelowe.techquiry.database.entities;

/**
 * The {@link Observer} class represents an observer entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class Observer {

	/**
	 * The inquiry id of the inquiry
	 */
	private final int inquiryId;

	/**
	 * The user id of the observer
	 */
	private final int userId;

	/**
	 * This constructor constructs a new {@link Observer} instance with the provided
	 * parameters as the required observer information.
	 * 
	 * @param inquiryId The inquiry id
	 * @param userId    The observer's user id
	 */
	public Observer(int inquiryId, int userId) {
		this.inquiryId = inquiryId;
		this.userId = userId;
	}

	/**
	 * This method returns the id of the observed inquiry
	 * 
	 * @return The inquiry's id
	 */
	public int getInquiryId() {
		return inquiryId;
	}

	/**
	 * This method returns the user id of the observer
	 * 
	 * @return The observer's user id
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * This method returns the object as a string containing the inquiry id and the
	 * observer's user id.
	 */
	@Override
	public String toString() {
		return "[Inquiry ID: " + inquiryId +
				", User ID: " + userId + "]";
	}

}
