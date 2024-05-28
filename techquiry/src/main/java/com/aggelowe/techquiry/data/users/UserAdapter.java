package com.aggelowe.techquiry.data.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.aggelowe.techquiry.Utilities;
import com.aggelowe.techquiry.exception.InvalidConstructionException;
import com.aggelowe.techquiry.exception.SQLExecutionException;

import static com.aggelowe.techquiry.Reference.LOGGER;

/**
 * The {@link UserAdapter} class is responsible for handling the connection of
 * {@link User} objects to the application database.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class UserAdapter {

	/**
	 * This constructor will throw an {@link InvalidConstructionException} whenever
	 * invoked. {@link UserAdapter} objects should <b>not</b> be constructible.
	 * 
	 * @throws InvalidConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private UserAdapter() {
		throw new InvalidConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method is responsible for creating the database table containing the
	 * user entries of the application in the application's database.
	 */
	public static void createUserTable() {
		LOGGER.debug("Creating user database table if missing");
		UserAccessor.createUserTable();
	}

}
