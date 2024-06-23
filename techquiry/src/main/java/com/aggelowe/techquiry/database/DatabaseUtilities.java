package com.aggelowe.techquiry.database;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.aggelowe.techquiry.common.Constants;
import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.exceptions.SQLExecutionException;
import com.aggelowe.techquiry.database.exceptions.SQLScriptException;

/**
 * The {@link DatabaseUtilities} class contains several utility methods that are
 * important for the functionality of the application database and the
 * communication between the database and the application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class DatabaseUtilities {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link DatabaseUtilities} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private DatabaseUtilities() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * The {@link #loadStatement(InputStream)} method parses the SQL statements from
	 * the provided {@link InputStream} pointing to the SQL script file and
	 * generates the {@link List} containing the {@link Statement} objects, ready to
	 * be executed.
	 * 
	 * @param connection The database connection to apply the statements on
	 * @param stream     The stream reading the file containing the SQL statements
	 * @return The list of {@link PreparedStatement} objects
	 */
	public static List<PreparedStatement> loadStatements(Connection connection, InputStream stream) {
		List<PreparedStatement> statements = new LinkedList<PreparedStatement>();
		BufferedInputStream buffer = new BufferedInputStream(stream);
		StringBuilder commandBuilder = new StringBuilder();
		int mode = 0;
		char previous = (char) -1;
		int code;
		try {
			while ((code = buffer.read()) != -1) {
				char character = (char) code;
				switch (mode) {
					case 0: {
						if (character == '"') {
							mode = 1;
						}
						if (character == '\'') {
							mode = 2;
						}
						if (character == '*' && previous == '/') {
							mode = 3;
							commandBuilder.deleteCharAt(commandBuilder.length() - 1);
							continue;
						}
						if (character == '-' && previous == '-') {
							mode = 4;
							commandBuilder.deleteCharAt(commandBuilder.length() - 1);
							continue;
						}
						if (character == '\n' || character == '\t') {
							if (previous != ' ' && previous != '\n' && previous != '\t') {
								commandBuilder.append(' ');
							}
						} else {
							commandBuilder.append(character);
						}
						if (character == ';') {
							String command = commandBuilder.toString();
							command = command.trim();
							commandBuilder = new StringBuilder();
							PreparedStatement statement;
							try {
								statement = connection.prepareStatement(command);
							} catch (SQLException exception) {
								throw new SQLScriptException("The SQL statement could not be constructed!", exception);
							}
							statements.add(statement);
						}
						break;
					}
					case 1: {
						if (character == '"') {
							mode = 0;
						}
						commandBuilder.append(character);
						break;
					}
					case 2: {
						if (character == '\'') {
							mode = 0;
						}
						commandBuilder.append(character);
						break;
					}
					case 3: {
						if (character == '/' && previous == '*') {
							mode = 0;
						}
						break;
					}
					case 4: {
						if (character == '\n') {
							mode = 0;
						}
						break;
					}
				}
				previous = character;
			}
		} catch (IOException exception) {
			throw new SQLScriptException("An exception occured while reading the SQL script!", exception);
		} finally {
			try {
				buffer.close();
			} catch (IOException exception) {
				throw new SQLScriptException("The SQL script input stream could not be closed!", exception);
			}
		}
		return statements;
	}

	/**
	 * The {@link #loadStatement(String)} method parses the SQL statements from the
	 * SQL script file with the given name (in the resource path defined in
	 * {@link Constants#SQL_DIRECTORY}) and generates the {@link List} containing
	 * the {@link Statement} objects, ready to be executed.
	 * 
	 * @param connection The database connection to apply the statements on
	 * @param name The name of the file containing the SQL statements
	 * @return The list of {@link PreparedStatement} objects
	 */
	public static List<PreparedStatement> loadStatements(Connection connection, String name) {
		String resource = name;
		InputStream stream = DatabaseUtilities.class.getResourceAsStream(resource);
		List<PreparedStatement> statements = loadStatements(connection, stream);
		return statements;
	}

	/**
	 * This method executes the given SQL statement with the provided parameters in
	 * the TechQuiry database and then returns the {@link ResultSet} containing the
	 * results of the executed statement.
	 * 
	 * @param statement  The {@link PreparedStatement} to execute
	 * @param parameters The parameters for the statement
	 * @return The results of the execution
	 */
	public static ResultSet executeStatement(PreparedStatement statement, Object... parameters) {
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
		return result;
	}

	/**
	 * This method executes the given list of statements with the provided
	 * parameters in TechQuiry's database and then returns a list containing the
	 * result of each executed SQL statement.
	 * 
	 * @param statements The list of statements to execute
	 * @param parameters The parameters for the statements
	 * @return The list of the result of each executed statement
	 */
	public static List<ResultSet> executeStatements(List<PreparedStatement> statements, Object... parameters) {
		List<ResultSet> results = new ArrayList<>(statements.size());
		statements.forEach(statement -> {
			if (statement != null) {
				ResultSet result = executeStatement(statement, parameters);
				results.add(result);
			}
		});
		return results;
	}

}
