package com.aggelowe.techquiry.common.exception;

/**
 * {@link IllegalConstructionException} is an {@link TechQuiryException}
 * subclass that is thrown when an illegal invocation of a constructor is
 * performed.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class IllegalConstructionException extends TechQuiryException {

	/**
	 * Constructs a new {@link IllegalConstructionException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public IllegalConstructionException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
