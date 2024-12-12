package com.aggelowe.techquiry.database.dao;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import com.aggelowe.techquiry.common.SecurityUtils;
import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerExecuteException;

public class UserLoginDaoTest {

	Connection connection;
	UserLoginDao userLoginDao;

	@BeforeEach
	public void initialize() {
		String databaseUrl = "jdbc:sqlite::memory:";
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connection = assertDoesNotThrow(() -> DriverManager.getConnection(databaseUrl, config.toProperties()));
		assertDoesNotThrow(() -> connection.setAutoCommit(false));
		userLoginDao = new UserLoginDao(new SQLRunner(connection));
		assertDoesNotThrow(() -> {
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS \"user_login\" (\n"
							+ "	\"user_id\" INTEGER NOT NULL UNIQUE,\n"
							+ "	\"username\" TEXT NOT NULL UNIQUE,\n"
							+ "	\"password_hash\" TEXT NOT NULL,\n"
							+ "	\"password_salt\" TEXT NOT NULL,\n"
							+ "	PRIMARY KEY(\"user_id\")\n"
							+ ");\n");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(2, 'charlie', 'YWJjZGFiY2Q=', 'YWJjZA==');");
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
	public void testCountSuccess() {
		int count = assertDoesNotThrow(() -> userLoginDao.count());
		assertEquals(3, count);
	}

	@Test
	public void testDeleteSuccess() {
		assertDoesNotThrow(() -> userLoginDao.delete(1));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_login WHERE user_id = 1"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertFalse(assertDoesNotThrow(() -> result.next()));
	}

	@Test
	public void testInsertSuccess() {
		int id = assertDoesNotThrow(() -> userLoginDao.insert(new UserLogin(0, "david", new byte[4], new byte[2])));
		assertEquals(3, id);
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_login WHERE user_id = 3"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals(3, assertDoesNotThrow(() -> result.getInt("user_id")));
		assertEquals("david", assertDoesNotThrow(() -> result.getString("username")));
		assertEquals(SecurityUtils.encodeBase64(new byte[4]), assertDoesNotThrow(() -> result.getString("password_hash")));
		assertEquals(SecurityUtils.encodeBase64(new byte[2]), assertDoesNotThrow(() -> result.getString("password_salt")));
		assertFalse(assertDoesNotThrow(() -> result.next()));
	}

	@Test
	public void testInsertException() {
		assertThrows(SQLRunnerExecuteException.class, () -> userLoginDao.insert(new UserLogin(3, "charlie", new byte[4], new byte[2])));
		assertThrows(SQLRunnerExecuteException.class, () -> userLoginDao.insert(new UserLogin(3, null, new byte[0], new byte[0])));
	}

	@Test
	public void testRangeSuccess() {
		List<UserLogin> userLogins = assertDoesNotThrow(() -> userLoginDao.range(2, 1));
		assertEquals(2, userLogins.size());
		UserLogin userLogin0 = userLogins.get(0);
		assertEquals(1, userLogin0.getId());
		assertEquals("bob", userLogin0.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzc3dvcmQ="), userLogin0.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzcw=="), userLogin0.getPasswordSalt());
		UserLogin userLogin1 = userLogins.get(1);
		assertEquals(2, userLogin1.getId());
	}

	@Test
	public void testSelectSuccess() {
		UserLogin userLogin = assertDoesNotThrow(() -> userLoginDao.select(1));
		assertEquals(1, userLogin.getId());
		assertEquals("bob", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzc3dvcmQ="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzcw=="), userLogin.getPasswordSalt());
	}

	@Test
	public void testUpdateSuccess() {
		assertDoesNotThrow(() -> userLoginDao.update(new UserLogin(2, "david", new byte[4], new byte[2])));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_login WHERE user_id = 2"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertEquals(2, assertDoesNotThrow(() -> result.getInt("user_id")));
		assertEquals("david", assertDoesNotThrow(() -> result.getString("username")));
		assertEquals(SecurityUtils.encodeBase64(new byte[4]), assertDoesNotThrow(() -> result.getString("password_hash")));
		assertEquals(SecurityUtils.encodeBase64(new byte[2]), assertDoesNotThrow(() -> result.getString("password_salt")));
	}

	@Test
	public void testUpdateException() {
		assertThrows(SQLRunnerExecuteException.class, () -> userLoginDao.update(new UserLogin(2, "alice", new byte[4], new byte[2])));
		assertThrows(SQLRunnerExecuteException.class, () -> userLoginDao.update(new UserLogin(2, null, new byte[4], new byte[2])));
	}

}
