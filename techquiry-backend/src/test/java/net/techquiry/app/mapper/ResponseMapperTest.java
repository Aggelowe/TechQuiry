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
import net.techquiry.app.dto.ResponseDto;
import net.techquiry.app.entity.Response;
import net.techquiry.app.mapper.exception.MissingValueException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class ResponseMapperTest {

	@Autowired
	ResponseMapper responseMapper;

	@Test
	void testToDtoSuccess() {
		Response response = new Response(1, 2, 1, false, "Instance Response");
		ResponseDto responseDto = responseMapper.toDto(response);
		assertEquals(1, responseDto.getResponseId());
		assertEquals(2, responseDto.getInquiryId());
		assertEquals(1, responseDto.getUserId());
		assertEquals(false, responseDto.getAnonymous());
		assertEquals("Instance Response", responseDto.getContent());
	}

	@Test
	void testToEntitySuccess() {
		ResponseDto responseDto = new ResponseDto(null, null, null, false, "Instance Response");
		Response response = assertDoesNotThrow(() -> responseMapper.toEntity(responseDto));
		assertEquals(false, response.getAnonymous());
		assertEquals("Instance Response", response.getContent());
	}

	@Test
	void testToEntityException() {
		ResponseDto target0 = new ResponseDto(null, null, null, null, "Instance Response");
		assertThrowsExactly(MissingValueException.class, () -> responseMapper.toEntity(target0));
		ResponseDto target1 = new ResponseDto(null, null, null, false, null);
		assertThrowsExactly(MissingValueException.class, () -> responseMapper.toEntity(target1));
	}

	@Test
	void testUpdateEntitySuccess() {
		ResponseDto responseDto = new ResponseDto(null, null, null, false, "Instance Response");
		Response original = new Response(0, 0, 0, true, "Test Response");
		Response response = responseMapper.updateEntity(responseDto, original);
		assertEquals(false, response.getAnonymous());
		assertEquals("Instance Response", response.getContent());
	}

}
