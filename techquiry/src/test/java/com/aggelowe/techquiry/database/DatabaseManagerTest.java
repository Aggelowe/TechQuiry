package com.aggelowe.techquiry.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

public class DatabaseManagerTest {

	Connection connection;
	DatabaseManager manager;

	@BeforeEach
	public void initialize() {
		String databaseUrl = "jdbc:sqlite::memory:";
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connection = assertDoesNotThrow(() -> DriverManager.getConnection(databaseUrl, config.toProperties()));
		assertDoesNotThrow(() -> connection.setAutoCommit(false));
		manager = new DatabaseManager(connection);
	}

	@AfterEach
	public void destroy() {
		if (connection != null) {
			assertDoesNotThrow(() -> connection.close());
		}
	}

	@Test
	public void testCreateSchemaSuccess() {
		assertDoesNotThrow(() -> manager.createSchema());
		Statement stmt = assertDoesNotThrow(() -> connection.createStatement());
		String sql = "SELECT name FROM sqlite_master WHERE type='table';";
		ResultSet result = assertDoesNotThrow(() -> stmt.executeQuery(sql));
		List<String> tables = new ArrayList<>();
		assertDoesNotThrow(() -> {
			while (result.next()) {
				tables.add(result.getString("name"));
			}
		});
		assertTrue(tables.contains("user_login"));
		assertTrue(tables.contains("user_data"));
		assertTrue(tables.contains("inquiry"));
		assertTrue(tables.contains("response"));
		assertTrue(tables.contains("observer"));
		assertTrue(tables.contains("upvote"));
	}

	@Test
	public void testCloseConnectionSuccess() {
		assertDoesNotThrow(() -> manager.closeConnection());
		assertThrows(SQLException.class, () -> connection.createStatement());
	}
	
}
