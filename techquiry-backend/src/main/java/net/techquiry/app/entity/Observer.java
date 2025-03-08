package net.techquiry.app.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The {@link Observer} class represents an observer entry of the TechQuiry
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
public class Observer {

	/**
	 * The inquiry id of the inquiry
	 */
	@NonNull
	private Integer inquiryId;

	/**
	 * The user id of the observer
	 */
	@NonNull
	private Integer userId;

}
