package com.aggelowe.techquiry.database.dao;

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
import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.entity.Observer;
import com.aggelowe.techquiry.database.entity.UserLogin;
import com.aggelowe.techquiry.database.exception.SQLRunnerExecuteException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class ObserverDaoTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	ObserverDao observerDao;

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
	void testCheckSuccess() {
		Observer target0 = new Observer(0, 0);
		assertTrue(assertDoesNotThrow(() -> observerDao.check(target0)));
		Observer target1 = new Observer(2, 0);
		assertFalse(assertDoesNotThrow(() -> observerDao.check(target1)));
	}

	@Test
	void testCountFromInquiryIdSuccess() {
		int count0 = assertDoesNotThrow(() -> observerDao.countFromInquiryId(0));
		assertEquals(2, count0);
		int count1 = assertDoesNotThrow(() -> observerDao.countFromInquiryId(2));
		assertEquals(0, count1);
	}

	@Test
	void testDeleteSuccess() {
		assertDoesNotThrow(() -> observerDao.delete(new Observer(1, 1)));
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
	void testInsertSuccess() {
		assertDoesNotThrow(() -> observerDao.insert(new Observer(2, 1)));
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
	void testInsertException() {
		assertThrows(SQLRunnerExecuteException.class, () -> observerDao.insert(new Observer(0, 0)));
		assertThrows(SQLRunnerExecuteException.class, () -> observerDao.insert(new Observer(3, 2)));
	}

	@Test
	void testSelectFromInquiryIdSuccess() {
		List<UserLogin> observers = assertDoesNotThrow(() -> observerDao.selectFromInquiryId(1));
		assertEquals(1, observers.size());
		UserLogin userLogin = observers.getFirst();
		assertEquals(1, userLogin.getUserId());
		assertEquals("bob", userLogin.getUsername());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzc3dvcmQ="), userLogin.getPasswordHash());
		assertArrayEquals(SecurityUtils.decodeBase64("cGFzcw=="), userLogin.getPasswordSalt());
	}

	@Test
	void testSelectFromUserIdSuccess() {
		List<Inquiry> observers = assertDoesNotThrow(() -> observerDao.selectFromUserId(1));
		assertEquals(2, observers.size());
		Inquiry inquiry = observers.getFirst();		
		assertEquals(0, inquiry.getInquiryId());
		assertEquals(1, inquiry.getUserId());
		assertEquals("Test", inquiry.getTitle());
		assertEquals("Test Content", inquiry.getContent());
		assertEquals(true, inquiry.getAnonymous());
	}

}
