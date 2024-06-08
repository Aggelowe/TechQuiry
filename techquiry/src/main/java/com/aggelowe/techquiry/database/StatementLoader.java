package com.aggelowe.techquiry.database;

import static com.aggelowe.techquiry.common.Constants.SQL_DIRECTORY;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.aggelowe.techquiry.common.Constants;
import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.database.exceptions.SQLScriptException;

/**
 * The {@link StatementLoader} class is responsible for loading the SQL
 * statements from the respective SQL script files and preparing them for
 * execution by the application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class StatementLoader {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever
	 * invoked. {@link StatementLoader} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private StatementLoader() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * The {@link #loadStatement(InputStream)} method parses the SQL statements from
	 * the provided {@link InputStream} pointing to the SQL script file and
	 * generates the {@link List} containing the {@link Statement} objects, ready to
	 * be executed.
	 * 
	 * @param stream The stream reading the file containing the SQL statements
	 * @return The list of {@link PreparedStatement} objects
	 */
	public static List<PreparedStatement> loadStatements(InputStream stream) {
		List<PreparedStatement> statements = new LinkedList<PreparedStatement>();
		BufferedInputStream buffer = new BufferedInputStream(stream);
		Connection connection = DatabaseInitializer.getConnection();
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
	 * @param name The name of the file containing the SQL statements
	 * @return The list of {@link PreparedStatement} objects
	 */
	public static List<PreparedStatement> loadStatements(String name) {
		String resource = SQL_DIRECTORY + name;
		InputStream stream = StatementLoader.class.getResourceAsStream(resource);
		List<PreparedStatement> statements = loadStatements(stream);
		return statements;
	}

}
