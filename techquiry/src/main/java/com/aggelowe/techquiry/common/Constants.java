package com.aggelowe.techquiry.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aggelowe.techquiry.common.exceptions.IllegalConstructionException;

/**
 * {@link Constants} is a class that holds constants that are important for the
 * functionality of the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class Constants {

	/**
	 * This constructor will throw an {@link IllegalConstructionException} whenever
	 * invoked. {@link Constants} objects should <b>not</b> be constructible.
	 * 
	 * @throws IllegalConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private Constants() throws IllegalConstructionException {
		throw new IllegalConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * The string containing the name of the application
	 */
	public static final String NAME = "TechQuiry";

	/**
	 * The string containing the application version
	 */
	public static final String VERSION = "0.0.1";

	/**
	 * The logger object used by the application
	 */
	public static final Logger LOGGER;

	/**
	 * The path of the execution directory
	 */
	public static final String EXECUTION_DIRECTORY;

	/**
	 * The filename of the application database file
	 */
	public static final String DATABASE_FILENAME = "techquiry.db";

	/**
	 * The maximum possible length of the salt for hashing the application users'
	 * passwords.
	 */
	public static final int MAX_SALT_LENGTH = 64;

	static {
		LOGGER = LogManager.getLogger(TechQuiry.class);
		EXECUTION_DIRECTORY = System.getProperty("user.dir");
	}

}
