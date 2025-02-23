package com.aggelowe.techquiry.database.exception;

/**
 * {@link DataAccessException} is an exception class that represents errors
 * encountered during data access operations in the application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class DataAccessException extends DatabaseException {

	/**
	 * Constructs a new {@link DataAccessException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public DataAccessException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link DataAccessException} with the given message and
	 * cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * The exception message for when the result count from an SQL script execution
	 * is invalid.
	 */
	public static final String INVALID_RESULT_COUNT_MESSAGE = "The script %s yeilded an unexpected count of results!";

	/**
	 * The exception message for when an SQL statement does not return an expected
	 * result.
	 */
	public static final String MISSING_RESULT_MESSAGE = "A statement in script %s did not yeild the expected result!";

	/**
	 * The exception message for when an SQL statement returned an invalid result.
	 */
	public static final String INVALID_RESULT_MESSAGE = "A statement in script %s yeilded an unexpected result!";

	private static final long serialVersionUID = 1L;

}
