package com.aggelowe.techquiry.data.users;

import static com.aggelowe.techquiry.Reference.LOGGER;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.aggelowe.techquiry.data.StatementLoader;
import com.aggelowe.techquiry.exception.InvalidConstructionException;
import com.aggelowe.techquiry.exception.SQLExecutionException;

/**
 * The {@link StatementLoader} class is responsible for making the necessary
 * statements to TechQuiry's database, and is used for the retrieval and
 * modification of the user data by the user system.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class UserAccessor {

	/**
	 * This constructor will throw an {@link InvalidConstructionException} whenever
	 * invoked. {@link UserAccessor} objects should <b>not</b> be constructible.
	 * 
	 * @throws InvalidConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private UserAccessor() {
		throw new InvalidConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method is responsible for creating the <i>users</i> table in the
	 * application database by executing the first statement in the respective SQL
	 * script file.
	 */
	public static void createUsersTable() {
		LOGGER.debug("Creating user database table if missing");
		String name = "users_create_table.sql";
		List<PreparedStatement> statements = StatementLoader.loadStatements(name);
		statements.forEach(statement -> {
			if (statement != null) {
				try {
					statement.execute();
				} catch (SQLException exception) {
					throw new SQLExecutionException("A database error occured on the SQL statement's execution!", exception);
				}
			}
		});
	}

}
