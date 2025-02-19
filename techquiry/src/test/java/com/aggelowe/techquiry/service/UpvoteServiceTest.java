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
import com.aggelowe.techquiry.common.TestAppConfiguration;
import com.aggelowe.techquiry.entity.Response;
import com.aggelowe.techquiry.entity.UserLogin;
import com.aggelowe.techquiry.service.action.UpvoteActionService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class UpvoteServiceTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	UpvoteService upvoteService;

	@Autowired
	UpvoteActionService upvoteActionService;

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
						CREATE TABLE IF NOT EXISTS 'inquiry' (
								'inquiry_id' INTEGER NOT NULL UNIQUE,
								'user_id' INTEGER NOT NULL,
								'title' TEXT NOT NULL,
								'content' TEXT NOT NULL,
								'anonymous' INTEGER NOT NULL,
								PRIMARY KEY('inquiry_id'),
								FOREIGN KEY ('user_id') REFERENCES 'user_login'('user_id')
								ON UPDATE CASCADE ON DELETE CASCADE
						);
						""");
				statement.execute("""
						CREATE TABLE IF NOT EXISTS 'response' (
								'response_id' INTEGER NOT NULL UNIQUE,
								'inquiry_id' INTEGER NOT NULL,
								'user_id' INTEGER NOT NULL,
								'anonymous' INTEGER NOT NULL,
								'content' TEXT NOT NULL,
								PRIMARY KEY('response_id'),
								FOREIGN KEY ('inquiry_id') REFERENCES 'inquiry'('inquiry_id')
								ON UPDATE CASCADE ON DELETE CASCADE,
								FOREIGN KEY ('user_id') REFERENCES 'user_login'('user_id')
								ON UPDATE CASCADE ON DELETE CASCADE
						);
						""");
				statement.execute("""
						CREATE TABLE IF NOT EXISTS 'upvote' (
								'response_id' INTEGER NOT NULL,
								'user_id' INTEGER NOT NULL,
								PRIMARY KEY('response_id', 'user_id'),
								FOREIGN KEY ('response_id') REFERENCES 'response'('response_id')
								ON UPDATE CASCADE ON DELETE CASCADE,
								FOREIGN KEY ('user_id') REFERENCES 'user_login'('user_id')
								ON UPDATE CASCADE ON DELETE CASCADE
						);
						""");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
				statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(0, 1, 'Test',	'Test Content', true);");
				statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(1, 0, 'Example',	'Example Content', true);");
				statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(2, 0, 'Instance',	'Instance Content', true);");
				statement.execute("INSERT INTO response(response_id, inquiry_id, user_id, anonymous, content) VALUES(0, 0, 0, true, 'Test Response');");
				statement.execute("INSERT INTO response(response_id, inquiry_id, user_id, anonymous, content) VALUES(1, 2, 1, false, 'Instance Response');");
				statement.execute("INSERT INTO response(response_id, inquiry_id, user_id, anonymous, content) VALUES(2, 2, 0, false, 'Second Response');");
				statement.execute("INSERT INTO upvote(response_id, user_id) VALUES(0, 0);");
				statement.execute("INSERT INTO upvote(response_id, user_id) VALUES(1, 1);");
				statement.execute("INSERT INTO upvote(response_id, user_id) VALUES(1, 0);");
				connection.commit();
			}
		});
	}

	@AfterEach
	void destroy() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("DROP TABLE 'upvote'");
				statement.execute("DROP TABLE 'response'");
				statement.execute("DROP TABLE 'inquiry'");
				statement.execute("DROP TABLE 'user_login'");
				connection.commit();
			}
		});
	}

	@Test
	void testGetUpvoteCountByResponseIdSuccess() {
		int count = assertDoesNotThrow(() -> upvoteService.getUpvoteCountByResponseId(1));
		assertEquals(2, count);
	}

	@Test
	void testGetUpvoteCountByResponseIdException() {
		assertThrows(EntityNotFoundException.class, () -> upvoteService.getUpvoteCountByResponseId(3));
	}

	@Test
	void testGetUpvoteUserLoginListByResponseIdSuccess() {
		List<UserLogin> logins = assertDoesNotThrow(() -> upvoteService.getUpvoteUserLoginListByResponseId(0));
		assertEquals(1, logins.size());
		UserLogin userLogin = logins.getFirst();
		assertEquals(0, userLogin.getUserId());
		assertEquals("alice", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("MTIzNDU2Nzg="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("MTIzNA=="), userLogin.getPasswordSalt());
	}

	@Test
	void testGetUpvoteUserLoginListByResponseIdException() {
		assertThrows(EntityNotFoundException.class, () -> upvoteService.getUpvoteUserLoginListByResponseId(3));
	}

	@Test
	void testGetUpvotedResponseListByUserIdSuccess() {
		List<Response> responses = assertDoesNotThrow(() -> upvoteService.getUpvotedResponseListByUserId(1));
		assertEquals(1, responses.size());
		Response response = responses.getFirst();
		assertEquals(1, response.getResponseId());
		assertEquals(2, response.getInquiryId());
		assertEquals(1, response.getUserId());
		assertEquals(false, response.getAnonymous());
		assertEquals("Instance Response", response.getContent());
	}

	@Test
	void testGetUpvotedResponseListByUserIdException() {
		assertThrows(EntityNotFoundException.class, () -> upvoteService.getUpvotedResponseListByUserId(2));
	}

	@Test
	void testCheckUpvoteSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		assertEquals(true, assertDoesNotThrow(() -> upvoteActionService.checkUpvote(1)));
	}

	@Test
	void testCheckUpvoteException() {
		sessionHelper.setAuthentication(null);
		assertThrows(ForbiddenOperationException.class, () -> upvoteActionService.checkUpvote(1));
	}

	@Test
	void testCreateUpvoteSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		assertDoesNotThrow(() -> upvoteActionService.createUpvote(2));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				assertDoesNotThrow(() -> statement.execute("SELECT * FROM upvote WHERE response_id = 2 AND user_id = 1"));
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(2, result.getInt("response_id"));
				assertEquals(1, result.getInt("user_id"));
			}
		});
	}

	@Test
	void testCreateUpvoteException() {
		sessionHelper.setAuthentication(null);
		assertThrows(ForbiddenOperationException.class, () -> upvoteActionService.createUpvote(2));
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrows(EntityNotFoundException.class, () -> upvoteActionService.createUpvote(3));
		assertThrows(InvalidRequestException.class, () -> upvoteActionService.createUpvote(1));
	}

	@Test
	void testDeleteUpvoteSuccess() {
		sessionHelper.setAuthentication(new Authentication(0));
		assertDoesNotThrow(() -> upvoteActionService.deleteUpvote(0));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM upvote WHERE response_id = 0");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertFalse(result.next());
			}
		});
	}

	@Test
	void testDeleteUpvoteException() {
		sessionHelper.setAuthentication(null);
		assertThrows(ForbiddenOperationException.class, () -> upvoteActionService.deleteUpvote(0));
		sessionHelper.setAuthentication(new Authentication(0));
		assertThrows(EntityNotFoundException.class, () -> upvoteActionService.deleteUpvote(2));
	}

}
