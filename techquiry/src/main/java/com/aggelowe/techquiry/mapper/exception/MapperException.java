package com.aggelowe.techquiry.mapper.exception;

import com.aggelowe.techquiry.common.exception.TechQuiryException;

/**
 * {@link MapperException} is an {@link TechQuiryException} subclass that is
 * thrown when a mapper related operation is attempted to be performed but
 * fails.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public abstract class MapperException extends TechQuiryException {

	/**
	 * Constructs a new {@link MapperException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	protected MapperException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link MapperException} with the given message and cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	protected MapperException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
