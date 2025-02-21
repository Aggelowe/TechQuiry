package com.aggelowe.techquiry.common;

import com.aggelowe.techquiry.common.exception.IllegalConstructionException;

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
	public static final String APPLICATION_NAME = "TechQuiry";

	/**
	 * The string containing the application version
	 */
	public static final String APPLICATION_VERSION = "0.0.1";

	/**
	 * The filename of the application database file
	 */
	public static final String DATABASE_FILENAME = "techquiry.db";

	/**
	 * The regular expression defining the valid format for usernames.
	 */
	public static final String SECURITY_USERNAME_REGEX = "^[A-Za-z][A-Za-z0-9_]{2,14}$";

	/**
	 * The hashing algorithm for generating the password hashes.
	 */
	public static final String SECURITY_HASHING_ALGORITHM = "SHA-256";

}
