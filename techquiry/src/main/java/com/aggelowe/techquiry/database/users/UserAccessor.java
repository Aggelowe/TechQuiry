package com.aggelowe.techquiry.database.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.StatementExecutor;
import com.aggelowe.techquiry.database.StatementLoader;
import com.aggelowe.techquiry.database.exceptions.SQLExecutionException;
import com.aggelowe.techquiry.database.exceptions.SQLScriptException;

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
	 * This constructor will throw an {@link ConstructorException} whenever
	 * invoked. {@link UserAccessor} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private UserAccessor() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method is responsible for creating the <i>users</i> table in the
	 * application database. This method executes every statement in the respective
	 * SQL script file.
	 */
	static void createUserTable() {
		String name = "users/create_table.sql";
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
	static ResultSet selectUserCount() {
		String name = "users/select_count.sql";
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
	 * application database that has the provided user id. This method executes only
	 * the first statement in the respective SQL script file.
	 * 
	 * @param id The user id of the user entry
	 * @return The {@link ResultSet} with the user entry
	 */
	static ResultSet selectUserById(int id) {
		String name = "users/select_by_id.sql";
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
	 * application database that has the provided username. This method executes
	 * only the first statement in the respective SQL script file.
	 * 
	 * @param username The username of the user entry
	 * @return The {@link ResultSet} with the user entry
	 */
	static ResultSet selectUserByUsername(String username) {
		String name = "users/select_by_username.sql";
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

	/**
	 * This method inserts a new user entry into the application database with the
	 * given user id, username, display name, password hash and password salt. This
	 * method executes only the first statement in the respective SQL script file.
	 * 
	 * @param id           The id of the user
	 * @param username     The username of the user
	 * @param displayName  The display name of the user
	 * @param passwordHash The hash of the password of the user
	 * @param passwordSalt The salt of the password hash of the user
	 */
	static void insertUser(int id, String username, String displayName, String passwordHash, String passwordSalt) {
		String name = "users/insert.sql";
		List<PreparedStatement> statements = StatementLoader.loadStatements(name);
		if (statements.size() < 1) {
			throw new SQLScriptException("Invalid number of statements in " + name + "!");
		}
		PreparedStatement statement = statements.getFirst();
		StatementExecutor.executeStatement(statement, id, username, displayName, passwordHash, passwordSalt);
	}

	/**
	 * This method updates the information of the user with the given id to the ones
	 * provided as parameters, including the username, display name, password hash
	 * and password salt. This method executes only the first statement in the
	 * respective SQL script file.
	 * 
	 * @param id           The id of the user
	 * @param username     The username of the user
	 * @param displayName  The display name of the user
	 * @param passwordHash The hash of the password of the user
	 * @param passwordSalt The salt of the password hash of the user
	 */
	static void updateUserById(int id, String username, String displayName, String passwordHash, String passwordSalt) {
		String name = "users/update_by_id.sql";
		List<PreparedStatement> statements = StatementLoader.loadStatements(name);
		if (statements.size() < 1) {
			throw new SQLScriptException("Invalid number of statements in " + name + "!");
		}
		PreparedStatement statement = statements.getFirst();
		StatementExecutor.executeStatement(statement, username, displayName, passwordHash, passwordSalt, id);
	}

	/**
	 * This method deletes the information of the user entry in the application
	 * database that has the provided user id. This method executes only the first
	 * statement in the respective SQL script file.
	 * 
	 * @param id The id of the user
	 */
	static void deleteUserById(int id) {
		String name = "users/delete_by_id.sql";
		List<PreparedStatement> statements = StatementLoader.loadStatements(name);
		if (statements.size() < 1) {
			throw new SQLScriptException("Invalid number of statements in " + name + "!");
		}
		PreparedStatement statement = statements.getFirst();
		StatementExecutor.executeStatement(statement, id);
	}

}
