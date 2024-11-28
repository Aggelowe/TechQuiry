package com.aggelowe.techquiry.common.exceptions;

/**
 * {@link InvalidEnvironmentVariableException} is an {@link TechQuiryException} subclass that
 * is thrown when an environment variable is formatted invalidly.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class InvalidEnvironmentVariableException extends TechQuiryException {

	/**
	 * Constructs a new {@link InvalidEnvironmentVariableException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public InvalidEnvironmentVariableException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link InvalidEnvironmentVariableException} with the given message and
	 * cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public InvalidEnvironmentVariableException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
