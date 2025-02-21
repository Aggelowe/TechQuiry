package com.aggelowe.techquiry.service.exception;

/**
 * {@link UnauthorizedOperationException} is an {@link ServiceException}
 * subclass that is thrown when an operation is requested, but the client making
 * the request is not logged in (properly).
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class UnauthorizedOperationException extends InvalidRequestException {

	/**
	 * Constructs a new {@link UnauthorizedOperationException} with the given
	 * message.
	 *
	 * @param message The details message of the exception.
	 */
	public UnauthorizedOperationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link UnauthorizedOperationException} with the given
	 * message and cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public UnauthorizedOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
