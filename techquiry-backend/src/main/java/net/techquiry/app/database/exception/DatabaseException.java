package net.techquiry.app.database.exception;

import net.techquiry.app.common.exception.TechQuiryException;

/**
 * {@link DatabaseException} is an {@link TechQuiryException} subclass that is
 * thrown when a database related operation is attempted to be performed but
 * fails.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public abstract class DatabaseException extends TechQuiryException {

	/**
	 * Constructs a new {@link DatabaseException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	protected DatabaseException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link DatabaseException} with the given message and cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	protected DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
