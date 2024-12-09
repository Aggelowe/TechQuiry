package com.aggelowe.techquiry.service.exceptions;

/**
 * {@link EntityNotFoundException} is an {@link ServiceException} subclass that
 * is thrown when a requested entity is not found in the application data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class EntityNotFoundException extends ServiceException {

	/**
	 * Constructs a new {@link EntityNotFoundException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public EntityNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link EntityNotFoundException} with the given message and
	 * cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
