package com.aggelowe.techquiry.exception;

/**
 * {@link SQLExecutionException} is an {@link DatabaseException} subclass that
 * is thrown when a SQL statement is attempted to be executed but the operation
 * fails.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class SQLExecutionException extends DatabaseException {

	/**
	 * Constructs a new {@link SQLExecutionException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public SQLExecutionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link SQLExecutionException} with the given message and
	 * cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public SQLExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
