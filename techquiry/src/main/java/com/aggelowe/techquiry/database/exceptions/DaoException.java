package com.aggelowe.techquiry.database.exceptions;

/**
 * {@link DaoException} is an exception class that represents errors encountered
 * during data access operations in the application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class DaoException extends DatabaseException {

	/**
	 * Constructs a new {@link DaoException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public DaoException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link DaoException} with the given message and cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
