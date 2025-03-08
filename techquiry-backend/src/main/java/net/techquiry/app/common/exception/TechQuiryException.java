package net.techquiry.app.common.exception;

/**
 * {@link TechQuiryException} is the superclass of all exceptions implemented in
 * the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public abstract class TechQuiryException extends Exception {

	/**
	 * Constructs a new {@link TechQuiryException} with the given message. The cause
	 * of the exception is set to NULL.
	 *
	 * @param message The details message of the exception.
	 */
	protected TechQuiryException(String message) {
		this(message, null);
	}

	/**
	 * Constructs a new {@link TechQuiryException} with the given message and cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The cause of the exception.
	 */
	protected TechQuiryException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
