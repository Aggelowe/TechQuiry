package net.techquiry.app.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.techquiry.app.common.TestAppConfiguration;
import net.techquiry.app.dto.UserDataDto;
import net.techquiry.app.entity.UserData;
import net.techquiry.app.mapper.exception.MissingValueException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class UserDataMapperTest {

	@Autowired
	UserDataMapper userDataMapper;

	@Test
	void testToDtoSuccess() {
		UserData userData = new UserData(1, "Bob", "Johnson");
		UserDataDto userDataDto = userDataMapper.toDto(userData);
		assertEquals(1, userDataDto.getUserId());
		assertEquals("Bob", userDataDto.getFirstName());
		assertEquals("Johnson", userDataDto.getLastName());
	}

	@Test
	void testToEntitySuccess() {
		UserDataDto userDataDto = new UserDataDto(null, "Bob", "Johnson");
		UserData userData = assertDoesNotThrow(() -> userDataMapper.toEntity(userDataDto));
		assertEquals("Bob", userData.getFirstName());
		assertEquals("Johnson", userData.getLastName());
	}

	@Test
	void testToEntityException() {
		UserDataDto target0 = new UserDataDto(null, null, "Johnson");
		assertThrowsExactly(MissingValueException.class, () -> userDataMapper.toEntity(target0));
		UserDataDto target1 = new UserDataDto(null, "Bob", null);
		assertThrowsExactly(MissingValueException.class, () -> userDataMapper.toEntity(target1));
	}

	@Test
	void testUpdateEntitySuccess() {
		UserDataDto userDataDto = new UserDataDto(null, "Bob", "Johnson");
		UserData original = new UserData(1, "Alice", "Smith");
		UserData userData = userDataMapper.updateEntity(userDataDto, original);
		assertEquals(1, userData.getUserId());
		assertEquals("Bob", userData.getFirstName());
		assertEquals("Johnson", userData.getLastName());
	}

}
