package com.aggelowe.techquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class contains the response data to be transfered between the client and
 * server.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class ResponseDto {

	/**
	 * The content of the response
	 */
	private String content;

	/**
	 * Whether the author is anonymous
	 */
	private Boolean anonymous;

}
