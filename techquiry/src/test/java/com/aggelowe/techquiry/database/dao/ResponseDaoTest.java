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
import com.aggelowe.techquiry.database.entity.Response;
import com.aggelowe.techquiry.database.exception.SQLRunnerExecuteException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class ResponseDaoTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	ResponseDao responseDao;

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
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
				statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
				statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(0, 1, 'Test',	'Test Content', true);");
				statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(1, 0, 'Example',	'Example Content', true);");
				statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(2, 0, 'Instance',	'Instance Content', true);");
				statement.execute("INSERT INTO response(response_id, inquiry_id, user_id, anonymous, content) VALUES(0, 0, 0, true, 'Test Response');");
				statement.execute("INSERT INTO response(response_id, inquiry_id, user_id, anonymous, content) VALUES(1, 2, 1, false, 'Instance Response');");
				statement.execute("INSERT INTO response(response_id, inquiry_id, user_id, anonymous, content) VALUES(2, 2, 0, false, 'Second Response');");
				connection.commit();
			}
		});
	}

	@AfterEach
	void destroy() {
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("DROP TABLE 'response'");
				statement.execute("DROP TABLE 'inquiry'");
				statement.execute("DROP TABLE 'user_login'");
				connection.commit();
			}
		});
	}

	@Test
	void testCountFromInquiryIdSuccess() {
		int count0 = assertDoesNotThrow(() -> responseDao.countFromInquiryId(1));
		assertEquals(0, count0);
		int count1 = assertDoesNotThrow(() -> responseDao.countFromInquiryId(2));
		assertEquals(2, count1);
	}

	@Test
	void testDeleteSuccess() {
		assertDoesNotThrow(() -> responseDao.delete(1));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM response WHERE response_id = 1");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertFalse(result.next());
			}
		});
	}

	@Test
	void testInsertSuccess() {
		int id = assertDoesNotThrow(() -> responseDao.insert(new Response(0, 1, 1, true, "Example Response")));
		assertEquals(3, id);
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM response WHERE response_id = 3");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(3, result.getInt("response_id"));
				assertEquals(1, result.getInt("inquiry_id"));
				assertEquals(1, result.getInt("user_id"));
				assertEquals(true, result.getBoolean("anonymous"));
				assertEquals("Example Response", result.getString("content"));
				assertFalse(result.next());
			}
		});
	}

	@Test
	void testInsertException() {
		assertThrows(SQLRunnerExecuteException.class, () -> responseDao.insert(new Response(2, 3, 1, true, "Example Response")));
	}

	@Test
	void testSelectFromInquiryIdSuccess() {
		List<Response> responses = assertDoesNotThrow(() -> responseDao.selectFromInquiryId(2));
		assertEquals(2, responses.size());
		Response response = responses.getFirst();
		assertEquals(1, response.getResponseId());
		assertEquals(2, response.getInquiryId());
		assertEquals(1, response.getUserId());
		assertEquals(false, response.getAnonymous());
		assertEquals("Instance Response", response.getContent());
	}

	@Test
	void testSelectSuccess() {
		Response response = assertDoesNotThrow(() -> responseDao.select(1));
		assertEquals(1, response.getResponseId());
		assertEquals(2, response.getInquiryId());
		assertEquals(1, response.getUserId());
		assertEquals(false, response.getAnonymous());
		assertEquals("Instance Response", response.getContent());
	}

	@Test
	void testUpdateSuccess() {
		assertDoesNotThrow(() -> responseDao.update(new Response(0, 1, 1, false, "Updated Response")));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM response WHERE response_id = 0");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(0, result.getInt("response_id"));
				assertEquals(1, result.getInt("inquiry_id"));
				assertEquals(1, result.getInt("user_id"));
				assertEquals(false, result.getBoolean("anonymous"));
				assertEquals("Updated Response", result.getString("content"));
			}
		});
	}

	@Test
	void testUpdateException() {
		assertThrows(SQLRunnerExecuteException.class, () -> responseDao.update(new Response(0, 3, 2, false, "Fail Response")));
	}

}
