package com.aggelowe.techquiry.service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.aggelowe.techquiry.config.ConnectionFactoryTest;
import com.aggelowe.techquiry.config.TestAppConfiguration;
import com.aggelowe.techquiry.database.entities.Inquiry;
import com.aggelowe.techquiry.helper.UserSessionHelper;
import com.aggelowe.techquiry.helper.UserSessionHelperTestImpl;
import com.aggelowe.techquiry.service.action.InquiryActionService;
import com.aggelowe.techquiry.service.exceptions.EntityNotFoundException;
import com.aggelowe.techquiry.service.exceptions.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exceptions.InvalidRequestException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class InquiryServiceTest {

    @Autowired
    ConnectionFactoryTest connectionFactory;

    Connection connection;
    
    @Autowired
    InquiryService inquiryService;

    @Autowired
    UserSessionHelper userSessionHelper;

    @Autowired
    InquiryActionService inquiryActionService;

    @BeforeEach
    public void initialize() {
        connection = connectionFactory.createConnection();
        assertDoesNotThrow(() -> {
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
            statement.execute(
                    "INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(0, 'alice', 'MTIzNDU2Nzg=', 'MTIzNA==');");
            statement.execute(
                    "INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(1, 'bob', 'cGFzc3dvcmQ=', 'cGFzcw==');");
            statement.execute(
                    "INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(0, 1, 'Test', 'Test Content', true);");
            statement.execute(
                    "INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(1, 0, 'Example',  'Example Content', true);");
            statement.execute(
                    "INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(2, 0, 'Instance', 'Instance Content', false);");
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
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(null);
        List<Inquiry> inquiries0 = assertDoesNotThrow(() -> inquiryActionService.getInquiryListByUserId(0));
        assertEquals(1, inquiries0.size());
        Inquiry inquiry = inquiries0.get(0);
        assertEquals(2, inquiry.getId());
        assertEquals(0, inquiry.getUserId());
        assertEquals("Instance", inquiry.getTitle());
        assertEquals("Instance Content", inquiry.getContent());
        assertEquals(false, inquiry.isAnonymous());
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(0));
        List<Inquiry> inquiries1 = assertDoesNotThrow(() -> inquiryActionService.getInquiryListByUserId(0));
        assertEquals(2, inquiries1.size());
    }

    @Test
    public void testGetInquiryListByUserIdException() {
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(null);
        assertThrows(EntityNotFoundException.class, () -> inquiryActionService.getInquiryListByUserId(3));
    }

    @Test
    public void testCreateInquirySuccess() {
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(0));
        Inquiry target = new Inquiry(0, 0, "Success", "Success Content", false);
        int id = assertDoesNotThrow(() -> inquiryActionService.createInquiry(target));
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
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(null);
        Inquiry target0 = new Inquiry(0, 1, "Fail", "Fail Content", true);
        assertThrows(ForbiddenOperationException.class, () -> inquiryActionService.createInquiry(target0));
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(0));
        assertThrows(ForbiddenOperationException.class, () -> inquiryActionService.createInquiry(target0));
        Inquiry target1 = new Inquiry(0, 2, "Fail", "Fail Content", true);
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(2));
        assertThrows(EntityNotFoundException.class, () -> inquiryActionService.createInquiry(target1));
        Inquiry target2 = new Inquiry(0, 1, "", "Fail Content", false);
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(1));
        assertThrows(InvalidRequestException.class, () -> inquiryActionService.createInquiry(target2));
        Inquiry target3 = new Inquiry(0, 1, "Fail", "", false);
        assertThrows(InvalidRequestException.class, () -> inquiryActionService.createInquiry(target3));
    }

    @Test
    public void testDeleteInquirySuccess() {
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(0));
        assertDoesNotThrow(() -> inquiryActionService.deleteInquiry(1));
        Statement statement = assertDoesNotThrow(() -> connection.createStatement());
        assertDoesNotThrow(() -> statement.execute("SELECT * FROM inquiry WHERE inquiry_id = 1"));
        ResultSet result = assertDoesNotThrow(() -> statement.getResultSet());
        assertNotNull(result);
        assertFalse(assertDoesNotThrow(() -> result.next()));
    }

    @Test
    public void testDeleteInquiryException() {
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(null);
        assertThrows(ForbiddenOperationException.class, () -> inquiryActionService.deleteInquiry(1));
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(1));
        assertThrows(ForbiddenOperationException.class, () -> inquiryActionService.deleteInquiry(1));
        assertThrows(EntityNotFoundException.class, () -> inquiryActionService.deleteInquiry(3));
    }

    @Test
    public void testUpdateInquirySuccess() {
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(0));
        Inquiry target = new Inquiry(1, 0, "Updated", "Updated Content", false);
        assertDoesNotThrow(() -> inquiryActionService.updateInquiry(target));
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
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(null);
        assertThrows(ForbiddenOperationException.class, () -> inquiryActionService.updateInquiry(target0));
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(0));
        assertThrows(ForbiddenOperationException.class, () -> inquiryActionService.updateInquiry(target0));
        Inquiry target1 = new Inquiry(1, 1, "Fail", "Fail Content", true);
        assertThrows(ForbiddenOperationException.class, () -> inquiryActionService.updateInquiry(target1));
        Inquiry target2 = new Inquiry(0, 2, "Fail", "Fail Content", true);
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(2));
        assertThrows(EntityNotFoundException.class, () -> inquiryActionService.createInquiry(target2));
        Inquiry target3 = new Inquiry(0, 1, "", "Fail Content", false);
        ((UserSessionHelperTestImpl) userSessionHelper).setAuthentication(Authentication.of(1));
        assertThrows(InvalidRequestException.class, () -> inquiryActionService.createInquiry(target3));
        Inquiry target4 = new Inquiry(0, 1, "Fail", "", false);
        assertThrows(InvalidRequestException.class, () -> inquiryActionService.createInquiry(target4));

    }

}
