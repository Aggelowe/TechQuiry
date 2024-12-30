package com.aggelowe.techquiry.service.exception;

/**
 * {@link InternalErrorException} is an {@link ServiceException} subclass that
 * is thrown when an internal error occurs on a request for an application
 * operation.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class InternalErrorException extends ServiceException {

	/**
	 * Constructs a new {@link InternalErrorException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public InternalErrorException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link InternalErrorException} with the given message and
	 * cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public InternalErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
