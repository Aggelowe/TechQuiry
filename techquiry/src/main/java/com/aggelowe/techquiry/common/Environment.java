package com.aggelowe.techquiry.common;

import static com.aggelowe.techquiry.common.Constants.EXECUTION_DIRECTORY;

import java.io.File;
import java.util.function.Function;

import com.aggelowe.techquiry.common.exceptions.IllegalConstructionException;
import com.aggelowe.techquiry.common.exceptions.InvalidEnvironmentVariableException;

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
	private static final Entry<Integer> PORT = new Entry<>("TQ_PORT", 9850, (orignal) -> {
		int value;
		try {
			value = Integer.valueOf(orignal);
		} catch (NumberFormatException exception) {
			throw new InvalidEnvironmentVariableException("The given port is not an integer.", exception);
		}
		if (value <= 0 || value >= 65535) {
			throw new InvalidEnvironmentVariableException("The given port is not within the valid port range.");
		}
		return value;
	});

	/**
	 * The {@link Entry} containing the work directory of the application.
	 */
	private static final Entry<File> WORK_DIRECTORY = new Entry<>("TQ_PATH", new File(EXECUTION_DIRECTORY), (original) -> {
		File file = new File(original);
		if (!file.exists()) {
			throw new InvalidEnvironmentVariableException("The given path does not exist.");
		}
		if (!file.isDirectory()) {
			throw new InvalidEnvironmentVariableException("The given path is not a directory.");
		}
		return file;
	});

	/**
	 * The {@link Entry} containing whether to perform the initial setup.
	 */
	private static final Entry<Boolean> SETUP = new Entry<>("TQ_SETUP", false, (original) -> Boolean.parseBoolean(original));

	/**
	 * The {@link Entry} class is responsible for loading, converting and storing
	 * the environment variable with the given key.
	 * 
	 * @param <Output> The type of the entry's value
	 * @author Aggelowe
	 * @since 0.0.1
	 */
	private static class Entry<Output> {

		/**
		 * The converted value of the environment variable
		 */
		private final Output value;

		/**
		 * This constructor constructs a new {@link Entry} object. The value is obtained
		 * from the environment variables using the given key. If the variable found, it
		 * is converted to the {@link Output} type using the given converter. If it is
		 * not found or the conversion fails, the given fallback value is used instead.
		 * 
		 * @param key       The key of the environment variable
		 * @param fallback  The value to use if the key is not found in the environment
		 * @param converter The {@link Function} that defines how to convert the value
		 *                  from a {@link String}
		 */
		public Entry(String key, Output fallback, IConverter<Output> converter) {
			final String original = System.getenv(key);
			Output value = fallback;
			if (original != null) {
				try {
					value = converter.convert(original);
				} catch (Exception exception) {
					Constants.LOGGER.error(exception);
				}
			}
			this.value = value;
		}

		/**
		 * This method returns the converted value of the environment variable.
		 * 
		 * @return The converted value
		 */
		public Output get() {
			return value;
		}

	}

	/**
	 * The {@link IConverter} functional interface is used to define how a string
	 * can be converted to the target type.
	 * 
	 * @param <Output> The target type
	 */
	@FunctionalInterface
	private interface IConverter<Output> {

		/**
		 * This method converts the {@link String} provided into an object of type
		 * {@link Output}.
		 * 
		 * @param original The string to convert to the target type
		 * @return The converted value
		 * @throws InvalidEnvironmentVariableException If an error occurs during the conversion
		 */
		Output convert(String original) throws InvalidEnvironmentVariableException;

	}

	/**
	 * This constructor will throw an {@link IllegalConstructionException} whenever invoked.
	 * {@link Environment} objects should <b>not</b> be constructible.
	 * 
	 * @throws IllegalConstructionException Will always be thrown when the constructor is
	 *                              invoked.
	 */
	private Environment() throws IllegalConstructionException {
		throw new IllegalConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method returns the port assigned to the Spring application by the
	 * environment.
	 * 
	 * @return The application's network port
	 */
	public static int getPort() {
		return PORT.get();
	}

	/**
	 * This method returns the work directory of the application as defined by the
	 * environment.
	 * 
	 * @return The application's work directory
	 */
	public static File getWorkDirectory() {
		return WORK_DIRECTORY.get();
	}

	/**
	 * This method returns whether to perform the initial setup as defined by the
	 * environment.
	 * 
	 * @return Whether to perform the initial setup
	 */
	public static boolean getSetup() {
		return SETUP.get();
	}

}
