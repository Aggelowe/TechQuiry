package com.aggelowe.techquiry.database.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.UserData;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerExecuteException;

public class UserDataDaoTest {

	Connection connection;
	UserDataDao userDataDao;

	@BeforeEach
	public void initialize() {
		String databaseUrl = "jdbc:sqlite::memory:";
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connection = assertDoesNotThrow(() -> DriverManager.getConnection(databaseUrl, config.toProperties()));
		assertDoesNotThrow(() -> connection.setAutoCommit(false));
		userDataDao = new UserDataDao(new SQLRunner(connection));
		assertDoesNotThrow(() -> {
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS \"user_login\" (\n"
							+ "	\"user_id\" INTEGER NOT NULL UNIQUE,\n"
							+ "	\"username\" TEXT NOT NULL UNIQUE,\n"
							+ "	\"password_hash\" TEXT NOT NULL,\n"
							+ "	\"password_salt\" TEXT NOT NULL,\n"
							+ "	PRIMARY KEY(\"user_id\")\n"
							+ ");\n");
			statement.execute("CREATE TABLE IF NOT EXISTS \"user_data\" (\n"
							+ "	\"user_id\" INTEGER NOT NULL UNIQUE,\n"
							+ "	\"first_name\" TEXT NOT NULL,\n"
							+ "	\"last_name\" TEXT NOT NULL,\n"
							+ "	\"icon\" BLOB,\n"
							+ "	PRIMARY KEY(\"user_id\"),\n"
							+ "	FOREIGN KEY (\"user_id\") REFERENCES \"user_login\"(\"user_id\")\n"
							+ "	ON UPDATE CASCADE ON DELETE CASCADE\n"
							+ ");");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(2, 'charlie', 'YWJjZGFiY2Q=', 'YWJjZA==');");
			statement.execute("INSERT INTO user_data(user_id, first_name, last_name, icon) VALUES(0, 'Alice', 'Smith', X'0000');");
			statement.execute("INSERT INTO user_data(user_id, first_name, last_name, icon) VALUES(1, 'Bob', 'Johnson', NULL);");
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
	public void testDeleteSuccess() {
		assertDoesNotThrow(() -> userDataDao.delete(1));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_data WHERE user_id = 1"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertFalse(assertDoesNotThrow(() -> result.next()));
	}

	@Test
	public void testInsertSuccess() {
		assertDoesNotThrow(() -> userDataDao.insert(new UserData(2, "Charlie", "Brown")));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_data WHERE user_id = 2"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals(2, assertDoesNotThrow(() -> result.getInt("user_id")));
		assertEquals("Charlie", assertDoesNotThrow(() -> result.getString("first_name")));
		assertEquals("Brown", assertDoesNotThrow(() -> result.getString("last_name")));
		assertNull(assertDoesNotThrow(() -> result.getBytes("icon")));
		assertFalse(assertDoesNotThrow(() -> result.next()));
	}

	@Test
	public void testInsertException() {
		assertThrows(SQLRunnerExecuteException.class, () -> userDataDao.insert(new UserData(1, "Charlie", "Brown")));
		assertThrows(SQLRunnerExecuteException.class, () -> userDataDao.insert(new UserData(3, "Charlie", "Brown")));
		assertThrows(SQLRunnerExecuteException.class, () -> userDataDao.insert(new UserData(2, null, null)));
	}

	@Test
	public void testSelectSuccess() {
		UserData userData = assertDoesNotThrow(() -> userDataDao.select(1));
		assertEquals(1, userData.getId());
		assertEquals("Bob", userData.getFirstName());
		assertEquals("Johnson", userData.getLastName());
		assertNull(userData.getIcon());
	}

	@Test
	public void testUpdateSuccess() {
		assertDoesNotThrow(() -> userDataDao.update(new UserData(1, "David", "Dawson")));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_data WHERE user_id = 1"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals(1, assertDoesNotThrow(() -> result.getInt("user_id")));
		assertEquals("David", assertDoesNotThrow(() -> result.getString("first_name")));
		assertEquals("Dawson", assertDoesNotThrow(() -> result.getString("last_name")));
		assertNull(assertDoesNotThrow(() -> result.getBytes("icon")));
	}

	@Test
	public void testUpdateException() {
		assertThrows(SQLRunnerExecuteException.class, () -> userDataDao.update(new UserData(1, null, null)));
	}

}
