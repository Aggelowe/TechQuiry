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
import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.entity.Observer;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.service.action.ObserverActionService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
public class ObserverServiceTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	ObserverService observerService;

	@Autowired
	ObserverActionService observerActionService;

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
	public void destroy() {
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
	public void testGetObserverCountByInquiryIdSuccess() {
		int count = assertDoesNotThrow(() -> observerService.getObserverCountByInquiryId(0));
		assertEquals(2, count);
	}

	@Test
	public void testGetObserverCountByInquiryIdException() {
		assertThrows(EntityNotFoundException.class, () -> observerService.getObserverCountByInquiryId(3));
	}

	@Test
	public void testGetObserverUserLoginListByInquiryIdSuccess() {
		List<UserLogin> logins = assertDoesNotThrow(() -> observerService.getObserverUserLoginListByInquiryId(1));
		assertEquals(1, logins.size());
		UserLogin userLogin = logins.getFirst();
		assertEquals(1, userLogin.getId());
		assertEquals("bob", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzc3dvcmQ="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzcw=="), userLogin.getPasswordSalt());
	}

	@Test
	public void testGetObserverUserLoginListByInquiryIdException() {
		assertThrows(EntityNotFoundException.class, () -> observerService.getObserverUserLoginListByInquiryId(3));
	}

	@Test
	public void testGetObservedInquiryListByUserIdSuccess() {
		List<Inquiry> inquiries = assertDoesNotThrow(() -> observerService.getObservedInquiryListByUserId(1));
		assertEquals(2, inquiries.size());
		Inquiry inquiry = inquiries.getFirst();
		assertEquals(0, inquiry.getId());
		assertEquals(1, inquiry.getUserId());
		assertEquals("Test", inquiry.getTitle());
		assertEquals("Test Content", inquiry.getContent());
		assertEquals(true, inquiry.isAnonymous());
	}

	@Test
	public void testGetObservedInquiryListByUserIdException() {
		assertThrows(EntityNotFoundException.class, () -> observerService.getObservedInquiryListByUserId(2));
	}

	@Test
	public void testCreateObserverSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		Observer target = new Observer(2, 1);
		assertDoesNotThrow(() -> observerActionService.createObserver(target));
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
	public void testCreateObserverException() {
		sessionHelper.setAuthentication(null);
		Observer target0 = new Observer(2, 1);
		assertThrows(ForbiddenOperationException.class, () -> observerActionService.createObserver(target0));
		sessionHelper.setAuthentication(new Authentication(0));
		assertThrows(ForbiddenOperationException.class, () -> observerActionService.createObserver(target0));
		Observer target1 = new Observer(2, 2);
		sessionHelper.setAuthentication(new Authentication(2));
		assertThrows(EntityNotFoundException.class, () -> observerActionService.createObserver(target1));
		Observer target2 = new Observer(3, 1);
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrows(EntityNotFoundException.class, () -> observerActionService.createObserver(target2));
		Observer target3 = new Observer(1, 1);
		assertThrows(InvalidRequestException.class, () -> observerActionService.createObserver(target3));
	}

	@Test
	public void testDeleteObserverSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		assertDoesNotThrow(() -> observerActionService.deleteObserver(new Observer(1, 1)));
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
	public void testDeleteObserverException() {
		sessionHelper.setAuthentication(null);
		Observer target0 = new Observer(1, 1);
		assertThrows(ForbiddenOperationException.class, () -> observerActionService.createObserver(target0));
		sessionHelper.setAuthentication(new Authentication(0));
		assertThrows(ForbiddenOperationException.class, () -> observerActionService.createObserver(target0));
		Observer target1 = new Observer(2, 1);
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrows(EntityNotFoundException.class, () -> observerActionService.deleteObserver(target1));
	}

}
