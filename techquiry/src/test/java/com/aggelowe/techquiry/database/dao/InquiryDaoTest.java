package com.aggelowe.techquiry.database.dao;

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

import com.aggelowe.techquiry.common.TestAppConfiguration;
import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.exception.SQLRunnerExecuteException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class InquiryDaoTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	InquiryDao inquiryDao;

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
	void testCountSuccess() {
		int count = assertDoesNotThrow(() -> inquiryDao.count());
		assertEquals(3, count);
	}

	@Test
	void testDeleteSuccess() {
		assertDoesNotThrow(() -> inquiryDao.delete(1));
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
	void testInsertSuccess() {
		int id = assertDoesNotThrow(() -> inquiryDao.insert(new Inquiry(0, 0, "Success", "Success Content", false)));
		assertEquals(3, id);
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = assertDoesNotThrow(() -> connection.createStatement());
				assertDoesNotThrow(() -> statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 3"));
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(3, result.getInt("inquiry_id"));
				assertEquals(0, result.getInt("user_id"));
				assertEquals("Success", result.getString("title"));
				assertEquals("Success Content", result.getString("content"));
				assertEquals(false, result.getBoolean("anonymous"));
				assertFalse(result.next());
			}
		});
	}

	@Test
	void testInsertException() {
		assertThrows(SQLRunnerExecuteException.class, () -> inquiryDao.insert(new Inquiry(3, 2, "Fail", "Fail Content", true)));
	}

	@Test
	void testRangeSuccess() {
		List<Inquiry> inquiries = assertDoesNotThrow(() -> inquiryDao.range(2, 1));
		assertEquals(2, inquiries.size());
		Inquiry inquiry0 = inquiries.get(0);
		assertEquals(1, inquiry0.getInquiryId());
		assertEquals(0, inquiry0.getUserId());
		assertEquals("Example", inquiry0.getTitle());
		assertEquals("Example Content", inquiry0.getContent());
		assertEquals(true, inquiry0.getAnonymous());
		Inquiry inquiry1 = inquiries.get(1);
		assertEquals(2, inquiry1.getInquiryId());
	}

	@Test
	void testSelectFromUserIdNonAnonymousSuccess() {
		List<Inquiry> inquiries0 = assertDoesNotThrow(() -> inquiryDao.selectFromUserIdNonAnonymous(0));
		assertEquals(1, inquiries0.size());
		Inquiry inquiry = inquiries0.get(0);
		assertEquals(2, inquiry.getInquiryId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Instance", inquiry.getTitle());
		assertEquals("Instance Content", inquiry.getContent());
		assertEquals(false, inquiry.getAnonymous());
		List<Inquiry> inquiries1 = assertDoesNotThrow(() -> inquiryDao.selectFromUserIdNonAnonymous(1));
		assertTrue(inquiries1.isEmpty());
	}

	@Test
	void testSelectFromUserIdSuccess() {
		List<Inquiry> inquiries0 = assertDoesNotThrow(() -> inquiryDao.selectFromUserId(0));
		assertEquals(2, inquiries0.size());
		Inquiry inquiry0 = inquiries0.get(0);
		assertEquals(1, inquiry0.getInquiryId());
		assertEquals(0, inquiry0.getUserId());
		assertEquals("Example", inquiry0.getTitle());
		assertEquals("Example Content", inquiry0.getContent());
		assertEquals(true, inquiry0.getAnonymous());
		List<Inquiry> inquiries1 = assertDoesNotThrow(() -> inquiryDao.selectFromUserId(1));
		assertEquals(1, inquiries1.size());
		Inquiry inquiry1 = inquiries1.get(0);
		assertEquals(0, inquiry1.getInquiryId());
		assertEquals(1, inquiry1.getUserId());
		assertEquals("Test", inquiry1.getTitle());
		assertEquals("Test Content", inquiry1.getContent());
		assertEquals(true, inquiry1.getAnonymous());
	}

	@Test
	void testSelectSuccess() {
		Inquiry inquiry = assertDoesNotThrow(() -> inquiryDao.select(1));
		assertEquals(1, inquiry.getInquiryId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Example", inquiry.getTitle());
		assertEquals("Example Content", inquiry.getContent());
		assertEquals(true, inquiry.getAnonymous());
	}

	@Test
	void testUpdateSuccess() {
		assertDoesNotThrow(() -> inquiryDao.update(new Inquiry(0, 1, "Updated", "Updated Content", false)));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 0");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(0, result.getInt("inquiry_id"));
				assertEquals(1, result.getInt("user_id"));
				assertEquals("Updated", result.getString("title"));
				assertEquals("Updated Content", result.getString("content"));
				assertEquals(false, result.getBoolean("anonymous"));
			}
		});
	}

	@Test
	void testUpdateException() {
		assertThrows(SQLRunnerExecuteException.class, () -> inquiryDao.update(new Inquiry(0, 3, "Fail", "Fail Content", false)));
	}

}
