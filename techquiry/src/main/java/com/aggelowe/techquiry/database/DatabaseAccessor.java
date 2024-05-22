package com.aggelowe.techquiry.database;

import static com.aggelowe.techquiry.Reference.LOGGER;
import static com.aggelowe.techquiry.Reference.SQL_DIRECTORY;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.aggelowe.techquiry.exception.InvalidConstructionException;
import com.aggelowe.techquiry.exception.SQLScriptException;

/**
 * The {@link DatabaseAccessor} class is responsible for making the necessary
 * statements to TechQuiry's database for the retrieval or modification of the
 * application's data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class DatabaseAccessor {

	/**
	 * This constructor will throw an {@link InvalidConstructionException} whenever
	 * invoked. {@link DatabaseAccessor} objects should <b>not</b> be constructible.
	 * 
	 * @throws InvalidConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private DatabaseAccessor() {
		throw new InvalidConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * The {@link #loadStatement(String)} method parses the SQL statements from the
	 * provided {@link InputStream} pointing to the SQL script file and generates
	 * the {@link List} containing the {@link Statement} objects, ready to be
	 * executed.
	 * 
	 * @param stream The stream reading the file containing the SQL statements
	 * @return The list of {@link PreparedStatement} objects
	 */
	private static List<PreparedStatement> loadStatements(InputStream stream) {
		List<PreparedStatement> statements = new LinkedList<PreparedStatement>();
		BufferedInputStream buffer = new BufferedInputStream(stream);
		Connection connection = DatabaseController.getConnection();
		StringBuilder commandBuilder = null;
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
	 * This method is responsible for creating the <i>users</i> table in the
	 * application database by executing the first statement in the respective SQL
	 * script file.
	 */
	public static void createUsersTable() {
		LOGGER.debug("Creating user database table if missing");
		String name = "create_users_table.sql";
		InputStream stream = DatabaseAccessor.class.getResourceAsStream(SQL_DIRECTORY + name);
		List<PreparedStatement> statements = loadStatements(stream);
		PreparedStatement statement;
		try {
			statement = statements.getFirst();
		} catch (NoSuchElementException exception) {
			throw new SQLScriptException("The first statement in " + name + " is not found!");
		}
		try {
			statement.execute();
		} catch (SQLException exception) {
			throw new SQLScriptException("A database error occured on the SQL statement's execution!", exception);
		}
	}

}
