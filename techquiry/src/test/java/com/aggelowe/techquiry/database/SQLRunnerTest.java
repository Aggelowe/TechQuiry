package com.aggelowe.techquiry.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import com.aggelowe.techquiry.database.exceptions.SQLRunnerExecuteException;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerLoadException;

public class SQLRunnerTest {

	Connection connection;
	SQLRunner runner;

	@BeforeEach
	public void initialize() {
		String databaseUrl = "jdbc:sqlite::memory:";
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connection = assertDoesNotThrow(() -> DriverManager.getConnection(databaseUrl, config.toProperties()));
		assertDoesNotThrow(() -> connection.setAutoCommit(false));
		runner = new SQLRunner(connection);
		assertDoesNotThrow(() -> {
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE test (id INTEGER PRIMARY KEY, username TEXT NOT NULL)");
			statement.execute("INSERT INTO test (id, username) VALUES (0, 'Alice')");
			statement.execute("INSERT INTO test (id, username) VALUES (1, 'Bob')");
			connection.commit();
		});
	}

	@AfterEach
	public void destroy() {
		if (connection != null) {
			assertDoesNotThrow(() -> connection.close());
		}
	}

	@Test
	public void testRunStatementSuccess() {
		String sql = "SELECT * FROM test WHERE id = ?";
		ResultSet result = assertDoesNotThrow(() -> runner.runStatement(sql, 0));
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals("Alice", assertDoesNotThrow(() -> result.getString("username")));
	}

	@Test
	public void testRunStatementException() {
		String false0 = "SELECT * FROM false";
		assertThrows(SQLRunnerExecuteException.class, () -> runner.runStatement(false0));
		String false1 = "SLECT *";
		assertThrows(SQLRunnerExecuteException.class, () -> runner.runStatement(false1));
		String false2 = "INSERT INTO test (id, username) VALUES (1, 'Charlie')";
		assertThrows(SQLRunnerExecuteException.class, () -> runner.runStatement(false2));
	}

	@Test
	public void testRunScriptSuccess() {
		String sql = "INSERT INTO test (id, username) /* Comment 1 */ VALUES (?, ?);;\n SELECT * -- Comment 2 \n FROM test WHERE id = ?";
		InputStream stream = new ByteArrayInputStream(sql.getBytes());
		List<ResultSet> results = assertDoesNotThrow(() -> runner.runScript(stream, 2, "Charlie", 1));
		assertEquals(2, results.size());
		assertNull(results.get(0));
		ResultSet result = results.get(1);
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals("Bob", assertDoesNotThrow(() -> result.getString("username")));
	}

	@Test
	public void testRunScriptException() {
		String false0 = "SLECT *; INSERT INTO test (id, username) VALUES (?, ?);";
		InputStream stream0 = new ByteArrayInputStream(false0.getBytes());
		assertThrows(SQLRunnerLoadException.class, () -> runner.runScript(stream0));
		String false1 = "SELECT * FROM test; INSERT INTO test (id, username) VALUES (?, ?);";
		InputStream stream1 = new ByteArrayInputStream(false1.getBytes());
		assertThrows(SQLRunnerExecuteException.class, () -> runner.runScript(stream1, 1, "Charlie"));
	}

}
