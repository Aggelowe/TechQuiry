package com.aggelowe.techquiry.exception;

/**
 * {@link InvalidConstructionException} is an {@link ApplicationException}
 * subclass that is thrown when an invalid invocation of a constructor is
 * performed.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class InvalidConstructionException extends ApplicationException {

	/**
	 * Constructs a new {@link InvalidConstructionException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public InvalidConstructionException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
