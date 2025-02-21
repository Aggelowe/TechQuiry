package com.aggelowe.techquiry.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.aggelowe.techquiry.common.TestAppConfiguration;
import com.aggelowe.techquiry.dto.InquiryDto;
import com.aggelowe.techquiry.entity.Inquiry;
import com.aggelowe.techquiry.mapper.exception.MissingValueException;

@SpringBootTest(classes = TestAppConfiguration.class)
@ExtendWith(SpringExtension.class)
class InquiryMapperTest {

	@Autowired
	InquiryMapper inquiryMapper;

	@Test
	void testToDtoSuccess() {
		Inquiry inquiry = new Inquiry(1, 0, "Example", "Example Content", true);
		InquiryDto inquiryDto = inquiryMapper.toDto(inquiry);
		assertEquals(1, inquiryDto.getInquiryId());
		assertNull(inquiryDto.getUserId());
		assertEquals("Example", inquiryDto.getTitle());
		assertEquals("Example Content", inquiryDto.getContent());
		assertEquals(true, inquiryDto.getAnonymous());
	}

	@Test
	void testToEntitySuccess() {
		InquiryDto inquiryDto = new InquiryDto(null, null, "Example", "Example Content", true);
		Inquiry inquiry = assertDoesNotThrow(() -> inquiryMapper.toEntity(inquiryDto));
		assertEquals("Example", inquiry.getTitle());
		assertEquals("Example Content", inquiry.getContent());
		assertEquals(true, inquiry.getAnonymous());
	}

	@Test
	void testToEntityException() {
		InquiryDto target0 = new InquiryDto(null, null, null, "Example Content", true);
		assertThrowsExactly(MissingValueException.class, () -> inquiryMapper.toEntity(target0));
		InquiryDto target1 = new InquiryDto(null, null, "Example", null, true);
		assertThrowsExactly(MissingValueException.class, () -> inquiryMapper.toEntity(target1));
		InquiryDto target2 = new InquiryDto(null, null, "Example", "Example Content", null);
		assertThrowsExactly(MissingValueException.class, () -> inquiryMapper.toEntity(target2));
	}

	@Test
	void testUpdateEntitySuccess() {
		InquiryDto inquiryDto = new InquiryDto(null, null, "Example", "Example Content", true);
		Inquiry original = new Inquiry(2, 0, "Instance", "Instance Content", false);
		Inquiry inquiry = inquiryMapper.updateEntity(inquiryDto, original);
		assertEquals("Example", inquiry.getTitle());
		assertEquals("Example Content", inquiry.getContent());
		assertEquals(true, inquiry.getAnonymous());
	}

}
