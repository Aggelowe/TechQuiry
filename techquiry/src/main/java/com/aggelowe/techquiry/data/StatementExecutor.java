package com.aggelowe.techquiry.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.aggelowe.techquiry.exception.InvalidConstructionException;
import com.aggelowe.techquiry.exception.SQLExecutionException;

/**
 * The {@link StatementExecutor} class is responsible for handling the execution
 * of the given statements in the TechQuiry database, while applying the given
 * settings and parameters.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class StatementExecutor {

	/**
	 * This constructor will throw an {@link InvalidConstructionException} whenever
	 * invoked. {@link StatementExecutor} objects should <b>not</b> be
	 * constructible.
	 * 
	 * @throws InvalidConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private StatementExecutor() {
		throw new InvalidConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method executes the given SQL statement with the provided parameters in
	 * the TechQuiry database and then returns the {@link ResultSet} containing the
	 * results of the executed statement.
	 * 
	 * @param statement  The {@link PreparedStatement} to execute
	 * @param parameters The parameters for the statement
	 * @return The results of the execution wrapped in an {@link Optional}
	 */
	public static Optional<ResultSet> executeStatement(PreparedStatement statement, Object... parameters) {
		int index = 1;
		for (Object parameter : parameters) {
			try {
				statement.setObject(index, parameter);
			} catch (SQLException exception) {
				throw new SQLExecutionException("An error occured while setting the SQL statement's parameters!", exception);
			}
			index++;
		}
		try {
			statement.execute();
		} catch (SQLException exception) {
			throw new SQLExecutionException("An error occured on the SQL statement's execution!", exception);
		}
		ResultSet result;
		try {
			result = statement.getResultSet();
		} catch (SQLException exception) {
			throw new SQLExecutionException("An error occured while obtaininh the statement's results!", exception);
		}
		return Optional.ofNullable(result);
	}

	/**
	 * This method executes the given list of statements (without parameters) in
	 * TechQuiry's database and then returns a list containing the result of each
	 * executed SQL statement.
	 * 
	 * @param statements The list of statements to execute
	 * @return The list of the result of each executed statement
	 */
	public static List<Optional<ResultSet>> executeStatements(List<PreparedStatement> statements) {
		List<Optional<ResultSet>> results = new ArrayList<>(statements.size());
		statements.forEach(statement -> {
			if (statement != null) {
				Optional<ResultSet> result = executeStatement(statement);
				results.add(result);
			}
		});
		return results;
	}
}
