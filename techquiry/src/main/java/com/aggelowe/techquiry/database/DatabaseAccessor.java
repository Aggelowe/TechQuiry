package com.aggelowe.techquiry.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.aggelowe.techquiry.exception.InvalidConstructionException;
import com.aggelowe.techquiry.exception.NonLoadableStatementException;

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
	 * given file and generates the {@link List} containing the {@link Statement}
	 * objects, ready to be executed.
	 * 
	 * @param file The file containing the SQL statements
	 * @return The list of {@link PreparedStatement} objects
	 */
	private static List<PreparedStatement> loadStatements(File file) {
		List<PreparedStatement> statements = new LinkedList<PreparedStatement>();
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException exception) {
			throw new NonLoadableStatementException("The " + file + " statement file is not accessible!", exception);
		}
		BufferedReader reader = new BufferedReader(fileReader);
		Connection connection = DatabaseController.getConnection();
		StringBuilder commandBuilder = null;
		int mode = 0;
		char previous = (char) -1;
		int code;
		try {
			while ((code = reader.read()) != -1) {
				char character = (char) code;
				if (commandBuilder == null) {
					commandBuilder = new StringBuilder();
				}
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
						if (character == '\n') {
							if (previous != ' ' && previous != '\n') {
								commandBuilder.append(' ');
							}
						} else {
							commandBuilder.append(character);
						}
						if (character == ';') {
							String command = commandBuilder.toString();
							command = command.trim();
							commandBuilder = null;
							PreparedStatement statement;
							try {
								statement = connection.prepareStatement(command);
							} catch (SQLException exception) {
								throw new NonLoadableStatementException("The statement could not be constructed!", exception);
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
			throw new NonLoadableStatementException("An exception occured while reading the statement file!", exception);
		} finally {
			try {
				reader.close();
			} catch (IOException exception) {
				throw new NonLoadableStatementException("The statement file reader could not be closed!", exception);
			}
		}
		return statements;
	}

}
