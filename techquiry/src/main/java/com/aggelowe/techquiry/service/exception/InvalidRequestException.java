package com.aggelowe.techquiry.service.exception;

/**
 * {@link InvalidRequestException} is an {@link ServiceException} subclass
 * that is thrown when a request for an application operation is malformed.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class InvalidRequestException extends ServiceException {

	/**
	 * Constructs a new {@link InvalidRequestException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public InvalidRequestException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link InvalidRequestException} with the given message and
	 * cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public InvalidRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
