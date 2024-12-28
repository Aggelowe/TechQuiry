package com.aggelowe.techquiry.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.aggelowe.techquiry.database.common.TestAppConfiguration;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.service.action.UserLoginActionService;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exceptions.InvalidRequestException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
public class UserLoginServiceTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	UserLoginService userLoginService;

	@Autowired
	UserLoginActionService userLoginActionService;

	@Autowired
	SessionHelper sessionHelper;

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
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', '5wq4WUIgP1dwqcr2Bela/SHzJwyUvIqo89/vHk565Lc=', 'nGxxd6QsFeF/cBeR5tgiIA==');");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'ptp5i/V5DHjaOsFQfCo7NseUflYX45loc9DTSPrl+NU=', 'Fw7zNLq9p0L1bT68ifEz9g==');");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(2, 'charlie', 'dm2H/fl9TtxWBKW5dN5nh9MRUNTbWuFM3xquxwQ+VC4=', 'jgKZJ7psArGnRao9N464eg==');");
				connection.commit();
			}
		});
	}

	@AfterEach
	public void destroy() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("DROP TABLE 'user_login'");
				connection.commit();
			}
		});
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
		Authentication authentication = assertDoesNotThrow(() -> userLoginService.authenticateUser("bob", "pass"));
		assertEquals(1, authentication.getUserId());
	}

	@Test
	public void testAuthenticateUserException() {
		assertThrows(InvalidRequestException.class, () -> userLoginService.authenticateUser("bob", "word"));
		assertThrows(InvalidRequestException.class, () -> userLoginService.authenticateUser("david", "pass"));
	}

	@Test
	public void testCreateLoginSuccess() {
		UserLogin target = new UserLogin(0, "david", "extra");
		sessionHelper.setAuthentication(null);
		int id = assertDoesNotThrow(() -> userLoginActionService.createLogin(target));
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
				String hash = SecurityUtils.encodeBase64(target.getPasswordHash());
				String salt = SecurityUtils.encodeBase64(target.getPasswordSalt());
				assertEquals(hash, result.getString("password_hash"));
				assertEquals(salt, result.getString("password_salt"));
			}
		});
	}

	@Test
	public void testCreateLoginException() {
		UserLogin target0 = new UserLogin(0, "emily", "password");
		sessionHelper.setAuthentication(new Authentication(0));
		assertThrows(ForbiddenOperationException.class, () -> userLoginActionService.createLogin(target0));
		UserLogin target1 = new UserLogin(0, "Εμιλία!", "password");
		sessionHelper.setAuthentication(null);
		assertThrows(InvalidRequestException.class, () -> userLoginActionService.createLogin(target1));
		UserLogin target2 = new UserLogin(0, "__xX__Emily__Xx__", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginActionService.createLogin(target2));
		UserLogin target3 = new UserLogin(0, "em", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginActionService.createLogin(target3));
		UserLogin target4 = new UserLogin(0, "alice", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginActionService.createLogin(target4));
	}

	@Test
	public void testDeleteLoginSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		assertDoesNotThrow(() -> userLoginActionService.deleteLogin(1));
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
	public void testDeleteLoginException() {
		sessionHelper.setAuthentication(null);
		assertThrows(ForbiddenOperationException.class, () -> userLoginActionService.deleteLogin(1));
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrows(ForbiddenOperationException.class, () -> userLoginActionService.deleteLogin(0));
		sessionHelper.setAuthentication(new Authentication(3));
		assertThrows(EntityNotFoundException.class, () -> userLoginActionService.deleteLogin(3));
	}

	@Test
	public void testUpdateLoginSuccess() {
		UserLogin login = new UserLogin(2, "david", "extra");
		sessionHelper.setAuthentication(new Authentication(2));
		assertDoesNotThrow(() -> userLoginActionService.updateLogin(login));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM user_login WHERE user_id = 2");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertEquals(2, result.getInt("user_id"));
				assertEquals("david", result.getString("username"));
				String hash = SecurityUtils.encodeBase64(login.getPasswordHash());
				String salt = SecurityUtils.encodeBase64(login.getPasswordSalt());
				assertEquals(hash, result.getString("password_hash"));
				assertEquals(salt, result.getString("password_salt"));
			}
		});
	}

	@Test
	public void testUpdateLoginException() {
		UserLogin target = new UserLogin(2, "david", "extra");
		sessionHelper.setAuthentication(null);
		assertThrows(ForbiddenOperationException.class, () -> userLoginActionService.updateLogin(target));
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrows(ForbiddenOperationException.class, () -> userLoginActionService.updateLogin(target));
		UserLogin login0 = new UserLogin(3, "david", "extra");
		sessionHelper.setAuthentication(new Authentication(3));
		assertThrows(EntityNotFoundException.class, () -> userLoginActionService.updateLogin(login0));
		UserLogin login1 = new UserLogin(2, "em", "password");
		sessionHelper.setAuthentication(new Authentication(2));
		assertThrows(InvalidRequestException.class, () -> userLoginActionService.updateLogin(login1));
		UserLogin login2 = new UserLogin(2, "alice", "password");
		assertThrows(InvalidRequestException.class, () -> userLoginActionService.updateLogin(login2));
	}

}
