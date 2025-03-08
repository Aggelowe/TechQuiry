package net.techquiry.app.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.techquiry.app.common.SecurityUtils;
import net.techquiry.app.common.TestAppConfiguration;
import net.techquiry.app.dto.UserLoginDto;
import net.techquiry.app.entity.UserLogin;
import net.techquiry.app.mapper.exception.MissingValueException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class UserLoginMapperTest {

	@Autowired
	UserLoginMapper userLoginMapper;

	@Test
	void testToDtoSuccess() {
		UserLogin userLogin = new UserLogin(1, "bob", new byte[4], new byte[2]);
		UserLoginDto userLoginDto = userLoginMapper.toDto(userLogin);
		assertEquals(1, userLoginDto.getUserId());
		assertEquals("bob", userLoginDto.getUsername());
		assertNull(userLoginDto.getPassword());
	}

	@Test
	void testToEntitySuccess() {
		UserLoginDto userLoginDto = new UserLoginDto(null, "bob", "password");
		UserLogin userLogin = assertDoesNotThrow(() -> userLoginMapper.toEntity(userLoginDto));
		assertEquals("bob", userLogin.getUsername());
		byte[] salt = userLogin.getPasswordSalt();
		byte[] hash = userLogin.getPasswordHash();
		assertTrue(SecurityUtils.verifyPassword("password", salt, hash));
	}

	@Test
	void testToEntityException() {
		UserLoginDto target0 = new UserLoginDto(null, null, "password");
		assertThrowsExactly(MissingValueException.class, () -> userLoginMapper.toEntity(target0));
		UserLoginDto target1 = new UserLoginDto(null, "bob", null);
		assertThrowsExactly(MissingValueException.class, () -> userLoginMapper.toEntity(target1));

	}

	@Test
	void testUpdateEntitySuccess() {
		UserLoginDto userLoginDto = new UserLoginDto(null, "bob", "password");
		UserLogin original = new UserLogin(1, "alice", new byte[4], new byte[2]);
		UserLogin userLogin = userLoginMapper.updateEntity(userLoginDto, original);
		assertEquals(1, userLogin.getUserId());
		assertEquals("bob", userLogin.getUsername());
		byte[] salt = userLogin.getPasswordSalt();
		byte[] hash = userLogin.getPasswordHash();
		assertTrue(SecurityUtils.verifyPassword("password", salt, hash));

	}

}
