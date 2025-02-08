package com.aggelowe.techquiry.database.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@link Inquiry} class represents an inquiry entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Inquiry {

	/**
	 * The unique id of the inquiry
	 */
	private final int id;

	/**
	 * The user id of the author
	 */
	private int userId;

	/**
	 * The title of the inquiry
	 */
	private String title;

	/**
	 * The content of the inquiry
	 */
	private String content;

	/**
	 * Whether the author is anonymous
	 */
	private boolean anonymous;

	/**
	 * This method returns the object as a string containing the inquiry id, the
	 * author's user id, the title, the content and whether the author is anonymous.
	 */
	@Override
	public String toString() {
	    return "Inquiry(id=" + id +
	    		(anonymous ? "" : ", userId=" + userId) +
	    		", title=" + title +
	    		", content=" + content +
	    		", anonymous=" + anonymous + ")";
	}
	
	/**
	 * This method creates a new shallow copy of the current {@link Inquiry} object.
	 */
	public Inquiry copy() {
		return new Inquiry(id, userId, title, content, anonymous);
	}

}
