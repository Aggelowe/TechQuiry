package com.aggelowe.techquiry.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.database.exceptions.SQLRunnerException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerExecuteException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerLoadException;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link SQLRunner} class is responsible for executing the provided SQL scripts on the predefined connection with the application's
 * database.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
@Log4j2
public final class SQLRunner {

    private final DataSource dataSource;

    /**
     * This constructor constructs a new {@link SQLRunner} instance with the provided connection as the interface between the application
     * and the database.
     * 
     * @param connection
     *            The database connection
     */
    @Autowired
    public SQLRunner(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This method loads the SQL statements from the provided {@link InputStream} pointing to the SQL script file, executes them on the
     * preset connection and returns the {@link List} containing the {@link ResultSet} objects.
     * 
     * @param stream
     *            The stream reading the file containing the SQL statements
     * @param parameters
     *            The parameters for the statements
     * @return The list of {@link ResultSet} objects
     * @throws SQLRunnerException
     *             If an error occurs while loading or running the script
     */
    public List<LocalResult> runScript(InputStream stream, Object... parameters) throws SQLRunnerException {
        try (Connection connection = dataSource.getConnection()) {
            List<PreparedStatement> statements = loadStatements(connection, stream);
            return executeStatements(connection, statements, parameters).stream().map(rs -> {
                try {
                    return parseResultSet(rs);
                } catch (SQLRunnerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }).toList();
        } catch (SQLException exception) {
            throw new SQLRunnerExecuteException("Could not get connection!", exception);
        }
    }

    /**
     * This method loads the SQL statements from the SQL script file with the given path, executes them on the preset connection and returns
     * the {@link List} containing the {@link ResultSet} objects.
     * 
     * @param stream
     *            The stream reading the file containing the SQL statements
     * @param parameters
     *            The parameters for the statements
     * @return The list of {@link ResultSet} objects
     * @throws SQLRunnerException
     *             If an error occurs while loading or running the script
     */
    public List<LocalResult> runScript(String path, Object... parameters) throws SQLRunnerException {
        InputStream stream = SQLRunner.class.getResourceAsStream(path);
        return runScript(stream, parameters);
    }

    /**
     * This method prepares the given statement, executes it on the preset connection and returns the output {@link ResultSet} object.
     * 
     * @param statement
     *            The statement to execute
     * @param parameters
     *            The parameters of the statement
     * @return The result of the execution of the statement
     * @throws SQLRunnerException
     *             If an error occurs while loading or running the script
     */
    public LocalResult runStatement(String statement, Object... parameters) throws SQLRunnerException {
        try (Connection connection = dataSource.getConnection()) {
            ResultSet result;
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
            return parseResultSet(result);
        } catch (SQLException exception) {
            throw new SQLRunnerExecuteException("Could not get connection!", exception);
        }

    }

    /**
     * The {@link #loadStatement(InputStream)} method parses the SQL statements from the provided {@link InputStream} pointing to the SQL
     * script file and generates the {@link List} containing the {@link PreparedStatement} objects, ready to be executed.
     * 
     * @param connection
     *            TODO
     * @param stream
     *            The stream reading the file containing the SQL statements
     * 
     * @return The list of {@link PreparedStatement} objects
     * @throws SQLRunnerLoadException
     *             If an error occurs while the statements are being loaded
     */
    private List<PreparedStatement> loadStatements(Connection connection, InputStream stream) throws SQLRunnerLoadException {
        List<PreparedStatement> statements = new LinkedList<PreparedStatement>();
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

    @Data
    @NoArgsConstructor
    public class LocalResult {
        List<Map<String, Object>> r = new ArrayList<>();
    }

    private LocalResult parseResultSet(ResultSet rs) throws SQLRunnerException {
        LocalResult lr = new LocalResult();
        if (rs == null) {
            return null;
        }
        ResultSetMetaData metaData;
        try {
            metaData = rs.getMetaData();
            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns.add(metaData.getColumnLabel(i));
            }
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (String column : columns) {
                    map.put(column, rs.getObject(column));
                }
                lr.getR().add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lr;
    }

    /**
     * This method executes the given SQL statement with the provided parameters in the TechQuiry database and then returns the
     * {@link ResultSet} containing the results of the executed statement.
     * 
     * @param statement
     *            The {@link PreparedStatement} to execute
     * @param parameters
     *            The parameters for the statement
     * @return The results of the execution
     * @throws SQLRunnerExecuteException
     *             If an error occurs while executing the statement
     */
    private ResultSet executeStatement(PreparedStatement statement, Object... parameters) throws SQLRunnerExecuteException {
        ResultSet result;
        try {
            int index = 1;
            for (Object parameter : parameters) {
                statement.setObject(index, parameter);
                index++;
            }
            statement.execute();
            result = statement.getResultSet();
        } catch (SQLException exception) {
            throw new SQLRunnerExecuteException("An error occured while executing the given statement!", exception);
        }
        return result;
    }

    /**
     * This method executes the given list of statements with the provided parameters in TechQuiry's database and then returns a list
     * containing the result of each executed SQL statement.
     * 
     * @param connection
     *            TODO
     * @param statements
     *            The list of statements to execute
     * @param parameters
     *            The parameters for the statements
     * 
     * @return The list of the result of each executed statement
     * @throws SQLRunnerExecuteException
     *             If an error occurs while executing the statements
     */
    private List<ResultSet> executeStatements(Connection connection, List<PreparedStatement> statements, Object... parameters)
            throws SQLRunnerExecuteException {
        List<ResultSet> results = new ArrayList<>(statements.size());
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
                ResultSet result = executeStatement(statement, passed);
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
