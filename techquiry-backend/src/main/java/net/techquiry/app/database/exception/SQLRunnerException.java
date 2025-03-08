package net.techquiry.app.database.exception;

/**
 * {@link SQLRunnerException} is an {@link DatabaseException} subclass that
 * is thrown when a SQL script is attempted to be loaded or executed but the operation
 * fails.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class SQLRunnerException extends DatabaseException {

	/**
	 * Constructs a new {@link SQLRunnerException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public SQLRunnerException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link SQLRunnerException} with the given message and cause.

	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public SQLRunnerException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}