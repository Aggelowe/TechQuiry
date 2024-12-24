//package com.aggelowe.techquiry.service;
//
//import static org.junit.jupiter.api.Assertions.assertArrayEquals;
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.Statement;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.sqlite.SQLiteConfig;
//
//import com.aggelowe.techquiry.database.DatabaseManager;
//import com.aggelowe.techquiry.database.entities.UserData;
//import com.aggelowe.techquiry.database.entities.UserLogin;
//import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
//import com.aggelowe.techquiry.service.exceptions.ForbiddenOperationException;
//import com.aggelowe.techquiry.service.exceptions.InvalidRequestException;
//
//public class UserDataServiceTest {
//
//	Connection connection;
//	UserDataService userDataService;
//
//	@BeforeEach
//	public void initialize() {
//		String databaseUrl = "jdbc:sqlite::memory:";
//		SQLiteConfig config = new SQLiteConfig();
//		config.enforceForeignKeys(true);
//		connection = assertDoesNotThrow(() -> DriverManager.getConnection(databaseUrl, config.toProperties()));
//		assertDoesNotThrow(() -> connection.setAutoCommit(false));
//		DatabaseManager manager = new DatabaseManager(connection);
//		assertDoesNotThrow(() -> {
//			Statement statement = connection.createStatement();
//			statement.execute("CREATE TABLE IF NOT EXISTS \"user_login\" (\n"
//					+ "	\"user_id\" INTEGER NOT NULL UNIQUE,\n"
//					+ "	\"username\" TEXT NOT NULL UNIQUE,\n"
//					+ "	\"password_hash\" TEXT NOT NULL,\n"
//					+ "	\"password_salt\" TEXT NOT NULL,\n"
//					+ "	PRIMARY KEY(\"user_id\")\n"
//					+ ");\n");
//			statement.execute("CREATE TABLE IF NOT EXISTS \"user_data\" (\n"
//					+ "	\"user_id\" INTEGER NOT NULL UNIQUE,\n"
//					+ "	\"first_name\" TEXT NOT NULL,\n"
//					+ "	\"last_name\" TEXT NOT NULL,\n"
//					+ "	\"icon\" BLOB,\n"
//					+ "	PRIMARY KEY(\"user_id\"),\n"
//					+ "	FOREIGN KEY (\"user_id\") REFERENCES \"user_login\"(\"user_id\")\n"
//					+ "	ON UPDATE CASCADE ON DELETE CASCADE\n"
//					+ ");");
//			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
//			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
//			statement.execute("INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(2, 'charlie', 'YWJjZGFiY2Q=', 'YWJjZA==');");
//			statement.execute("INSERT INTO user_data(user_id, first_name, last_name, icon) VALUES(0, 'Alice', 'Smith', X'0000');");
//			statement.execute("INSERT INTO user_data(user_id, first_name, last_name, icon) VALUES(1, 'Bob', 'Johnson', NULL);");
//			connection.commit();
//		});
//	}
//
//	@AfterEach
//	public void destroy() {
//		if (connection != null) {
//			assertDoesNotThrow(() -> connection.close());
//		}
//	}
//
//	@Test
//	public void testFindDataByUserIdSuccess() {
//		UserData userData = assertDoesNotThrow(() -> userDataService.findDataByUserId(1));
//		assertEquals(1, userData.getId());
//		assertEquals("Bob", userData.getFirstName());
//		assertEquals("Johnson", userData.getLastName());
//		assertEquals(null, userData.getIcon());
//	}
//
//	@Test
//	public void testFindDataByUserIdException() {
//		assertThrows(EntityNotFoundException.class, () -> userDataService.findDataByUserId(2));
//	}
//
//	@Test
//	public void testCreateDataSuccess() {
//		UserLogin current = new UserLogin(2, "charlie", "test");
//		UserData target = new UserData(2, "Charlie", "Brown", null);
//		assertDoesNotThrow(() -> userDataService.createActionService(current).createData(target));
//		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
//		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_data WHERE user_id = 2"));
//		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
//		assertNotNull(result);
//		assertTrue(assertDoesNotThrow(() -> result.next()));
//		assertEquals(2, assertDoesNotThrow(() -> result.getInt("user_id")));
//		assertEquals("Charlie", assertDoesNotThrow(() -> result.getString("first_name")));
//		assertEquals("Brown", assertDoesNotThrow(() -> result.getString("last_name")));
//		assertNull(assertDoesNotThrow(() -> result.getBytes("icon")));
//	}
//
//	@Test
//	public void testCreateDataException() {
//		UserData target0 = new UserData(2, "Charlie", "Brown", null);
//		assertThrows(ForbiddenOperationException.class, () -> userDataService.createActionService(null).createData(target0));
//		UserLogin current0 = new UserLogin(0, "david", "test");
//		assertThrows(ForbiddenOperationException.class, () -> userDataService.createActionService(current0).createData(target0));
//		UserLogin current1 = new UserLogin(2, "charlie", "test");
//		UserData target1 = new UserData(2, "", "Brown", null);
//		assertThrows(InvalidRequestException.class, () -> userDataService.createActionService(current1).createData(target1));
//		UserData target2 = new UserData(2, "Charlie", "", null);
//		assertThrows(InvalidRequestException.class, () -> userDataService.createActionService(current1).createData(target2));
//		UserLogin current2 = new UserLogin(1, "bob", "test");
//		UserData target3 = new UserData(1, "Charlie", "Brown", null);
//		assertThrows(InvalidRequestException.class, () -> userDataService.createActionService(current2).createData(target3));
//		UserLogin current3 = new UserLogin(3, "david", "test");
//		UserData target4 = new UserData(3, "Charlie", "Brown", null);
//		assertThrows(EntityNotFoundException.class, () -> userDataService.createActionService(current3).createData(target4));
//	}
//
//	@Test
//	public void testDeleteDataSuccess() {
//		UserLogin current = new UserLogin(1, "bob", "test");
//		assertDoesNotThrow(() -> userDataService.createActionService(current).deleteData(1));
//		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
//		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_data WHERE user_id = 1"));
//		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
//		assertNotNull(result);
//		assertFalse(assertDoesNotThrow(() -> result.next()));
//	}
//
//	@Test
//	public void testDeleteDataException() {
//		assertThrows(ForbiddenOperationException.class, () -> userDataService.createActionService(null).deleteData(1));
//		UserLogin current0 = new UserLogin(1, "bob", "test");
//		assertThrows(ForbiddenOperationException.class, () -> userDataService.createActionService(current0).deleteData(0));
//		UserLogin current1 = new UserLogin(3, "david", "test");
//		assertThrows(EntityNotFoundException.class, () -> userDataService.createActionService(current1).deleteData(3));
//	}
//
//	@Test
//	public void testUpdateDataSuccess() {
//		UserLogin current = new UserLogin(1, "bob", "test");
//		UserData target = new UserData(1, "David", "Dawson", new byte[2]);
//		assertDoesNotThrow(() -> userDataService.createActionService(current).updateData(target));
//		Statement statement = assertDoesNotThrow(() -> connection.createStatement());
//		assertDoesNotThrow(() -> statement.execute("SELECT * FROM user_data WHERE user_id = 1"));
//		ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
//		assertNotNull(result);
//		assertTrue(assertDoesNotThrow(() -> result.next()));
//		assertEquals(1, assertDoesNotThrow(() -> result.getInt("user_id")));
//		assertEquals("David", assertDoesNotThrow(() -> result.getString("first_name")));
//		assertEquals("Dawson", assertDoesNotThrow(() -> result.getString("last_name")));
//		assertArrayEquals(new byte[2], assertDoesNotThrow(() -> result.getBytes("icon")));
//	}
//
//	@Test
//	public void testUpdateDataException() {
//		UserData target0 = new UserData(1, "David", "Dawson", new byte[2]);
//		assertThrows(ForbiddenOperationException.class, () -> userDataService.createActionService(null).updateData(target0));
//		UserLogin current0 = new UserLogin(0, "alice", "test");
//		assertThrows(ForbiddenOperationException.class, () -> userDataService.createActionService(current0).updateData(target0));
//		UserLogin current1 = new UserLogin(1, "bob", "test");
//		UserData target1 = new UserData(1, "", "Brown", null);
//		assertThrows(InvalidRequestException.class, () -> userDataService.createActionService(current1).updateData(target1));
//		UserData target2 = new UserData(1, "Charlie", "", null);
//		assertThrows(InvalidRequestException.class, () -> userDataService.createActionService(current1).updateData(target2));
//		UserLogin current2 = new UserLogin(2, "david", "test");
//		UserData target3 = new UserData(2, "Charlie", "Brown", null);
//		assertThrows(EntityNotFoundException.class, () -> userDataService.createActionService(current2).updateData(target3));
//
//	}
//
//}
