package com.aggelowe.techquiry.config;

import com.aggelowe.techquiry.ApplicationException;

/**
 * {@link ConfigurationException} is a {@link ApplicationException} subclass that
 * is thrown when unexpected behavior occurs within the configuration system.
 * 
 * @author Angelos Margaritis (Aggelowe)
 * @since 0.0.1
 */
public class ConfigurationException extends ApplicationException {

	/**
	 * Constructs a new {@link ConfigurationException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link ConfigurationException} with the given message and cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause The <i>cause</i> of the exception.
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
