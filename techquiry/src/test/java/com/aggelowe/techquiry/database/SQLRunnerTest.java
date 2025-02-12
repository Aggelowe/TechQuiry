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
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.aggelowe.techquiry.common.TestAppConfiguration;
import com.aggelowe.techquiry.database.exception.SQLRunnerExecuteException;
import com.aggelowe.techquiry.database.exception.SQLRunnerLoadException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class SQLRunnerTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	SQLRunner runner;

	@BeforeEach
	void initialize() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("CREATE TABLE test (id INTEGER PRIMARY KEY, username TEXT NOT NULL)");
				statement.execute("INSERT INTO test (id, username) VALUES (0, 'Alice')");
				statement.execute("INSERT INTO test (id, username) VALUES (1, 'Bob')");
				connection.commit();
			}
		});
	}

	@AfterEach
	void destroy() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("DROP TABLE test");
				connection.commit();
			}
		});
	}

	@Test
	void testRunStatementSuccess() {
		String sql = "SELECT * FROM test WHERE id = ?";
		LocalResult result = assertDoesNotThrow(() -> runner.runStatement(sql, 0));
		assertNotNull(result);
		Iterator<Map<String, Object>> iterator = result.iterator();
		assertTrue(assertDoesNotThrow(() -> iterator.hasNext()));
		assertEquals("Alice", assertDoesNotThrow(() -> iterator.next().get("username")));
	}

	@Test
	void testRunStatementException() {
		String false0 = "SELECT * FROM false";
		assertThrows(SQLRunnerExecuteException.class, () -> runner.runStatement(false0));
		String false1 = "SLECT *";
		assertThrows(SQLRunnerExecuteException.class, () -> runner.runStatement(false1));
		String false2 = "INSERT INTO test (id, username) VALUES (1, 'Charlie')";
		assertThrows(SQLRunnerExecuteException.class, () -> runner.runStatement(false2));
	}

	@Test
	void testRunScriptSuccess() {
		String sql = "INSERT INTO test (id, username) /* Comment 1 */ VALUES (?, ?);;\n SELECT * -- Comment 2 \n FROM test WHERE id = ?";
		InputStream stream = new ByteArrayInputStream(sql.getBytes());
		List<LocalResult> results = assertDoesNotThrow(() -> runner.runScript(stream, 2, "Charlie", 1));
		assertEquals(2, results.size());
		assertNull(results.get(0));
		LocalResult result = results.get(1);
		assertNotNull(result);
		Iterator<Map<String, Object>> iterator = result.iterator();
		assertTrue(assertDoesNotThrow(() -> iterator.hasNext()));
		assertEquals("Bob", assertDoesNotThrow(() -> iterator.next().get("username")));
	}

	@Test
	void testRunScriptException() {
		String false0 = "SLECT *; INSERT INTO test (id, username) VALUES (?, ?);";
		InputStream stream0 = new ByteArrayInputStream(false0.getBytes());
		assertThrows(SQLRunnerLoadException.class, () -> runner.runScript(stream0));
		String false1 = "SELECT * FROM test; INSERT INTO test (id, username) VALUES (?, ?);";
		InputStream stream1 = new ByteArrayInputStream(false1.getBytes());
		assertThrows(SQLRunnerExecuteException.class, () -> runner.runScript(stream1, 1, "Charlie"));
	}

}
