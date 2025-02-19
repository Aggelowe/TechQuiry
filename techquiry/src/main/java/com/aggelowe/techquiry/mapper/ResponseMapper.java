package com.aggelowe.techquiry.mapper;

import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.dto.ResponseDto;
import com.aggelowe.techquiry.dto.ResponseDto.ResponseDtoBuilder;
import com.aggelowe.techquiry.entity.Response;
import com.aggelowe.techquiry.entity.Response.ResponseBuilder;
import com.aggelowe.techquiry.mapper.exception.MapperException;
import com.aggelowe.techquiry.mapper.exception.MissingValueException;

/**
 * The {@link ResponseMapper} class is responsible for mapping between
 * {@link Response} and {@link ResponseDto} objects.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
public class ResponseMapper {

	/**
	 * This method maps the given {@link Response} to a {@link ResponseDto} object.
	 * 
	 * @param response The response entity to map
	 * @return The response DTO
	 */
	public ResponseDto toDto(Response response) {
		ResponseDtoBuilder builder = ResponseDto.builder();
		builder.responseId(response.getResponseId());
		builder.inquiryId(response.getInquiryId());
		builder.content(response.getContent());
		boolean anonymous = response.getAnonymous();
		builder.anonymous(anonymous);
		if (!anonymous) {
			builder.userId(response.getUserId());
		}
		return builder.build();
	}

	/**
	 * This method creates a new {@link Response} object based on the data of the
	 * given {@link ResponseDto}.
	 * 
	 * @param responseDto The data transfer object to map
	 * @return The new response entity
	 * @throws MissingValueException If the content or anonymous flag in the DTO are
	 *                               missing
	 */
	public Response toEntity(ResponseDto responseDto) throws MapperException {
		String content = responseDto.getContent();
		Boolean anonymous = responseDto.getAnonymous();
		if (content == null || anonymous == null) {
			throw new MissingValueException("The content and/or anonymous flag is missing!");
		}
		return new Response(0, 0, 0, anonymous, content);
	}

	/**
	 * This method creates a new {@link Response} object whose data are a copy of
	 * the original entity and whose data are changed according to the given
	 * {@link ResponseDto}.
	 * 
	 * @param responseDto The data transfer object to map
	 * @param original    The entity to draw the original data from
	 * @return The new response entity
	 */
	public Response updateEntity(ResponseDto responseDto, Response original) {
		String content = responseDto.getContent();
		Boolean anonymous = responseDto.getAnonymous();
		ResponseBuilder builder = original.toBuilder();
		if (content != null) {
			builder.content(content);
		}
		if (anonymous != null) {
			builder.anonymous(anonymous);
		}
		return builder.build();
	}

}
