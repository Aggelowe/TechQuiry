package com.aggelowe.techquiry.service;

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

import com.aggelowe.techquiry.database.common.TestAppConfiguration;
import com.aggelowe.techquiry.database.entity.Response;
import com.aggelowe.techquiry.service.action.ResponseActionService;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.session.Authentication;
import com.aggelowe.techquiry.service.session.SessionHelper;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class ResponseServiceTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	ResponseService responseService;

	@Autowired
	ResponseActionService responseActionService;

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
	void testGetResponseCountByInquiryIdSuccess() {
		int count = assertDoesNotThrow(() -> responseService.getResponseCountByInquiryId(2));
		assertEquals(2, count);
	}

	@Test
	void testGetResponseCountByInquiryIdException() {
		assertThrows(EntityNotFoundException.class, () -> responseService.getResponseCountByInquiryId(3));
	}

	@Test
	void testGetResponseListByInquiryIdSuccess() {
		List<Response> responses = assertDoesNotThrow(() -> responseService.getResponseListByInquiryId(2));
		assertEquals(2, responses.size());
		Response response = responses.getFirst();
		assertEquals(1, response.getResponseId());
		assertEquals(2, response.getInquiryId());
		assertEquals(1, response.getUserId());
		assertEquals(false, response.getAnonymous());
		assertEquals("Instance Response", response.getContent());
	}

	@Test
	void testGetResponseListByInquiryIdException() {
		assertThrows(EntityNotFoundException.class, () -> responseService.getResponseListByInquiryId(3));
	}

	@Test
	void testGetResponseByResponseIdSuccess() {
		Response response = assertDoesNotThrow(() -> responseService.getResponseByResponseId(1));
		assertEquals(1, response.getResponseId());
		assertEquals(2, response.getInquiryId());
		assertEquals(1, response.getUserId());
		assertEquals(false, response.getAnonymous());
		assertEquals("Instance Response", response.getContent());
	}

	@Test
	void testGetResponseByResponseIdException() {
		assertThrows(EntityNotFoundException.class, () -> responseService.getResponseByResponseId(3));
	}

	@Test
	void testCreateResponseSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		Response target = new Response(0, 1, 0, true, "Example Response");
		int id = assertDoesNotThrow(() -> responseActionService.createResponse(target));
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
	void testCreateResponseException() {
		sessionHelper.setAuthentication(null);
		Response target0 = new Response(0, 2, 0, true, "Fail Response");
		assertThrows(ForbiddenOperationException.class, () -> responseActionService.createResponse(target0));
		Response target1 = new Response(0, 3, 0, true, "Fail Response");
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrows(EntityNotFoundException.class, () -> responseActionService.createResponse(target1));
		Response target2 = new Response(0, 2, 0, true, "");
		assertThrows(InvalidRequestException.class, () -> responseActionService.createResponse(target2));
	}

	@Test
	void testDeleteResponseSuccess() {
		sessionHelper.setAuthentication(new Authentication(1));
		assertDoesNotThrow(() -> responseActionService.deleteResponse(1));
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
	void testDeleteResponseException() {
		sessionHelper.setAuthentication(null);
		assertThrows(ForbiddenOperationException.class, () -> responseActionService.deleteResponse(1));
		sessionHelper.setAuthentication(new Authentication(0));
		assertThrows(ForbiddenOperationException.class, () -> responseActionService.deleteResponse(1));
		assertThrows(EntityNotFoundException.class, () -> responseActionService.deleteResponse(3));
	}

	@Test
	void testUpdateResponseSuccess() {
		sessionHelper.setAuthentication(new Authentication(0));
		Response target = new Response(0, 1, 0, false, "Updated Response");
		assertDoesNotThrow(() -> responseActionService.updateResponse(target));
		assertDoesNotThrow(() -> {
			try (Connection connection = dataSource.getConnection()) {
				Statement statement = connection.createStatement();
				statement.execute("SELECT * FROM response WHERE response_id = 0");
				ResultSet result = statement.getResultSet();
				assertNotNull(result);
				assertTrue(result.next());
				assertEquals(0, result.getInt("response_id"));
				assertEquals(1, result.getInt("inquiry_id"));
				assertEquals(0, result.getInt("user_id"));
				assertEquals(false, result.getBoolean("anonymous"));
				assertEquals("Updated Response", result.getString("content"));
			}
		});
	}

	@Test
	void testUpdateResponseException() {
		sessionHelper.setAuthentication(null);
		Response target0 = new Response(0, 1, 0, false, "Fail Response");
		assertThrows(ForbiddenOperationException.class, () -> responseActionService.updateResponse(target0));
		sessionHelper.setAuthentication(new Authentication(1));
		assertThrows(ForbiddenOperationException.class, () -> responseActionService.updateResponse(target0));
		Response target1 = new Response(3, 1, 0, false, "Fail Response");
		sessionHelper.setAuthentication(new Authentication(0));
		assertThrows(EntityNotFoundException.class, () -> responseActionService.updateResponse(target1));
		Response target2 = new Response(0, 3, 0, false, "Fail Response");
		assertThrows(EntityNotFoundException.class, () -> responseActionService.updateResponse(target2));
		Response target3 = new Response(0, 1, 0, false, "");
		assertThrows(InvalidRequestException.class, () -> responseActionService.updateResponse(target3));
	}

}
