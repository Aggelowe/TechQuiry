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
import com.aggelowe.techquiry.database.entities.Upvote;
import com.aggelowe.techquiry.database.exceptions.SQLRunnerExecuteException;

public class UpvoteDaoTest {

	Connection connection;
	UpvoteDao upvoteDao;

	@BeforeEach
	public void initialize() {
		String databaseUrl = "jdbc:sqlite::memory:";
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connection = assertDoesNotThrow(() -> DriverManager.getConnection(databaseUrl, config.toProperties()));
		assertDoesNotThrow(() -> connection.setAutoCommit(false));
		upvoteDao = new UpvoteDao(new SQLRunner(connection));
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
			statement.execute("CREATE TABLE IF NOT EXISTS \"response\" (\n"
							+ "	\"response_id\" INTEGER NOT NULL UNIQUE,\n"
							+ "	\"inquiry_id\" INTEGER NOT NULL,\n"
							+ "	\"user_id\" INTEGER NOT NULL,\n"
							+ "	\"anonymous\" INTEGER NOT NULL,\n"
							+ "	\"content\" TEXT NOT NULL,\n"
							+ "	PRIMARY KEY(\"response_id\"),\n"
							+ "	FOREIGN KEY (\"inquiry_id\") REFERENCES \"inquiry\"(\"inquiry_id\")\n"
							+ "	ON UPDATE CASCADE ON DELETE CASCADE,\n"
							+ "	FOREIGN KEY (\"user_id\") REFERENCES \"user_login\"(\"user_id\")\n"
							+ "	ON UPDATE CASCADE ON DELETE CASCADE\n"
							+ ");");
			statement.execute("CREATE TABLE IF NOT EXISTS \"upvote\" (\n"
							+ "	\"response_id\" INTEGER NOT NULL,\n"
							+ "	\"user_id\" INTEGER NOT NULL,\n"
							+ "	PRIMARY KEY(\"response_id\", \"user_id\"),\n"
							+ "	FOREIGN KEY (\"response_id\") REFERENCES \"response\"(\"response_id\")\n"
							+ "	ON UPDATE CASCADE ON DELETE CASCADE,\n"
							+ "	FOREIGN KEY (\"user_id\") REFERENCES \"user_login\"(\"user_id\")\n"
							+ "	ON UPDATE CASCADE ON DELETE CASCADE\n"
							+ ");");
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
		});
	}

	@AfterEach
	public void destroy() {
		if (connection != null) {
			assertDoesNotThrow(() -> connection.close());
		}
	}

	@Test
	public void testDeleteSuccess() {
		assertDoesNotThrow(() -> upvoteDao.delete(new Upvote(0, 0)));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM upvote WHERE response_id = 0"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertFalse(assertDoesNotThrow(() -> result.next()));
	}

	@Test
	public void testInsertSuccess() {
		assertDoesNotThrow(() -> upvoteDao.insert(new Upvote(2, 1)));
		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
		assertDoesNotThrow(() -> statement.execute("SELECT * FROM upvote WHERE response_id = 2 AND user_id = 1"));
		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
		assertNotNull(result);
		assertTrue(assertDoesNotThrow(() -> result.next()));
		assertEquals(2, assertDoesNotThrow(() -> result.getInt("response_id")));
		assertEquals(1, assertDoesNotThrow(() -> result.getInt("user_id")));
	}

	@Test
	public void testInsertException() {
		assertThrows(SQLRunnerExecuteException.class, () -> upvoteDao.insert(new Upvote(0, 0)));
		assertThrows(SQLRunnerExecuteException.class, () -> upvoteDao.insert(new Upvote(3, 2)));
	}

	@Test
	public void testSelectFromResponseIdSuccess() {
		List<Upvote> upvotes = assertDoesNotThrow(() -> upvoteDao.selectFromResponseId(0));
		assertEquals(1, upvotes.size());
		Upvote upvote = upvotes.getFirst();
		assertEquals(0, upvote.getResponseId());
		assertEquals(0, upvote.getUserId());
	}

	@Test
	public void testSelectFromUserIdSuccess() {
		List<Upvote> upvotes = assertDoesNotThrow(() -> upvoteDao.selectFromUserId(1));
		assertEquals(1, upvotes.size());
		Upvote upvote = upvotes.getFirst();
		assertEquals(1, upvote.getResponseId());
		assertEquals(1, upvote.getUserId());
	}

}
