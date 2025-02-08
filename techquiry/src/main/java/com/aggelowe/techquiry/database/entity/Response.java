package com.aggelowe.techquiry.database.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
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
	    return "Response(id=" + id +
	    		", inquiryId=" + inquiryId +
	    		(anonymous ? "" : ", userId=" + userId) +
	    		", anonymous=" + anonymous +
	    		", content=" + content + ")";
	}

	/**
	 * This method creates a new shallow copy of the current {@link Response}
	 * object.
	 */
	public Response copy() {
		return new Response(id, inquiryId, userId, anonymous, content);
	}

}
