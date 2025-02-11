package com.aggelowe.techquiry.mapper;

import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.database.entity.Inquiry;
import com.aggelowe.techquiry.database.entity.Inquiry.InquiryBuilder;
import com.aggelowe.techquiry.dto.InquiryDto;
import com.aggelowe.techquiry.dto.InquiryDto.InquiryDtoBuilder;
import com.aggelowe.techquiry.mapper.exception.MapperException;
import com.aggelowe.techquiry.mapper.exception.MissingValueException;

/**
 * The {@link InquiryMapper} class is responsible for mapping between
 * {@link Inquiry} and {@link InquiryDto} objects.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
public class InquiryMapper {

	/**
	 * This method maps the given {@link Inquiry} to a {@link InquiryDto} object.
	 * 
	 * @param inquiry The inquiry entity to map
	 * @return The inquiry DTO
	 */
	public InquiryDto toDto(Inquiry inquiry) {
		InquiryDtoBuilder builder = InquiryDto.builder();
		builder.inquiryId(inquiry.getInquiryId());
		builder.title(inquiry.getTitle());
		builder.content(inquiry.getContent());
		boolean anonymous = inquiry.getAnonymous();
		builder.anonymous(anonymous);
		if (!anonymous) {
			builder.userId(inquiry.getUserId());
		}
		return builder.build();
	}

	/**
	 * This method creates a new {@link Inquiry} object based on the data of the
	 * given {@link InquiryDto}.
	 * 
	 * @param inquiryDto The data transfer object to map
	 * @return The new inquiry entity
	 * @throws MissingValueException If the title, content or anonymous flag in the
	 *                               DTO are missing
	 */
	public Inquiry toEntity(InquiryDto inquiryDto) throws MapperException {
		String title = inquiryDto.getTitle();
		String content = inquiryDto.getContent();
		Boolean anonymous = inquiryDto.getAnonymous();
		if (title == null || content == null || anonymous == null) {
			throw new MissingValueException("The title, content and/or anonymous flag is missing!");
		}
		return new Inquiry(0, 0, title, content, anonymous);
	}

	/**
	 * This method creates a new {@link Inquiry} object whose data are a copy of the
	 * original entity and whose data are changed according to the given
	 * {@link InquiryDto}.
	 * 
	 * @param inquiryDto The data transfer object to map
	 * @param original   The entity to draw the original data from
	 * @return The new inquiry entity
	 */
	public Inquiry updateEntity(InquiryDto inquiryDto, Inquiry original) {
		String title = inquiryDto.getTitle();
		String content = inquiryDto.getContent();
		Boolean anonymous = inquiryDto.getAnonymous();
		InquiryBuilder builder = original.toBuilder();
		if (title != null) {
			builder.title(title);
		}
		if (content != null) {
			builder.content(content);
		}
		if (anonymous != null) {
			builder.anonymous(anonymous);
		}
		return builder.build();
	}

}
