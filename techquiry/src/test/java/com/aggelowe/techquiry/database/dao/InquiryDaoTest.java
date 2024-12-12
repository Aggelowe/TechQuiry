package com.aggelowe.techquiry.database.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import com.aggelowe.techquiry.database.SQLRunner;
import com.aggelowe.techquiry.database.entities.Inquiry;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerExecuteException;

public class InquiryDaoTest {

	Connection connection;
	InquiryDao inquiryDao;

	@BeforeEach
	public void initialize() {
		String databaseUrl = "jdbc:sqlite::memory:";
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connection = assertDoesNotThrow(() -> DriverManager.getConnection(databaseUrl, config.toProperties()));
		assertDoesNotThrow(() -> connection.setAutoCommit(false));
		inquiryDao = new InquiryDao(new SQLRunner(connection));
		assertDoesNotThrow(() -> {
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS \"user_login\" (\n"
							+ "	\"user_id\" INTEGER NOT NULL UNIQUE,\n" 
							+ "	\"username\" TEXT NOT NULL UNIQUE,\n"
							+ "	\"password_hash\" TEXT NOT NULL,\n"
							+ "	\"password_salt\" TEXT NOT NULL,\n"
							+ "	PRIMARY KEY(\"user_id\")\n"
							+ ");\n");
			statement.execute("CREATE TABLE IF NOT EXISTS \"inquiry\" (\n" 
							+ "	\"inquiry_id\" INTEGER NOT NULL UNIQUE,\n"
							+ "	\"user_id\" INTEGER NOT NULL,\n"
							+ "	\"title\" TEXT NOT NULL,\n"
							+ "	\"content\" TEXT NOT NULL,\n"
							+ "	\"anonymous\" INTEGER NOT NULL,\n"
							+ "	PRIMARY KEY(\"inquiry_id\"),\n"
							+ "	FOREIGN KEY (\"user_id\") REFERENCES \"user_login\"(\"user_id\")\n"
							+ "	ON UPDATE CASCADE ON DELETE CASCADE\n"
							+ ");");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
			statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(0, 1, 'Test',	'Test Content', true);");
			statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(1, 0, 'Example',	'Example Content', true);");
			statement.execute("INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(2, 0, 'Instance',	'Instance Content', false);");
			connection.commit();
		});
	}

	@AfterEach
	public void destroy() {
		if (connection != null) {
			assertDoesNotThrow(() -> connection.close());
		}
	}

	@Test
	public void testCountSuccess() {
		int count = assertDoesNotThrow(() -> inquiryDao.count());
		assertEquals(3, count);
	}

	@Test
	public void testDeleteSuccess() {
		assertDoesNotThrow(() -> inquiryDao.delete(1));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 1"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertFalse(assertDoesNotThrow(() -> result.next()));
	}

	@Test
	public void testInsertSuccess() {
		int id = assertDoesNotThrow(() -> inquiryDao.insert(new Inquiry(0, 0, "Success", "Success Content", false)));
		assertEquals(3, id);
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 3"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals(3, assertDoesNotThrow(() -> result.getInt("inquiry_id")));
		assertEquals(0, assertDoesNotThrow(() -> result.getInt("user_id")));
		assertEquals("Success", assertDoesNotThrow(() -> result.getString("title")));
		assertEquals("Success Content", assertDoesNotThrow(() -> result.getString("content")));
		assertEquals(false, assertDoesNotThrow(() -> result.getBoolean("anonymous")));
		assertFalse(assertDoesNotThrow(() -> result.next()));
	}

	@Test
	public void testInsertException() {
		assertThrows(SQLRunnerExecuteException.class, () -> inquiryDao.insert(new Inquiry(3, 2, "Fail", "Fail Content", true)));
		assertThrows(SQLRunnerExecuteException.class, () -> inquiryDao.insert(new Inquiry(3, 0, null, null, true)));
	}

	@Test
	public void testRangeSuccess() {
		List<Inquiry> inquiries = assertDoesNotThrow(() -> inquiryDao.range(2, 1));
		assertEquals(2, inquiries.size());
		Inquiry inquiry0 = inquiries.get(0);
		assertEquals(1, inquiry0.getId());
		assertEquals(0, inquiry0.getUserId());
		assertEquals("Example", inquiry0.getTitle());
		assertEquals("Example Content", inquiry0.getContent());
		assertEquals(true, inquiry0.isAnonymous());
		Inquiry inquiry1 = inquiries.get(1);
		assertEquals(2, inquiry1.getId());
	}

	@Test
	public void testSelectFromUserIdNonAnonymousSuccess() {
		List<Inquiry> inquiries0 = assertDoesNotThrow(() -> inquiryDao.selectFromUserIdNonAnonymous(0));
		assertEquals(1, inquiries0.size());
		Inquiry inquiry = inquiries0.get(0);
		assertEquals(2, inquiry.getId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Instance", inquiry.getTitle());
		assertEquals("Instance Content", inquiry.getContent());
		assertEquals(false, inquiry.isAnonymous());
		List<Inquiry> inquiries1 = assertDoesNotThrow(() -> inquiryDao.selectFromUserIdNonAnonymous(1));
		assertTrue(inquiries1.isEmpty());
	}

	@Test
	public void testSelectFromUserIdSuccess() {
		List<Inquiry> inquiries0 = assertDoesNotThrow(() -> inquiryDao.selectFromUserId(0));
		assertEquals(2, inquiries0.size());
		Inquiry inquiry0 = inquiries0.get(0);
		assertEquals(1, inquiry0.getId());
		assertEquals(0, inquiry0.getUserId());
		assertEquals("Example", inquiry0.getTitle());
		assertEquals("Example Content", inquiry0.getContent());
		assertEquals(true, inquiry0.isAnonymous());
		List<Inquiry> inquiries1 = assertDoesNotThrow(() -> inquiryDao.selectFromUserId(1));
		assertEquals(1, inquiries1.size());
		Inquiry inquiry1 = inquiries1.get(0);
		assertEquals(0, inquiry1.getId());
		assertEquals(1, inquiry1.getUserId());
		assertEquals("Test", inquiry1.getTitle());
		assertEquals("Test Content", inquiry1.getContent());
		assertEquals(true, inquiry1.isAnonymous());
	}

	@Test
	public void testSelectSuccess() {
		Inquiry inquiry = assertDoesNotThrow(() -> inquiryDao.select(1));
		assertEquals(1, inquiry.getId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Example", inquiry.getTitle());
		assertEquals("Example Content", inquiry.getContent());
		assertEquals(true, inquiry.isAnonymous());
	}

	@Test
	public void testUpdateSuccess() {
		assertDoesNotThrow(() -> inquiryDao.update(new Inquiry(0, 1, "Updated", "Updated Content", false)));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 0"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals(0, assertDoesNotThrow(() -> result.getInt("inquiry_id")));
		assertEquals(1, assertDoesNotThrow(() -> result.getInt("user_id")));
		assertEquals("Updated", assertDoesNotThrow(() -> result.getString("title")));
		assertEquals("Updated Content", assertDoesNotThrow(() -> result.getString("content")));
		assertEquals(false, assertDoesNotThrow(() -> result.getBoolean("anonymous")));
	}

	@Test
	public void testUpdateException() {
		assertThrows(SQLRunnerExecuteException.class, () -> inquiryDao.update(new Inquiry(0, 3, "Fail", "Fail Content", false)));
		assertThrows(SQLRunnerExecuteException.class, () -> inquiryDao.update(new Inquiry(0, 1, null, null, false)));
	}

}
