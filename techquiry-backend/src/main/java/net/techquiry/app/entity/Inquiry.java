package net.techquiry.app.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The {@link Inquiry} class represents an inquiry entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class Inquiry {

	/**
	 * The unique id of the inquiry
	 */
	@NonNull
	private Integer inquiryId;

	/**
	 * The user id of the author
	 */
	@NonNull
	@ToString.Exclude
	private Integer userId;

	/**
	 * The title of the inquiry
	 */
	@NonNull
	private String title;

	/**
	 * The content of the inquiry
	 */
	@NonNull
	private String content;

	/**
	 * Whether the author is anonymous
	 */
	@NonNull
	private Boolean anonymous;

}
