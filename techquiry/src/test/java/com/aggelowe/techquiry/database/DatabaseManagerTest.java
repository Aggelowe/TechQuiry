package com.aggelowe.techquiry.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.aggelowe.techquiry.database.common.TestAppConfiguration;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class DatabaseManagerTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	DatabaseManager databaseManager;

	@Test
	void testCreateSchemaSuccess() {
		assertDoesNotThrow(() -> databaseManager.createSchema());
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = assertDoesNotThrow(() -> connection.createStatement());
				String sql = "SELECT name FROM sqlite_master WHERE type='table';";
				ResultSet result = assertDoesNotThrow(() -> statement.executeQuery(sql));
				List<String> tables = new ArrayList<>();
				assertDoesNotThrow(() -> {
					while (result.next()) {
						tables.add(result.getString("name"));
					}
				});
				assertTrue(tables.contains("user_login"));
				assertTrue(tables.contains("user_data"));
				assertTrue(tables.contains("inquiry"));
				assertTrue(tables.contains("response"));
				assertTrue(tables.contains("observer"));
				assertTrue(tables.contains("upvote"));
				statement.execute("DROP TABLE 'upvote'");
				statement.execute("DROP TABLE 'observer'");
				statement.execute("DROP TABLE 'response'");
				statement.execute("DROP TABLE 'inquiry'");
				statement.execute("DROP TABLE 'user_data'");
				statement.execute("DROP TABLE 'user_login'");
				connection.commit();
			}
		});
	}

}
