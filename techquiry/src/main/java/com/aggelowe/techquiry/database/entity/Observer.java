package com.aggelowe.techquiry.database.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The {@link Observer} class represents an observer entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Observer {

	/**
	 * The inquiry id of the inquiry
	 */
	private final int inquiryId;

	/**
	 * The user id of the observer
	 */
	private final int userId;

}
