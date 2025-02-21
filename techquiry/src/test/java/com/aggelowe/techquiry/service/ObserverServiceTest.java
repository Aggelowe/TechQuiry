package com.aggelowe.techquiry.service;

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
import com.aggelowe.techquiry.entity.Inquiry;
import com.aggelowe.techquiry.entity.UserLogin;
import com.aggelowe.techquiry.service.action.ObserverActionService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class ObserverServiceTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	ObserverService observerService;

	@Autowired
	ObserverActionService observerActionService;

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
						CREATE TABLE IF NOT EXISTS 'observer' (
								'inquiry_id' INTEGER NOT NULL,
								'user_id' INTEGER NOT NULL,
								PRIMARY KEY('inquiry_id', 'user_id'),
								FOREIGN KEY ('inquiry_id') REFERENCES 'inquiry'('inquiry_id')
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
				statement.execute("INSERT INTO observer(inquiry_id, user_id) VALUES(0, 0);");
				statement.execute("INSERT INTO observer(inquiry_id, user_id) VALUES(0, 1);");
				statement.execute("INSERT INTO observer(inquiry_id, user_id) VALUES(1, 1);");
				connection.commit();
			}
		});
	}

	@AfterEach
	void destroy() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("DROP TABLE 'observer'");
				statement.execute("DROP TABLE 'inquiry'");
				statement.execute("DROP TABLE 'user_login'");
				connection.commit();
			}
		});
	}

	@Test
	void testGetObserverCountByInquiryIdSuccess() {
		int count = assertDoesNotThrow(() -> observerService.getObserverCountByInquiryId(0));
		assertEquals(2, count);
	}

	@Test
	void testGetObserverCountByInquiryIdException() {
		assertThrowsExactly(EntityNotFoundException.class, () -> observerService.getObserverCountByInquiryId(3));
	}

	@Test
	void testGetObserverUserLoginListByInquiryIdSuccess() {
		List<UserLogin> logins = assertDoesNotThrow(() -> observerService.getObserverUserLoginListByInquiryId(1));
		assertEquals(1, logins.size());
		UserLogin userLogin = logins.getFirst();
		assertEquals(1, userLogin.getUserId());
		assertEquals("bob", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzc3dvcmQ="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzcw=="), userLogin.getPasswordSalt());
	}

	@Test
	void testGetObserverUserLoginListByInquiryIdException() {
		assertThrowsExactly(EntityNotFoundException.class, () -> observerService.getObserverUserLoginListByInquiryId(3));
	}

	@Test
	void testGetObservedInquiryListByUserIdSuccess() {
		List<Inquiry> inquiries = assertDoesNotThrow(() -> observerService.getObservedInquiryListByUserId(1));
		assertEquals(2, inquiries.size());
		Inquiry inquiry = inquiries.getFirst();
		assertEquals(0, inquiry.getInquiryId());
		assertEquals(1, inquiry.getUserId());
		assertEquals("Test", inquiry.getTitle());
		assertEquals("Test Content", inquiry.getContent());
		assertEquals(true, inquiry.getAnonymous());
	}

	@Test
	void testGetObservedInquiryListByUserIdException() {
		assertThrowsExactly(EntityNotFoundException.class, () -> observerService.getObservedInquiryListByUserId(2));
	}

	@Test
	void testCheckObserverSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		assertEquals(true, assertDoesNotThrow(() -> observerActionService.checkObserver(0)));
	}

	@Test
	void testCheckObserverException() {
		sessionHelper.setAuthentication(null);
		assertThrowsExactly(ForbiddenOperationException.class, () -> observerActionService.checkObserver(0));
	}

	@Test
	void testCreateObserverSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		assertDoesNotThrow(() -> observerActionService.createObserver(2));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM observer WHERE inquiry_id = 2 AND user_id = 1");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(2, result.getInt("inquiry_id"));
				assertEquals(1, result.getInt("user_id"));
			}
		});
	}

	@Test
	void testCreateObserverException() {
		sessionHelper.setAuthentication(null);
		assertThrowsExactly(ForbiddenOperationException.class, () -> observerActionService.createObserver(2));
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrowsExactly(EntityNotFoundException.class, () -> observerActionService.createObserver(3));
		assertThrowsExactly(InvalidRequestException.class, () -> observerActionService.createObserver(1));
	}

	@Test
	void testDeleteObserverSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		assertDoesNotThrow(() -> observerActionService.deleteObserver(1));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM observer WHERE inquiry_id = 1");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertFalse(result.next());
			}
		});
	}

	@Test
	void testDeleteObserverException() {
		sessionHelper.setAuthentication(null);
		assertThrowsExactly(ForbiddenOperationException.class, () -> observerActionService.deleteObserver(1));
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrowsExactly(EntityNotFoundException.class, () -> observerActionService.deleteObserver(2));
	}

}
