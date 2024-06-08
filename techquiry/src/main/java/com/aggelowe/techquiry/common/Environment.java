package com.aggelowe.techquiry.common;

import java.util.Map;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;

/**
 * The {@link Environment} class is the one responsible for providing the
 * environment variables used for the configuration of the TechQuiry
 * application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class Environment {

	/**
	 * The {@link Map} object containing the system environment variables.
	 */
	private static final Map<String, String> ENVIRONMENT_VARIABLES;

	/**
	 * The variable key of the application's port
	 */
	private static final String PORT_KEY = "TQ_PORT";

	/**
	 * The default value of the application's port
	 */
	private static final String PORT_DEFAULT = "9850";

	/**
	 * This constructor will throw an {@link ConstructorException} whenever invoked.
	 * {@link Environment} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private Environment() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method returns the port assigned to the Spring application by the
	 * environment.
	 * 
	 * @return The application's network port
	 */
	public static String getPort() {
		return ENVIRONMENT_VARIABLES.getOrDefault(PORT_KEY, PORT_DEFAULT);
	}

	static {
		ENVIRONMENT_VARIABLES = System.getenv();
	}

}
