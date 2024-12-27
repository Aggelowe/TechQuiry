package com.aggelowe.techquiry.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The {@link Observer} class represents an observer entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
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
	 * This method returns the object as a string containing the inquiry id and the
	 * observer's user id.
	 */
	@Override
	public String toString() {
		return "[Inquiry ID: " + inquiryId +
				", User ID: " + userId + "]";
	}

}
