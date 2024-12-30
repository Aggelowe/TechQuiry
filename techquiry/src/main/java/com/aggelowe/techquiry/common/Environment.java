package com.aggelowe.techquiry.common;

import static com.aggelowe.techquiry.common.Constants.EXECUTION_DIRECTORY;

import java.io.File;

import com.aggelowe.techquiry.common.exception.IllegalConstructionException;

import lombok.extern.log4j.Log4j2;

/**
 * The {@link Environment} class is the one responsible for providing the
 * environment variables used for the configuration of the TechQuiry
 * application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Log4j2
public final class Environment {

	/**
	 * The application's port.
	 */
	public static final int PORT = getVariable("TQ_PORT", 9850, Integer::valueOf, value -> {
		return value > 0 && value <= 65535;
	});

	/**
	 * The work directory of the application.
	 */
	public static final File WORK_DIRECTORY = getVariable("TQ_PATH", new File(EXECUTION_DIRECTORY), original -> new File(original), value -> {
		return value.exists() && value.isDirectory();
	});

	/**
	 * The whether to perform the initial setup.
	 */
	public static final boolean SETUP = getVariable("TQ_SETUP", false, Boolean::parseBoolean);

	/**
	 * The salt length for hashing the application users' passwords.
	 */
	public static final int SALT_LENGTH = getVariable("TQ_SALT_SIZE", 16, Integer::valueOf, value -> {
		return value > 0 && value <= 64;
	});

	/**
	 * The maximum time the application will wait for a connection from the database
	 * connection pool.
	 */
	public static final long CONNECTION_TIMEOUT = getVariable("TQ_CONNECTION_TIMEOUT", 30000L, Long::valueOf, value -> {
		return value >= 250L;
	});

	/**
	 * The maximum time a database connection will stay idle in the database
	 * connection pool.
	 */
	public static final long CONNECTION_IDLE_TIMEOUT = getVariable("TQ_CONNECTION_IDLE_TIMEOUT", 600000L, Long::valueOf, value -> {
		return value == 0L || value >= 10000L;
	});

	/**
	 * The maximum lifetime of a database connection in the database connection
	 * pool.
	 */
	public static final long CONNECTION_MAX_LIFETIME = getVariable("TQ_CONNECTION_MAX_LIFETIME", 1800000L, Long::valueOf, value -> {
		return value >= 30000L;
	});

	/**
	 * The maximum size of the pool of connections to the database.
	 */
	public static final int CONNECTION_POOL_SIZE = getVariable("TQ_CONNECTION_POOL_SIZE", 10, Integer::valueOf, value -> {
		return value > 0;
	});

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
		 * @throws Exception If an error occurs during the conversion
		 */
		Output convert(String original) throws Exception;

	}

	/**
	 * The {@link ILimit} functional interface is used to define the boundaries for
	 * whether the provided value is valid.
	 * 
	 * @param <Output> The checked value type
	 */
	private interface ILimit<Output> {

		/**
		 * This method checks whether the given value of type {@link Output} falls
		 * within the constraints of the application.
		 * 
		 * @param value The value to check
		 * @return Whether the value is valid
		 */
		boolean check(Output value);

	}

	/**
	 * This constructor will throw an {@link IllegalConstructionException} whenever
	 * invoked. {@link Environment} objects should <b>not</b> be constructible.
	 * 
	 * @throws IllegalConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private Environment() throws IllegalConstructionException {
		throw new IllegalConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method returns value obtained from the environment variables using the
	 * given key. If the variable found, it is converted to the {@link Output} type
	 * using the given converter. If the conversion fails, the application will
	 * terminate. If the converted value falls outside the predefined constraints,
	 * the application will terminate. If the variable is not found, the given
	 * fallback value is used instead.
	 * 
	 * @param key       The key of the environment variable
	 * @param fallback  The value to use if the key is not found in the environment
	 * @param converter The {@link IConverter} that defines how to convert the value
	 *                  from a {@link String}
	 * @param limit     The constraints for the value of the variable
	 * @return The value of the environment variable
	 */
	private static <Output> Output getVariable(String key, Output fallback, IConverter<Output> converter, ILimit<Output> limit) {
		final String original = System.getenv(key);
		Output value = fallback;
		if (original != null) {
			try {
				value = converter.convert(original);
			} catch (Exception exception) {
				log.fatal("An exception was thrown while converting " + key + "!", exception);
				System.exit(1);
			}
		}
		if (limit != null && !limit.check(value)) {
			log.fatal("The value of " + key + " is outside the defined contraints!");
			System.exit(1);
		}
		return value;
	}

	/**
	 * This method returns value obtained from the environment variables using the
	 * given key. If the variable found, it is converted to the {@link Output} type
	 * using the given converter. If the conversion fails, the application will
	 * terminate. If the variable is not found, the given fallback value is used
	 * instead.
	 * 
	 * @param key       The key of the environment variable
	 * @param fallback  The value to use if the key is not found in the environment
	 * @param converter The {@link IConverter} that defines how to convert the value
	 *                  from a {@link String}
	 * @return The value of the environment variable
	 */
	private static <Output> Output getVariable(String key, Output fallback, IConverter<Output> converter) {
		return getVariable(key, fallback, converter, null);
	}

}
