package com.aggelowe.techquiry;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aggelowe.techquiry.exception.InvalidConstructionException;

/**
 * {@link Reference} is a class that holds constants that are
 * important for the functionality of the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class Reference {

	/**
	 * This constructor will throw an {@link InvalidConstructionException} whenever invoked.
	 * {@link Reference} objects should <b>not</b> be constructible.
	 * 
	 * @throws InvalidConstructionException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private Reference() {
		throw new InvalidConstructionException(getClass().getName() + " objects should not be constructed!");
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
	 * The {@link File} object representing the execution directory
	 */
	public static final File EXECUTION_DIRECTORY;
	
	/**
	 * The filename of the application configuration file
	 */
	public static final String CONFIGURATION_FILENAME = "techquiry.properties";
	
	/**
	 * The filename of the application database file
	 */
	public static final String DATABASE_FILENAME = "techquiry.db";
	
	static {
		LOGGER = LogManager.getLogger(TechQuiry.class);
		String executionPath = System.getProperty("user.dir");
		EXECUTION_DIRECTORY = new File(executionPath);
	}

	
}
