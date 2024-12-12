package com.aggelowe.techquiry.database.entities;

/**
 * The {@link Response} class represents an response entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
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
	 * This constructor constructs a new {@link Response} instance with the provided
	 * parameters as the required response information.
	 * 
	 * @param id        The unique response id
	 * @param inquiryId The parent's inquiry id
	 * @param userId    The author's user id
	 * @param anonymous Whether the author is anonymous
	 * @param content   The response's content
	 */
	public Response(int id, int inquiryId, int userId, boolean anonymous, String content) {
		this.id = id;
		this.inquiryId = inquiryId;
		this.userId = userId;
		this.anonymous = anonymous;
		this.content = content;
	}

	/**
	 * This method returns the unique id of the response
	 * 
	 * @return The response's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * This method returns the id of the parent inquiry's id
	 * 
	 * @return The parent inquiry's id
	 */
	public int getInquiryId() {
		return inquiryId;
	}

	/**
	 * This method returns the user id of the author
	 * 
	 * @return The author's user id
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * This method whether the response's author is anonymous
	 * 
	 * @return Whether the author is anonymous
	 */
	public boolean isAnonymous() {
		return anonymous;
	}

	/**
	 * This method returns the content of the response
	 * 
	 * @return The response's content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * This method sets the parent inquiry's id
	 * 
	 * @param userId The parent inquiry's id
	 */
	public void setInquiryId(int inquiryId) {
		this.inquiryId = inquiryId;
	}

	/**
	 * This method sets the author's user id
	 * 
	 * @param userId The author's user id
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * This method sets whether the response's author is anonymous
	 * 
	 * @param anonymous Whether the author is anonymous
	 */
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	/**
	 * This method sets the content of the response
	 * 
	 * @param content The response's content
	 */
	public void setContent(String content) {
		this.content = content;
	}

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
