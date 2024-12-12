package com.aggelowe.techquiry.database.entities;

/**
 * The {@link Inquiry} class represents an inquiry entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
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
	 * This constructor constructs a new {@link Inquiry} instance with the
	 * provided parameters as the required inquiry information.
	 * 
	 * @param id The unique inquiry id
	 * @param userId The author's user id
	 * @param title The inquiry's title
	 * @param content The inquiry's content
	 * @param anonymous Whether the author is anonymous
	 */
	public Inquiry(int id, int userId, String title, String content, boolean anonymous) {
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.content = content;
		this.anonymous = anonymous;
	}

	/**
	 * This method returns the unique id of the inquiry
	 * 
	 * @return The inquiry's id
	 */	
	public int getId() {
		return id;
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
	 * This method returns the title of the inquiry
	 * 
	 * @return The inquiry's title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * This method returns the content of the inquiry
	 * 
	 * @return The inquiry's content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * This method whether the inquiry's author is anonymous
	 * 
	 * @return Whether the author is anonymous
	 */
	public boolean isAnonymous() {
		return anonymous;
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
	 * This method sets the title of the inquiry
	 * 
	 * @param title The inquiry's title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * This method sets the content of the inquiry
	 * 
	 * @param content The inquiry's content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * This method sets whether the inquiry's author is anonymous
	 * 
	 * @param anonymous Whether the author is anonymous
	 */
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	/**
	 * This method returns the object as a string containing the inquiry id, the
	 * author's user id, the title, the content and whether the author is anonymous.
	 */
	@Override
	public String toString() {
		return "[Inquiry ID: " + id +
				", User ID: " + (anonymous ? "REDACTED" : userId) +
				", Title: " + title +
				", Content: " + content +
				", Anonymous: " + anonymous + "]";
	}
	
}
