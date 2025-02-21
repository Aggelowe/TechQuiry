package com.aggelowe.techquiry.service.exception;

/**
 * {@link ForbiddenOperationException} is an {@link ServiceException} subclass
 * that is thrown when a requested operation is not allowed.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class ForbiddenOperationException extends InvalidRequestException {

	/**
	 * Constructs a new {@link ForbiddenOperationException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public ForbiddenOperationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link ForbiddenOperationException} with the given message
	 * and cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public ForbiddenOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
