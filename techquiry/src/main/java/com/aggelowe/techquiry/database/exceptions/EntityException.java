package com.aggelowe.techquiry.database.exceptions;

/**
 * {@link EntityException} is an exception class that represents errors
 * encountered during the creation or modification of entities.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class EntityException extends DatabaseException {

	/**
	 * Constructs a new {@link EntityException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public EntityException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link EntityException} with the given message and cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public EntityException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
