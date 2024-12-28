package com.aggelowe.techquiry.service.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The {@link Authentication} class acts as a container for user login
 * information used for controlling and verifying the user session.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
public class Authentication {

	/**
	 * The unique user id of the authenticated user
	 */
	private final int userId;

}
