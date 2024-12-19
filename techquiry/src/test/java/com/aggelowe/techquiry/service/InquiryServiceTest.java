package com.aggelowe.techquiry.service;

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

import com.aggelowe.techquiry.database.DatabaseManager;
import com.aggelowe.techquiry.database.entities.Inquiry;
import com.aggelowe.techquiry.database.entities.UserLogin;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exceptions.InvalidRequestException;

public class InquiryServiceTest {

	Connection connection;
	InquiryService inquiryService;

	@BeforeEach
	public void initialize() {
		String databaseUrl = "jdbc:sqlite::memory:";
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connection = assertDoesNotThrow(() -> DriverManager.getConnection(databaseUrl, config.toProperties()));
		assertDoesNotThrow(() -> connection.setAutoCommit(false));
		DatabaseManager manager = new DatabaseManager(connection);
		inquiryService = new InquiryService(manager);
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
	public void testGetInquiryCountSuccess() {
		int count = assertDoesNotThrow(() -> inquiryService.getInquiryCount());
		assertEquals(3, count);
	}

	@Test
	public void testGetInquiryRangeSuccess() {
		List<Inquiry> inquiries = assertDoesNotThrow(() -> inquiryService.getInquiryRange(2, 1));
		assertEquals(1, inquiries.size());
		Inquiry inquiry = inquiries.get(0);
		assertEquals(2, inquiry.getId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Instance", inquiry.getTitle());
		assertEquals("Instance Content", inquiry.getContent());
		assertEquals(false, inquiry.isAnonymous());
	}

	@Test
	public void testFindInquiryByInquiryIdSuccess() {
		Inquiry inquiry = assertDoesNotThrow(() -> inquiryService.findInquiryByInquiryId(1));
		assertEquals(1, inquiry.getId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Example", inquiry.getTitle());
		assertEquals("Example Content", inquiry.getContent());
		assertEquals(true, inquiry.isAnonymous());
	}

	@Test
	public void testFindInquiryByInquiryIdException() {
		assertThrows(EntityNotFoundException.class, () -> inquiryService.findInquiryByInquiryId(3));
	}

	@Test
	public void testGetInquiryListByUserIdSuccess() {
		List<Inquiry> inquiries0 = assertDoesNotThrow(() -> inquiryService.createActionService(null).getInquiryListByUserId(0));
		assertEquals(1, inquiries0.size());
		Inquiry inquiry = inquiries0.get(0);
		assertEquals(2, inquiry.getId());
		assertEquals(0, inquiry.getUserId());
		assertEquals("Instance", inquiry.getTitle());
		assertEquals("Instance Content", inquiry.getContent());
		assertEquals(false, inquiry.isAnonymous());
		UserLogin current = new UserLogin(0, "alice", "test");
		List<Inquiry> inquiries1 = assertDoesNotThrow(() -> inquiryService.createActionService(current).getInquiryListByUserId(0));
		assertEquals(2, inquiries1.size());
	}

	@Test
	public void testGetInquiryListByUserIdException() {
		assertThrows(EntityNotFoundException.class, () -> inquiryService.createActionService(null).getInquiryListByUserId(3));
	}

	@Test
	public void testCreateInquirySuccess() {
		UserLogin current = new UserLogin(0, "alice", "test");
		Inquiry target = new Inquiry(0, 0, "Success", "Success Content", false);
		int id = assertDoesNotThrow(() -> inquiryService.createActionService(current).createInquiry(target));
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
	public void testCreateInquiryException() {
		Inquiry target0 = new Inquiry(0, 1, "Fail", "Fail Content", true);
		assertThrows(ForbiddenOperationException.class, () -> inquiryService.createActionService(null).createInquiry(target0));
		UserLogin current0 = new UserLogin(0, "alice", "test");
		assertThrows(ForbiddenOperationException.class, () -> inquiryService.createActionService(current0).createInquiry(target0));
		Inquiry target1 = new Inquiry(0, 2, "Fail", "Fail Content", true);
		UserLogin current1 = new UserLogin(2, "charlie", "test");
		assertThrows(EntityNotFoundException.class, () -> inquiryService.createActionService(current1).createInquiry(target1));
		Inquiry target2 = new Inquiry(0, 1, "", "Fail Content", false);
		UserLogin current2 = new UserLogin(1, "bob", "test");
		assertThrows(InvalidRequestException.class, () -> inquiryService.createActionService(current2).createInquiry(target2));
		Inquiry target3 = new Inquiry(0, 1, "Fail", "", false);
		assertThrows(InvalidRequestException.class, () -> inquiryService.createActionService(current2).createInquiry(target3));
	}

	@Test
	public void testDeleteInquirySuccess() {
		UserLogin current = new UserLogin(0, "alice", "test");
		assertDoesNotThrow(() -> inquiryService.createActionService(current).deleteInquiry(1));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 1"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertFalse(assertDoesNotThrow(() -> result.next()));
	}

	@Test
	public void testDeleteInquiryException() {
		assertThrows(ForbiddenOperationException.class, () -> inquiryService.createActionService(null).deleteInquiry(1));
		UserLogin current = new UserLogin(1, "bob", "test");
		assertThrows(ForbiddenOperationException.class, () -> inquiryService.createActionService(current).deleteInquiry(1));
		assertThrows(EntityNotFoundException.class, () -> inquiryService.createActionService(current).deleteInquiry(3));
	}

	@Test
	public void testUpdateInquirySuccess() {
		UserLogin current = new UserLogin(0, "alice", "test");
		Inquiry target = new Inquiry(1, 0, "Updated", "Updated Content", false);
		assertDoesNotThrow(() -> inquiryService.createActionService(current).updateInquiry(target));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 1"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals(1, assertDoesNotThrow(() -> result.getInt("inquiry_id")));
		assertEquals(0, assertDoesNotThrow(() -> result.getInt("user_id")));
		assertEquals("Updated", assertDoesNotThrow(() -> result.getString("title")));
		assertEquals("Updated Content", assertDoesNotThrow(() -> result.getString("content")));
		assertEquals(false, assertDoesNotThrow(() -> result.getBoolean("anonymous")));
	}

	@Test
	public void testUpdateInquiryException() {
		Inquiry target0 = new Inquiry(0, 1, "Fail", "Fail Content", true);
		assertThrows(ForbiddenOperationException.class, () -> inquiryService.createActionService(null).updateInquiry(target0));
		UserLogin current0 = new UserLogin(0, "alice", "test");
		assertThrows(ForbiddenOperationException.class, () -> inquiryService.createActionService(current0).updateInquiry(target0));
		Inquiry target1 = new Inquiry(1, 1, "Fail", "Fail Content", true);
		assertThrows(ForbiddenOperationException.class, () -> inquiryService.createActionService(current0).updateInquiry(target1));
		Inquiry target2 = new Inquiry(0, 2, "Fail", "Fail Content", true);
		UserLogin current1 = new UserLogin(2, "charlie", "test");
		assertThrows(EntityNotFoundException.class, () -> inquiryService.createActionService(current1).createInquiry(target2));
		Inquiry target3 = new Inquiry(0, 1, "", "Fail Content", false);
		UserLogin current2 = new UserLogin(1, "bob", "test");
		assertThrows(InvalidRequestException.class, () -> inquiryService.createActionService(current2).createInquiry(target3));
		Inquiry target4 = new Inquiry(0, 1, "Fail", "", false);
		assertThrows(InvalidRequestException.class, () -> inquiryService.createActionService(current2).createInquiry(target4));

	}

}
