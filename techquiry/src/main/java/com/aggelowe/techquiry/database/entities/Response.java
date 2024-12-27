package com.aggelowe.techquiry.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@link Response} class represents an response entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@Setter
@AllArgsConstructor
public class Response {

	/**
	 * The unique id of the response
	 */
	private final int id;

	/**
	 * The id of the parent inquiry
	 */
	private int inquiryId;

	/**
	 * The user id of the author
	 */
	private int userId;

	/**
	 * Whether the author is anonymous
	 */
	private boolean anonymous;

	/**
	 * The content of the response
	 */
	private String content;

	/**
	 * This method returns the object as a string containing the response id, the
	 * parent inquiry's id, the author's user id, the content and whether the author
	 * is anonymous.
	 */
	@Override
	public String toString() {
		return "[Response ID: " + id
				+ ", Inquiry ID: " + inquiryId
				+ ", User ID: " + (anonymous ? "REDACTED" : userId)
				+ ", Anonymous: " + anonymous
				+ ", Content: " + content + "]";
	}

}
