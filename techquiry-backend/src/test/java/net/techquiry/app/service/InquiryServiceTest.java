package net.techquiry.app.service;

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

import net.techquiry.app.common.TestAppConfiguration;
import net.techquiry.app.entity.Inquiry;
import net.techquiry.app.service.action.InquiryActionService;
import net.techquiry.app.service.exception.EntityNotFoundException;
import net.techquiry.app.service.exception.ForbiddenOperationException;
import net.techquiry.app.service.exception.InvalidRequestException;
import net.techquiry.app.service.exception.UnauthorizedOperationException;
import net.techquiry.app.service.session.Authentication;
import net.techquiry.app.service.session.SessionHelper;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class InquiryServiceTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	InquiryService inquiryService;

	@Autowired
	InquiryActionService inquiryActionService;

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
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
				statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(0, 1, 'Test',	'Test Content', true);");
				statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(1, 0, 'Example',	'Example Content', true);");
				statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(2, 0, 'Instance',	'Instance Content', false);");
				connection.commit();
			}
		});
	}

	@AfterEach
	void destroy() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("DROP TABLE 'inquiry'");
				statement.execute("DROP TABLE 'user_login'");
				connection.commit();
			}
		});
	}

	@Test
	void testGetInquiryCountSuccess() {
		int count = assertDoesNotThrow(() -> inquiryService.getInquiryCount());
		assertEquals(3, count);
	}

	@Test
	void testGetInquiryRangeSuccess() {
		List<Inquiry> inquiries = assertDoesNotThrow(() -> inquiryService.getInquiryRange(2, 1));
		assertEquals(1, inquiries.size());
		Inquiry inquiry = inquiries.get(0);
		assertEquals(2, inquiry.getInquiryId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Instance", inquiry.getTitle());
		assertEquals("Instance Content", inquiry.getContent());
		assertEquals(false, inquiry.getAnonymous());
	}

	@Test
	void testGetInquiryByInquiryIdSuccess() {
		Inquiry inquiry = assertDoesNotThrow(() -> inquiryService.getInquiryByInquiryId(1));
		assertEquals(1, inquiry.getInquiryId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Example", inquiry.getTitle());
		assertEquals("Example Content", inquiry.getContent());
		assertEquals(true, inquiry.getAnonymous());
	}

	@Test
	void testGetInquiryByInquiryIdException() {
		assertThrowsExactly(EntityNotFoundException.class, () -> inquiryService.getInquiryByInquiryId(3));
	}

	@Test
	void testGetInquiryListByUserIdSuccess() {
		sessionHelper.setAuthentication(null);
		List<Inquiry> inquiries0 = assertDoesNotThrow(() -> inquiryActionService.getInquiryListByUserId(0));
		assertEquals(1, inquiries0.size());
		Inquiry inquiry = inquiries0.get(0);
		assertEquals(2, inquiry.getInquiryId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Instance", inquiry.getTitle());
		assertEquals("Instance Content", inquiry.getContent());
		assertEquals(false, inquiry.getAnonymous());
		sessionHelper.setAuthentication(new Authentication(0));
		List<Inquiry> inquiries1 = assertDoesNotThrow(() -> inquiryActionService.getInquiryListByUserId(0));
		assertEquals(2, inquiries1.size());
	}

	@Test
	void testGetInquiryListByUserIdException() {
		sessionHelper.setAuthentication(null);
		assertThrowsExactly(EntityNotFoundException.class, () -> inquiryActionService.getInquiryListByUserId(3));
	}

	@Test
	void testCreateInquirySuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		Inquiry target = new Inquiry(0, 0, "Success", "Success Content", false);
		int id = assertDoesNotThrow(() -> inquiryActionService.createInquiry(target));
		assertEquals(3, id);
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 3");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(3, result.getInt("inquiry_id"));
				assertEquals(1, result.getInt("user_id"));
				assertEquals("Success", result.getString("title"));
				assertEquals("Success Content", result.getString("content"));
				assertEquals(false, result.getBoolean("anonymous"));
				assertFalse(result.next());
			}
		});
	}

	@Test
	void testCreateInquiryException() {
		sessionHelper.setAuthentication(null);
		Inquiry target0 = new Inquiry(0, 0, "Fail", "Fail Content", true);
		assertThrowsExactly(UnauthorizedOperationException.class, () -> inquiryActionService.createInquiry(target0));
		Inquiry target1 = new Inquiry(0, 0, "\t", "Fail Content", false);
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrowsExactly(InvalidRequestException.class, () -> inquiryActionService.createInquiry(target1));
		Inquiry target2 = new Inquiry(0, 0, "Fail", "\t", false);
		assertThrowsExactly(InvalidRequestException.class, () -> inquiryActionService.createInquiry(target2));
	}

	@Test
	void testDeleteInquirySuccess() {
		sessionHelper.setAuthentication(new Authentication(0));
		assertDoesNotThrow(() -> inquiryActionService.deleteInquiry(1));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 1");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertFalse(result.next());
			}
		});
	}

	@Test
	void testDeleteInquiryException() {
		sessionHelper.setAuthentication(null);
		assertThrowsExactly(UnauthorizedOperationException.class, () -> inquiryActionService.deleteInquiry(1));
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrowsExactly(ForbiddenOperationException.class, () -> inquiryActionService.deleteInquiry(1));
		assertThrowsExactly(EntityNotFoundException.class, () -> inquiryActionService.deleteInquiry(3));
	}

	@Test
	void testUpdateInquirySuccess() {
		sessionHelper.setAuthentication(new Authentication(0));
		Inquiry target = new Inquiry(1, 1, "Updated", "Updated Content", false);
		assertDoesNotThrow(() -> inquiryActionService.updateInquiry(target));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 1");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(1, result.getInt("inquiry_id"));
				assertEquals(0, result.getInt("user_id"));
				assertEquals("Updated", result.getString("title"));
				assertEquals("Updated Content", result.getString("content"));
				assertEquals(false, result.getBoolean("anonymous"));
			}
		});
	}

	@Test
	void testUpdateInquiryException() {
		Inquiry target0 = new Inquiry(0, 0, "Fail", "Fail Content", true);
		sessionHelper.setAuthentication(null);
		assertThrowsExactly(UnauthorizedOperationException.class, () -> inquiryActionService.updateInquiry(target0));
		Inquiry target1 = new Inquiry(0, 0, "Fail", "Fail Content", true);
		sessionHelper.setAuthentication(new Authentication(0));
		assertThrowsExactly(ForbiddenOperationException.class, () -> inquiryActionService.updateInquiry(target1));
		Inquiry target2 = new Inquiry(3, 0, "Fail", "Fail Content", true);
		assertThrowsExactly(EntityNotFoundException.class, () -> inquiryActionService.updateInquiry(target2));
		Inquiry target3 = new Inquiry(0, 0, "\t", "Fail Content", false);
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrowsExactly(InvalidRequestException.class, () -> inquiryActionService.updateInquiry(target3));
		Inquiry target4 = new Inquiry(0, 0, "Fail", "\t", false);
		assertThrowsExactly(InvalidRequestException.class, () -> inquiryActionService.updateInquiry(target4));
	}

}
