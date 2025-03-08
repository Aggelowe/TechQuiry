package net.techquiry.app.database.dao;

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

import net.techquiry.app.common.SecurityUtils;
import net.techquiry.app.common.TestAppConfiguration;
import net.techquiry.app.database.exception.SQLRunnerExecuteException;
import net.techquiry.app.entity.Response;
import net.techquiry.app.entity.Upvote;
import net.techquiry.app.entity.UserLogin;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class UpvoteDaoTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	UpvoteDao upvoteDao;

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
	void testCheckSuccess() {
		Upvote target0 = new Upvote(0, 0);
		assertTrue(assertDoesNotThrow(() -> upvoteDao.check(target0)));
		Upvote target1 = new Upvote(2, 0);
		assertFalse(assertDoesNotThrow(() -> upvoteDao.check(target1)));
	}

	@Test
	void testCountFromResponseIdSuccess() {
		int count0 = assertDoesNotThrow(() -> upvoteDao.countFromResponseId(0));
		assertEquals(1, count0);
		int count1 = assertDoesNotThrow(() -> upvoteDao.countFromResponseId(2));
		assertEquals(0, count1);
	}

	@Test
	void testDeleteSuccess() {
		assertDoesNotThrow(() -> upvoteDao.delete(new Upvote(0, 0)));
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
	void testInsertSuccess() {
		assertDoesNotThrow(() -> upvoteDao.insert(new Upvote(2, 1)));
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
	void testInsertException() {
		assertThrowsExactly(SQLRunnerExecuteException.class, () -> upvoteDao.insert(new Upvote(0, 0)));
		assertThrowsExactly(SQLRunnerExecuteException.class, () -> upvoteDao.insert(new Upvote(3, 2)));
	}

	@Test
	void testSelectFromResponseIdSuccess() {
		List<UserLogin> upvotes = assertDoesNotThrow(() -> upvoteDao.selectFromResponseId(0));
		assertEquals(1, upvotes.size());
		UserLogin userLogin = upvotes.getFirst();
		assertEquals(0, userLogin.getUserId());
		assertEquals("alice", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("MTIzNDU2Nzg="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("MTIzNA=="), userLogin.getPasswordSalt());
	}

	@Test
	void testSelectFromUserIdSuccess() {
		List<Response> upvotes = assertDoesNotThrow(() -> upvoteDao.selectFromUserId(1));
		assertEquals(1, upvotes.size());
		Response response = upvotes.getFirst();
		assertEquals(1, response.getResponseId());
		assertEquals(2, response.getInquiryId());
		assertEquals(1, response.getUserId());
		assertEquals(false, response.getAnonymous());
		assertEquals("Instance Response", response.getContent());
	}

}
