package com.aggelowe.techquiry.database.exception;

/**
 * {@link SQLRunnerExecuteException} is an {@link DatabaseException} subclass that
 * is thrown when a SQL script is attempted to be executed but the operation
 * fails.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class SQLRunnerExecuteException extends SQLRunnerException {

	/**
	 * Constructs a new {@link SQLRunnerExecuteException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public SQLRunnerExecuteException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link SQLRunnerExecuteException} with the given message and
	 * cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public SQLRunnerExecuteException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
