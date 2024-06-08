package com.aggelowe.techquiry.common.exceptions;

/**
 * {@link ConstructorException} is an {@link ApplicationException}
 * subclass that is thrown when an invalid invocation of a constructor is
 * performed.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class ConstructorException extends ApplicationException {

	/**
	 * Constructs a new {@link ConstructorException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public ConstructorException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
