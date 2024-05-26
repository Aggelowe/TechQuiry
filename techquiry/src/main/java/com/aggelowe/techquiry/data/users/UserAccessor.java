package com.aggelowe.techquiry.data.users;

import static com.aggelowe.techquiry.Reference.LOGGER;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import com.aggelowe.techquiry.data.StatementExecutor;
import com.aggelowe.techquiry.data.StatementLoader;
import com.aggelowe.techquiry.exception.InvalidConstructionException;
import com.aggelowe.techquiry.exception.SQLExecutionException;
import com.aggelowe.techquiry.exception.SQLScriptException;

/**
 * The {@link UserAccessor} class is responsible for making the necessary
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
	 * application database. This method executes every statement in the respective
	 * SQL script file.
	 */
	public static void createUserTable() {
		LOGGER.debug("Creating user database table if missing");
		String name = "users_create_table.sql";
		List<PreparedStatement> statements = StatementLoader.loadStatements(name);
		StatementExecutor.executeStatements(statements);
	}

	/**
	 * This method returns the {@link ResultSet} containing the number of user
	 * entries in the application database. This method executes only the first
	 * statement in the respective SQL script file.
	 * 
	 * @return The {@link ResultSet} with the user count
	 */
	private static ResultSet getUserCount() {
		String name = "users_select_count.sql";
		List<PreparedStatement> statements = StatementLoader.loadStatements(name);
		if (statements.size() < 1) {
			throw new SQLScriptException("Invalid number of statements in " + name + "!");
		}
		PreparedStatement statement = statements.getFirst();
		Optional<ResultSet> result = StatementExecutor.executeStatement(statement);
		if (result.isEmpty()) {
			throw new SQLExecutionException("The first statement in " + name + " did not yeild a result!");
		}
		return result.get();
	}

	/**
	 * This method returns the {@link ResultSet} containing the user entry in the
	 * application database that has the provided user id. This method executes only the first
	 * statement in the respective SQL script file.
	 * 
	 * @param id The user id of the user entry
	 * @return The {@link ResultSet} with the user entry
	 */
	private static ResultSet getUserById(int id) {
		String name = "users_select_by_id.sql";
		List<PreparedStatement> statements = StatementLoader.loadStatements(name);
		if (statements.size() < 1) {
			throw new SQLScriptException("Invalid number of statements in " + name + "!");
		}
		PreparedStatement statement = statements.getFirst();
		Optional<ResultSet> result = StatementExecutor.executeStatement(statement, id);
		if (result.isEmpty()) {
			throw new SQLExecutionException("The first statement in " + name + " did not yeild a result!");
		}
		return result.get();
	}

	/**
	 * This method returns the {@link ResultSet} containing the user entry in the
	 * application database that has the provided username. This method executes only the first
	 * statement in the respective SQL script file.
	 * 
	 * @param username The username of the user entry
	 * @return The {@link ResultSet} with the user entry
	 */
	private static ResultSet getUserByUsername(String username) {
		String name = "users_select_by_username.sql";
		List<PreparedStatement> statements = StatementLoader.loadStatements(name);
		if (statements.size() < 1) {
			throw new SQLScriptException("Invalid number of statements in " + name + "!");
		}
		PreparedStatement statement = statements.getFirst();
		Optional<ResultSet> result = StatementExecutor.executeStatement(statement, username);
		if (result.isEmpty()) {
			throw new SQLExecutionException("The first statement in " + name + " did not yeild a result!");
		}
		return result.get();
	}
	
}
