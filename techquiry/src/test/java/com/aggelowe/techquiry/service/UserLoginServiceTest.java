package com.aggelowe.techquiry.service;

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
import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exceptions.InvalidRequestException;

public class UserLoginServiceTest {

	Connection connection;
	UserLoginService userLoginService;

	@BeforeEach
	public void initialize() {
		String databaseUrl = "jdbc:sqlite::memory:";
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connection = assertDoesNotThrow(() -> DriverManager.getConnection(databaseUrl, config.toProperties()));
		assertDoesNotThrow(() -> connection.setAutoCommit(false));
		DatabaseManager manager = new DatabaseManager(connection);
		userLoginService = new UserLoginService(manager);
		assertDoesNotThrow(() -> {
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS \"user_login\" (\n"
					+ "	\"user_id\" INTEGER NOT NULL UNIQUE,\n"
					+ "	\"username\" TEXT NOT NULL UNIQUE,\n"
					+ "	\"password_hash\" TEXT NOT NULL,\n"
					+ "	\"password_salt\" TEXT NOT NULL,\n"
					+ "	PRIMARY KEY(\"user_id\")\n"
					+ ");\n");			
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', '5wq4WUIgP1dwqcr2Bela/SHzJwyUvIqo89/vHk565Lc=', 'nGxxd6QsFeF/cBeR5tgiIA==');");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'ptp5i/V5DHjaOsFQfCo7NseUflYX45loc9DTSPrl+NU=', 'Fw7zNLq9p0L1bT68ifEz9g==');");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(2, 'charlie', 'dm2H/fl9TtxWBKW5dN5nh9MRUNTbWuFM3xquxwQ+VC4=', 'jgKZJ7psArGnRao9N464eg==');");
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
	public void testGetLoginCountSuccess() {
		int count = assertDoesNotThrow(() -> userLoginService.getLoginCount());
		assertEquals(3, count);
	}

	@Test
	public void testGetLoginRangeSuccess() {
		List<UserLogin> userLogins = assertDoesNotThrow(() -> userLoginService.getLoginRange(2, 1));
		assertEquals(1, userLogins.size());
		UserLogin userLogin = userLogins.get(0);
		assertEquals(2, userLogin.getId());
		assertEquals("charlie", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("dm2H/fl9TtxWBKW5dN5nh9MRUNTbWuFM3xquxwQ+VC4="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("jgKZJ7psArGnRao9N464eg=="), userLogin.getPasswordSalt());
	}

	@Test
	public void testFindLoginByUserIdSuccess() {
		UserLogin userLogin = assertDoesNotThrow(() -> userLoginService.findLoginByUserId(1));
		assertEquals(1, userLogin.getId());
		assertEquals("bob", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("ptp5i/V5DHjaOsFQfCo7NseUflYX45loc9DTSPrl+NU="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("Fw7zNLq9p0L1bT68ifEz9g=="), userLogin.getPasswordSalt());
	}

	@Test
	public void testFindLoginByUserIdException() {
		assertThrows(EntityNotFoundException.class, () -> userLoginService.findLoginByUserId(3));
	}

	@Test
	public void testFindLoginByUsernameSuccess() {
		UserLogin userLogin = assertDoesNotThrow(() -> userLoginService.findLoginByUsername("bob"));
		assertEquals(1, userLogin.getId());
		assertEquals("bob", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("ptp5i/V5DHjaOsFQfCo7NseUflYX45loc9DTSPrl+NU="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("Fw7zNLq9p0L1bT68ifEz9g=="), userLogin.getPasswordSalt());
	}

	@Test
	public void testFindLoginByUsernameException() {
		assertThrows(EntityNotFoundException.class, () -> userLoginService.findLoginByUsername("david"));
	}

	@Test
	public void testAuthenticateUserSuccess() {
		UserLogin userLogin = assertDoesNotThrow(() -> userLoginService.authenticateUser("bob", "pass"));
		assertEquals(1, userLogin.getId());
		assertEquals("bob", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("ptp5i/V5DHjaOsFQfCo7NseUflYX45loc9DTSPrl+NU="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("Fw7zNLq9p0L1bT68ifEz9g=="), userLogin.getPasswordSalt());
	}

	@Test
	public void testAuthenticateUserException() {
		assertThrows(InvalidRequestException.class, () -> userLoginService.authenticateUser("bob", "word"));
		assertThrows(InvalidRequestException.class, () -> userLoginService.authenticateUser("david", "pass"));
	}

	@Test
	public void testCreateLoginSuccess() {
		UserLogin target = new UserLogin(0, "david", "extra");
		int id = assertDoesNotThrow(() -> userLoginService.createActionService(null).createLogin(target));
		assertEquals(3, id);
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_login WHERE user_id = 3"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals(3, assertDoesNotThrow(() -> result.getInt("user_id")));
		assertEquals("david", assertDoesNotThrow(() -> result.getString("username")));
		String hash = SecurityUtils.encodeBase64(target.getPasswordHash());
		String salt = SecurityUtils.encodeBase64(target.getPasswordSalt());
		assertEquals(hash, assertDoesNotThrow(() -> result.getString("password_hash")));
		assertEquals(salt, assertDoesNotThrow(() -> result.getString("password_salt")));
	}

	@Test
	public void testCreateLoginException() {
		UserLogin current = new UserLogin(0, "david", "extra");
		UserLogin target0 = new UserLogin(0, "emily", "password");
		assertThrows(ForbiddenOperationException.class, () -> userLoginService.createActionService(current).createLogin(target0));
		UserLogin target1 = new UserLogin(0, "Εμιλία!", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginService.createActionService(null).createLogin(target1));
		UserLogin target2 = new UserLogin(0, "__xX__Emily__Xx__", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginService.createActionService(null).createLogin(target2));
		UserLogin target3 = new UserLogin(0, "em", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginService.createActionService(null).createLogin(target3));
		UserLogin target4 = new UserLogin(0, "alice", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginService.createActionService(null).createLogin(target4));
	}

	@Test
	public void testDeleteLoginSuccess() {
		UserLogin login = new UserLogin(1, "bob", "pass");
		assertDoesNotThrow(() -> userLoginService.createActionService(login).deleteLogin(1));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_login WHERE user_id = 1"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertFalse(assertDoesNotThrow(() -> result.next()));
	}

	@Test
	public void testDeleteLoginException() {
		assertThrows(ForbiddenOperationException.class, () -> userLoginService.createActionService(null).deleteLogin(1));
		UserLogin current0 = new UserLogin(1, "bob", "pass");
		assertThrows(ForbiddenOperationException.class, () -> userLoginService.createActionService(current0).deleteLogin(0));
		UserLogin current1 = new UserLogin(3, "david", "extra");
		assertThrows(EntityNotFoundException.class, () -> userLoginService.createActionService(current1).deleteLogin(3));
	}

	@Test
	public void testUpdateLoginSuccess() {
		UserLogin login = new UserLogin(2, "david", "extra");
		assertDoesNotThrow(() -> userLoginService.createActionService(login).updateLogin(login));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_login WHERE user_id = 2"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertEquals(2, assertDoesNotThrow(() -> result.getInt("user_id")));
		assertEquals("david", assertDoesNotThrow(() -> result.getString("username")));
		String hash = SecurityUtils.encodeBase64(login.getPasswordHash());
		String salt = SecurityUtils.encodeBase64(login.getPasswordSalt());
		assertEquals(hash, assertDoesNotThrow(() -> result.getString("password_hash")));
		assertEquals(salt, assertDoesNotThrow(() -> result.getString("password_salt")));
	}

	@Test
	public void testUpdateLoginException() {
		UserLogin target = new UserLogin(2, "david", "extra");
		assertThrows(ForbiddenOperationException.class, () -> userLoginService.createActionService(null).updateLogin(target));
		UserLogin current = new UserLogin(1, "bob", "pass");
		assertThrows(ForbiddenOperationException.class, () -> userLoginService.createActionService(current).updateLogin(target));
		UserLogin login0 = new UserLogin(3, "david", "extra");
		assertThrows(EntityNotFoundException.class, () -> userLoginService.createActionService(login0).updateLogin(login0));
		UserLogin login1 = new UserLogin(2, "em", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginService.createActionService(login1).updateLogin(login1));
		UserLogin login2 = new UserLogin(2, "alice", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginService.createActionService(login2).updateLogin(login2));
	}

}
