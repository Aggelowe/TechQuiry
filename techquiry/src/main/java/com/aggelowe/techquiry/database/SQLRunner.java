package com.aggelowe.techquiry.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.database.exception.SQLRunnerException;
import com.aggelowe.techquiry.database.exception.SQLRunnerExecuteException;
import com.aggelowe.techquiry.database.exception.SQLRunnerLoadException;

import lombok.RequiredArgsConstructor;

/**
 * The {@link SQLRunner} class is responsible for executing the provided SQL
 * scripts on the predefined connection with the application's database.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@RequiredArgsConstructor
public final class SQLRunner {

	/**
	 * This object represents the connection with the SQLite database.
	 */
	private final DataSource dataSource;

	/**
	 * This method loads the SQL statements from the provided {@link InputStream}
	 * pointing to the SQL script file, executes them on the preset connection and
	 * returns the {@link List} containing the {@link LocalResult} objects.
	 * 
	 * @param stream     The stream reading the file containing the SQL statements
	 * @param parameters The parameters for the statements
	 * @return The list of {@link LocalResult} objects
	 * @throws SQLRunnerException If an error occurs while loading or running the
	 *                            script
	 */
	public List<LocalResult> runScript(InputStream stream, Object... parameters) throws SQLRunnerException {
		try (Connection connection = dataSource.getConnection()) {
			List<PreparedStatement> statements = loadStatements(connection, stream);
			return executeStatements(connection, statements, parameters);
		} catch (SQLException exception) {
			throw new SQLRunnerExecuteException("Could not get database connection!", exception);
		}
	}

	/**
	 * This method loads the SQL statements from the SQL script file with the given
	 * path, executes them on the preset connection and returns the {@link List}
	 * containing the {@link LocalResult} objects.
	 * 
	 * @param stream     The stream reading the file containing the SQL statements
	 * @param parameters The parameters for the statements
	 * @return The list of {@link LocalResult} objects
	 * @throws SQLRunnerException If an error occurs while loading or running the
	 *                            script
	 */
	public List<LocalResult> runScript(String path, Object... parameters) throws SQLRunnerException {
		InputStream stream = SQLRunner.class.getResourceAsStream(path);
		return runScript(stream, parameters);
	}

	/**
	 * This method prepares the given statement, executes it on the preset
	 * connection and returns the output {@link LocalResult} object.
	 * 
	 * @param statement  The statement to execute
	 * @param parameters The parameters of the statement
	 * @return The result of the execution of the statement
	 * @throws SQLRunnerException If an error occurs while loading or running the
	 *                            script
	 */
	public LocalResult runStatement(String statement, Object... parameters) throws SQLRunnerException {
		try (Connection connection = dataSource.getConnection()) {
			LocalResult result;
			try {
				PreparedStatement prepared = connection.prepareStatement(statement);
				result = executeStatement(prepared, parameters);
				connection.commit();
			} catch (SQLException exception) {
				try {
					connection.rollback();
				} catch (SQLException rollback) {
					throw new SQLRunnerExecuteException("Could not rollback failed commit!", rollback);
				}
				throw new SQLRunnerExecuteException("An error occured while executing the provided SQL statement!", exception);
			}
			return result;
		} catch (SQLException exception) {
			throw new SQLRunnerExecuteException("Could not get database connection!", exception);
		}
	}

	/**
	 * The {@link #loadStatement(InputStream)} method parses the SQL statements from
	 * the provided {@link InputStream} pointing to the SQL script file and
	 * generates the {@link List} containing the {@link PreparedStatement} objects,
	 * ready to be executed.
	 * 
	 * @param connection The connection to the database
	 * @param stream     The stream reading the file containing the SQL statements
	 * @return The list of {@link PreparedStatement} objects
	 * @throws SQLRunnerLoadException If an error occurs while the statements are
	 *                                being loaded
	 */
	private List<PreparedStatement> loadStatements(Connection connection, InputStream stream) throws SQLRunnerLoadException {
		List<PreparedStatement> statements = new LinkedList<>();
		InputStreamReader reader = new InputStreamReader(stream);
		StringBuilder commandBuilder = new StringBuilder();
		int mode = 0;
		char previous = (char) -1;
		int code;
		try {
			while ((code = reader.read()) != -1) {
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
							if (command.length() != 1) {
								PreparedStatement statement = connection.prepareStatement(command);
								statements.add(statement);
							}
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
						if (character == '\'' && previous != '\'') {
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
			String command = commandBuilder.toString();
			command = command.trim();
			if (!command.isEmpty()) {
				PreparedStatement statement = connection.prepareStatement(command);
				statements.add(statement);
			}
		} catch (IOException exception) {
			throw new SQLRunnerLoadException("An exception occured while reading the SQL script!", exception);
		} catch (SQLException exception) {
			throw new SQLRunnerLoadException("The SQL statement could not be constructed!", exception);
		} finally {
			try {
				reader.close();
			} catch (IOException exception) {
				throw new SQLRunnerLoadException("The SQL script input stream could not be closed!", exception);
			}
		}
		return statements;
	}

	/**
	 * This method executes the given SQL statement with the provided parameters in
	 * the TechQuiry database and then returns the {@link LocalResult} containing
	 * the results of the executed statement.
	 * 
	 * @param statement  The {@link PreparedStatement} to execute
	 * @param parameters The parameters for the statement
	 * @return The results of the execution
	 * @throws SQLRunnerExecuteException If an error occurs while executing the
	 *                                   statement
	 */
	private LocalResult executeStatement(PreparedStatement statement, Object... parameters) throws SQLRunnerExecuteException {
		ResultSet result = null;
		LocalResult local;
		try {
			int index = 1;
			for (Object parameter : parameters) {
				statement.setObject(index, parameter);
				index++;
			}
			statement.execute();
			result = statement.getResultSet();
			local = LocalResult.of(result);
		} catch (SQLException exception) {
			throw new SQLRunnerExecuteException("An error occured while executing the given statement!", exception);
		} finally {
			try {
				statement.close();
				if (result != null) {
					result.close();
				}
			} catch (SQLException exception) {
				throw new SQLRunnerExecuteException("An error occured while closing the database resources!", exception);
			}
		}
		return local;
	}

	/**
	 * This method executes the given list of statements with the provided
	 * parameters in TechQuiry's database and then returns a list containing the
	 * result of each executed SQL statement.
	 * 
	 * @param connection The connection to the database
	 * @param statements The list of statements to execute
	 * @param parameters The parameters for the statements
	 * @return The list of the result of each executed statement
	 * @throws SQLRunnerExecuteException If an error occurs while executing the
	 *                                   statements
	 */
	private List<LocalResult> executeStatements(Connection connection, List<PreparedStatement> statements, Object... parameters) throws SQLRunnerExecuteException {
		List<LocalResult> results = new ArrayList<>(statements.size());
		try {
			for (PreparedStatement statement : statements) {
				if (statement == null) {
					continue;
				}
				int len = parameters.length;
				ParameterMetaData meta = statement.getParameterMetaData();
				int max = Math.min(meta.getParameterCount(), parameters.length);
				Object[] passed = Arrays.copyOf(parameters, max);
				parameters = Arrays.copyOfRange(parameters, max, len);
				LocalResult result = executeStatement(statement, passed);
				results.add(result);
			}
			connection.commit();
		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException rollback) {
				throw new SQLRunnerExecuteException("Could not rollback failed commit!", rollback);
			}
			throw new SQLRunnerExecuteException("An error occured while executing the provided SQL statements!", exception);
		}
		return results;
	}

}
