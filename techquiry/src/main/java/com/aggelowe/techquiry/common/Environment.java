package com.aggelowe.techquiry.common;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;
import com.aggelowe.techquiry.common.exceptions.EnvironmentException;

import static com.aggelowe.techquiry.common.Constants.EXECUTION_DIRECTORY;

import java.io.File;

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
	 * The {@link Entry} containing the application's port.
	 */
	private static final Entry PORT = new Entry("TQ_PORT", "9850");

	/**
	 * The {@link Entry} containing the work directory of the application.
	 */
	private static final Entry WORK_DIRECTORY = new Entry("TQ_PATH", EXECUTION_DIRECTORY);

	/**
	 * The {@link Environment} class is responsible for loading, containing and
	 * returning the environment variable with the given key.
	 * 
	 * @author Aggelowe
	 * @since 0.0.1
	 */
	private static class Entry {

		/**
		 * The {@link String} value of the environment variable
		 */
		private final String value;

		/**
		 * This constructor constructs a new {@link Entry} object. The value is obtained
		 * from the environment variables using the given key, and if it is not found,
		 * the given fallback value is used instead.
		 * 
		 * @param key      The key of the environment variable
		 * @param fallback The value to use if the key is not found in the environment
		 */
		public Entry(String key, String fallback) {
			this.value = System.getenv().getOrDefault(key, fallback);
		}

		/**
		 * This method returns the value stored in the object.
		 * 
		 * @return The value stored
		 */
		public String get() {
			return value;
		}

	}

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
	public static int getPort() {
		String variable = PORT.get();
		int value;
		try {
			value = Integer.valueOf(variable);
		} catch (NumberFormatException exception) {
			throw new EnvironmentException("The given port is not an integer.", exception);
		}
		if (value <= 0 || value >= 65535) {
			throw new EnvironmentException("The given port is not within the valid port range.");
		}
		return value;
	}

	/**
	 * This method returns the work directory of the application as defined by the
	 * environment.
	 * 
	 * @return The application's work directory
	 */
	public static File getWorkDirectory() {
		String variable = WORK_DIRECTORY.get();
		File file = new File(variable);
		if (!file.isDirectory()) {
			throw new EnvironmentException("The given path is not a directory.");
		}
		return file;
	}

}
