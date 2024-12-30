package com.aggelowe.techquiry.database.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.aggelowe.techquiry.database.common.TestAppConfiguration;
import com.aggelowe.techquiry.database.entity.UserData;
import com.aggelowe.techquiry.database.exception.SQLRunnerExecuteException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
public class UserDataDaoTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	UserDataDao userDataDao;

	@BeforeEach
	public void initialize() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("""
						CREATE TABLE IF NOT EXISTS 'user_login' (
								'user_id' INTEGER NOT NULL UNIQUE,
								'username' TEXT NOT NULL UNIQUE,
								'password_hash' TEXT NOT NULL,
								'password_salt' TEXT NOT NULL,
								PRIMARY KEY('user_id')
						);
						""");
				statement.execute("""
						CREATE TABLE IF NOT EXISTS 'user_data' (
								'user_id' INTEGER NOT NULL UNIQUE,
								'first_name' TEXT NOT NULL,
								'last_name' TEXT NOT NULL,
								'icon' BLOB,
								PRIMARY KEY('user_id'),
								FOREIGN KEY ('user_id') REFERENCES 'user_login'('user_id')
								ON UPDATE CASCADE ON DELETE CASCADE
						);
						""");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(2, 'charlie', 'YWJjZGFiY2Q=', 'YWJjZA==');");
				statement.execute("INSERT INTO user_data(user_id, first_name, last_name, icon) VALUES(0, 'Alice', 'Smith', X'0000');");
				statement.execute("INSERT INTO user_data(user_id, first_name, last_name, icon) VALUES(1, 'Bob', 'Johnson', NULL);");
				connection.commit();
			}
		});
	}

	@AfterEach
	public void destroy() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("DROP TABLE 'user_data'");
				statement.execute("DROP TABLE 'user_login'");
				connection.commit();
			}
		});
	}

	@Test
	public void testDeleteSuccess() {
		assertDoesNotThrow(() -> userDataDao.delete(1));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM user_data WHERE user_id = 1");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertFalse(result.next());
			}
		});
	}

	@Test
	public void testInsertSuccess() {
		assertDoesNotThrow(() -> userDataDao.insert(new UserData(2, "Charlie", "Brown")));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM user_data WHERE user_id = 2");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(2, result.getInt("user_id"));
				assertEquals("Charlie", result.getString("first_name"));
				assertEquals("Brown", result.getString("last_name"));
				assertNull(result.getBytes("icon"));
				assertFalse(result.next());
			}
		});
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
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM user_data WHERE user_id = 1");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(1, result.getInt("user_id"));
				assertEquals("David", result.getString("first_name"));
				assertEquals("Dawson", result.getString("last_name"));
				assertNull(assertDoesNotThrow(() -> result.getBytes("icon")));
			}
		});
	}

	@Test
	public void testUpdateException() {
		assertThrows(SQLRunnerExecuteException.class, () -> userDataDao.update(new UserData(1, null, null)));
	}

}
