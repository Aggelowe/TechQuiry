package com.aggelowe.techquiry.database;

import com.aggelowe.techquiry.common.exceptions.IllegalConstructionException;

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
	 * This constructor will throw an {@link IllegalConstructionException} whenever invoked.
	 * {@link DatabaseConstants} objects should <b>not</b> be constructible.
	 * 
	 * @throws IllegalConstructionException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private DatabaseConstants() throws IllegalConstructionException {
		throw new IllegalConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * The name of the SQL script for applying the database schema.
	 */
	public static final String CREATE_SCHEMA_SCRIPT = "/database/schema.sql";
	
	/**
	 * The name of the SQL script for obtaining the count of user login entries.
	 */
	public static final String USER_LOGIN_COUNT_SCRIPT = "/database/user_login/count.sql";

	/**
	 * The name of the SQL script for deleting a user login entry.
	 */
	public static final String USER_LOGIN_DELETE_SCRIPT = "/database/user_login/delete.sql";
	
	/**
	 * The name of the SQL script for inserting a user login entry.
	 */
	public static final String USER_LOGIN_INSERT_SCRIPT = "/database/user_login/insert.sql";
	
	/**
	 * The name of the SQL script for selecting a user login entry range.
	 */
	public static final String USER_LOGIN_RANGE_SCRIPT = "/database/user_login/range.sql";
	
	/**
	 * The name of the SQL script for selecting a user login entry with an id.
	 */
	public static final String USER_LOGIN_SELECT_SCRIPT = "/database/user_login/select.sql";
	
	/**
	 * The name of the SQL script for selecting a user login entry with a username.
	 */
	public static final String USER_LOGIN_SELECT_USERNAME_SCRIPT = "/database/user_login/select_username.sql";
	
	/**
	 * The name of the SQL script for updating a user login entry.
	 */
	public static final String USER_LOGIN_UPDATE_SCRIPT = "/database/user_login/update.sql";
	
	/**
	 * The name of the SQL script for obtaining the count of user data entries.
	 */
	public static final String USER_DATA_COUNT_SCRIPT = "/database/user_data/count.sql";

	/**
	 * The name of the SQL script for deleting a user data entry.
	 */
	public static final String USER_DATA_DELETE_SCRIPT = "/database/user_data/delete.sql";
	
	/**
	 * The name of the SQL script for inserting a user data entry.
	 */
	public static final String USER_DATA_INSERT_SCRIPT = "/database/user_data/insert.sql";
	
	/**
	 * The name of the SQL script for selecting a user data entry range.
	 */
	public static final String USER_DATA_RANGE_SCRIPT = "/database/user_data/range.sql";
	
	/**
	 * The name of the SQL script for selecting a user data entry.
	 */
	public static final String USER_DATA_SELECT_SCRIPT = "/database/user_data/select.sql";
	
	/**
	 * The name of the SQL script for updating a user data entry.
	 */
	public static final String USER_DATA_UPDATE_SCRIPT = "/database/user_data/update.sql";
	
	/**
	 * The name of the SQL script for obtaining the count of inquiry entries.
	 */
	public static final String INQUIRY_COUNT_SCRIPT = "/database/inquiry/count.sql";
	
	/**
	 * The name of the SQL script for deleting an inquiry entry.
	 */
	public static final String INQUIRY_DELETE_SCRIPT = "/database/inquiry/delete.sql";
	
	/**
	 * The name of the SQL script for inserting an inquiry entry.
	 */
	public static final String INQUIRY_INSERT_SCRIPT = "/database/inquiry/insert.sql";
	
	/**
	 * The name of the SQL script for selecting an inquiry entry range.
	 */
	public static final String INQUIRY_RANGE_SCRIPT = "/database/inquiry/range.sql";
	
	/**
	 * The name of the SQL script for selecting an inquiry entry.
	 */
	public static final String INQUIRY_SELECT_SCRIPT = "/database/inquiry/select.sql";
	
	/**
	 * The name of the SQL script for updating an inquiry entry.
	 */
	public static final String INQUIRY_UPDATE_SCRIPT = "/database/inquiry/update.sql";
	
	/**
	 * The name of the SQL script for deleting an observer entry.
	 */
	public static final String OBSERVER_DELETE_SCRIPT = "/database/observer/delete.sql";
	
	/**
	 * The name of the SQL script for inserting an observer entry.
	 */
	public static final String OBSERVER_INSERT_SCRIPT = "/database/observer/insert.sql";
	
	/**
	 * The name of the SQL script for selecting an observer entry with an inquiry id.
	 */
	public static final String OBSERVER_SELECT_INQUIRY_ID_SCRIPT = "/database/observer/select_inquiry_id.sql";
	
	/**
	 * The name of the SQL script for selecting an observer entry with a user id.
	 */
	public static final String OBSERVER_SELECT_USER_ID_SCRIPT = "/database/observer/select_user_id.sql";
	
	/**
	 * The name of the SQL script for deleting a response entry.
	 */
	public static final String RESPONSE_DELETE_SCRIPT = "/database/response/delete.sql";
	
	/**
	 * The name of the SQL script for inserting a response entry.
	 */
	public static final String RESPONSE_INSERT_SCRIPT = "/database/response/insert.sql";
	
	/**
	 * The name of the SQL script for selecting a response entry with an inquiry id.
	 */
	public static final String RESPONSE_SELECT_INQUIRY_ID_SCRIPT = "/database/response/select_inquiry_id.sql";
	
	/**
	 * The name of the SQL script for selecting a response entry.
	 */
	public static final String RESPONSE_SELECT_SCRIPT = "/database/response/select.sql";
	
	/**
	 * The name of the SQL script for updating a response entry.
	 */
	public static final String RESPONSE_UPDATE_SCRIPT = "/database/response/update.sql";
	
	/**
	 * The name of the SQL script for deleting an upvote entry.
	 */
	public static final String UPVOTE_DELETE_SCRIPT = "/database/upvote/delete.sql";
	
	/**
	 * The name of the SQL script for inserting an upvote entry.
	 */
	public static final String UPVOTE_INSERT_SCRIPT = "/database/upvote/insert.sql";
	
	/**
	 * The name of the SQL script for selecting an upvote entry with a response id.
	 */
	public static final String UPVOTE_SELECT_RESPONSE_ID_SCRIPT = "/database/upvote/select_response_id.sql";
	
	/**
	 * The name of the SQL script for selecting an upvote entry with a user id.
	 */
	public static final String UPVOTE_SELECT_USER_ID_SCRIPT = "/database/upvote/select_user_id.sql";
	
}
