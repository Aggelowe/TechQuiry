package net.techquiry.app.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
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

import net.techquiry.app.common.TestAppConfiguration;
import net.techquiry.app.entity.UserData;
import net.techquiry.app.service.action.UserDataActionService;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.ForbiddenOperationException;
import net.techquiry.app.service.exception.InvalidRequestException;
import net.techquiry.app.service.exception.UnauthorizedOperationException;
import net.techquiry.app.service.session.Authentication;
import net.techquiry.app.service.session.SessionHelper;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class UserDataServiceTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	UserDataService userDataService;

	@Autowired
	UserDataActionService userDataActionService;

	@Autowired
	SessionHelper sessionHelper;

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
	void destroy() {
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
	void testGetDataByUserIdSuccess() {
		UserData userData = assertDoesNotThrow(() -> userDataService.getDataByUserId(1));
		assertEquals(1, userData.getUserId());
		assertEquals("Bob", userData.getFirstName());
		assertEquals("Johnson", userData.getLastName());
		assertEquals(null, userData.getIcon());
	}

	@Test
	void testGetDataByUserIdException() {
		assertThrowsExactly(EntityNotFoundException.class, () -> userDataService.getDataByUserId(2));
	}

	@Test
	void testCreateDataSuccess() {
		UserData target = new UserData(0, "Charlie", "Brown", null);
		sessionHelper.setAuthentication(new Authentication(2));
		assertDoesNotThrow(() -> userDataActionService.createData(target));
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
			}
		});
	}

	@Test
	void testCreateDataException() {
		UserData target0 = new UserData(0, "Charlie", "Brown", null);
		sessionHelper.setAuthentication(null);
		assertThrowsExactly(UnauthorizedOperationException.class, () -> userDataActionService.createData(target0));
		UserData target1 = new UserData(0, "\t", "Brown", null);
		sessionHelper.setAuthentication(new Authentication(2));
		assertThrowsExactly(InvalidRequestException.class, () -> userDataActionService.createData(target1));
		UserData target2 = new UserData(0, "Charlie", "\t", null);
		assertThrowsExactly(InvalidRequestException.class, () -> userDataActionService.createData(target2));
		sessionHelper.setAuthentication(new Authentication(1));
		UserData target3 = new UserData(0, "Charlie", "Brown", null);
		assertThrowsExactly(InvalidRequestException.class, () -> userDataActionService.createData(target3));
	}

	@Test
	void testDeleteDataSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		assertDoesNotThrow(() -> userDataActionService.deleteData(1));
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
	void testDeleteDataException() {
		sessionHelper.setAuthentication(null);
		assertThrowsExactly(UnauthorizedOperationException.class, () -> userDataActionService.deleteData(1));
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrowsExactly(ForbiddenOperationException.class, () -> userDataActionService.deleteData(0));
		sessionHelper.setAuthentication(new Authentication(3));
		assertThrowsExactly(EntityNotFoundException.class, () -> userDataActionService.deleteData(3));
	}

	@Test
	void testUpdateDataSuccess() {
		UserData target = new UserData(1, "David", "Dawson", new byte[2]);
		sessionHelper.setAuthentication(new Authentication(1));
		assertDoesNotThrow(() -> userDataActionService.updateData(target));
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
				assertArrayEquals(new byte[2], result.getBytes("icon"));
			}
		});
	}

	@Test
	void testUpdateDataException() {
		UserData target0 = new UserData(1, "David", "Dawson", new byte[2]);
		sessionHelper.setAuthentication(null);
		assertThrowsExactly(UnauthorizedOperationException.class, () -> userDataActionService.updateData(target0));
		sessionHelper.setAuthentication(new Authentication(0));
		assertThrowsExactly(ForbiddenOperationException.class, () -> userDataActionService.updateData(target0));
		UserData target1 = new UserData(1, "\t", "Brown", null);
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrowsExactly(InvalidRequestException.class, () -> userDataActionService.updateData(target1));
		UserData target2 = new UserData(1, "Charlie", "\t", null);
		assertThrowsExactly(InvalidRequestException.class, () -> userDataActionService.updateData(target2));
		UserData target3 = new UserData(2, "Charlie", "Brown", null);
		sessionHelper.setAuthentication(new Authentication(2));
		assertThrowsExactly(EntityNotFoundException.class, () -> userDataActionService.updateData(target3));
	}

}
