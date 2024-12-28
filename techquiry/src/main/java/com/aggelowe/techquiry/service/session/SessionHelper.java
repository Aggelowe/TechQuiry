package com.aggelowe.techquiry.service.session;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * The {@link SessionHelper} class is a session-scoped component and is
 * responsible for holding the user data for the current user session.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
@Setter
@Getter
public class SessionHelper {

	/**
	 * The {@link Authentication} object holding the authentication information for
	 * the current user session.
	 */
	private Authentication authentication;

}
