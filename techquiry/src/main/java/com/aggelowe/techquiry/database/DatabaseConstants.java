package com.aggelowe.techquiry.database;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;

/**
 * {@link DatabaseConstants} is a class that holds constants that are important
 * for the functionality of the application database and the communication
 * between the database and the application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class DatabaseConstants {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link DatabaseConstants} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private DatabaseConstants() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * The name of the SQL script for applying the database schema.
	 */
	public static final String CREATE_SCHEMA_SCRIPT = "/database/create_schema.sql";

}
