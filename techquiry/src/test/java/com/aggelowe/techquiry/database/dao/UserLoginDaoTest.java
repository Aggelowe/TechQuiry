package com.aggelowe.techquiry.database.dao;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.aggelowe.techquiry.common.SecurityUtils;
import com.aggelowe.techquiry.common.TestAppConfiguration;
import com.aggelowe.techquiry.database.exception.SQLRunnerExecuteException;
import com.aggelowe.techquiry.entity.UserLogin;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class UserLoginDaoTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	UserLoginDao userLoginDao;

	@BeforeEach
	void initialize() {
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
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(2, 'charlie', 'YWJjZGFiY2Q=', 'YWJjZA==');");
				connection.commit();
			}
		});
	}

	@AfterEach
	void destroy() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("DROP TABLE 'user_login'");
				connection.commit();
			}
		});
	}

	@Test
	void testCountSuccess() {
		int count = assertDoesNotThrow(() -> userLoginDao.count());
		assertEquals(3, count);
	}

	@Test
	void testDeleteSuccess() {
		assertDoesNotThrow(() -> userLoginDao.delete(1));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM user_login WHERE user_id = 1");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertFalse(result.next());
			}
		});
	}

	@Test
	void testInsertSuccess() {
		int id = assertDoesNotThrow(() -> userLoginDao.insert(new UserLogin(0, "david", new byte[4], new byte[2])));
		assertEquals(3, id);
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM user_login WHERE user_id = 3");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(3, result.getInt("user_id"));
				assertEquals("david", result.getString("username"));
				assertEquals(SecurityUtils.encodeBase64(new byte[4]), result.getString("password_hash"));
				assertEquals(SecurityUtils.encodeBase64(new byte[2]), result.getString("password_salt"));
				assertFalse(result.next());
			}
		});
	}

	@Test
	void testInsertException() {
		assertThrowsExactly(SQLRunnerExecuteException.class, () -> userLoginDao.insert(new UserLogin(3, "charlie", new byte[4], new byte[2])));
	}

	@Test
	void testRangeSuccess() {
		List<UserLogin> userLogins = assertDoesNotThrow(() -> userLoginDao.range(2, 1));
		assertEquals(2, userLogins.size());
		UserLogin userLogin0 = userLogins.get(0);
		assertEquals(1, userLogin0.getUserId());
		assertEquals("bob", userLogin0.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzc3dvcmQ="), userLogin0.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzcw=="), userLogin0.getPasswordSalt());
		UserLogin userLogin1 = userLogins.get(1);
		assertEquals(2, userLogin1.getUserId());
	}

	@Test
	void testSelectSuccess() {
		UserLogin userLogin = assertDoesNotThrow(() -> userLoginDao.select(1));
		assertEquals(1, userLogin.getUserId());
		assertEquals("bob", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzc3dvcmQ="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzcw=="), userLogin.getPasswordSalt());
	}

	@Test
	void testUpdateSuccess() {
		assertDoesNotThrow(() -> userLoginDao.update(new UserLogin(2, "david", new byte[4], new byte[2])));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_login WHERE user_id = 2"));
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertEquals(2, result.getInt("user_id"));
				assertEquals("david", result.getString("username"));
				assertEquals(SecurityUtils.encodeBase64(new byte[4]), result.getString("password_hash"));
				assertEquals(SecurityUtils.encodeBase64(new byte[2]), result.getString("password_salt"));
			}
		});
	}

	@Test
	void testUpdateException() {
		assertThrowsExactly(SQLRunnerExecuteException.class, () -> userLoginDao.update(new UserLogin(2, "alice", new byte[4], new byte[2])));
	}

}
