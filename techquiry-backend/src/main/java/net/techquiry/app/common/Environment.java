package net.techquiry.app.common;

import java.io.File;

import lombok.extern.log4j.Log4j2;
import net.techquiry.app.common.exception.IllegalConstructionException;

/**
 * The {@link Environment} class is responsible for providing the environment
 * variables for configuring different aspects of the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Log4j2
public final class Environment {

	/**
	 * The port that the server is running.
	 */
	public static final int SERVER_PORT = env(Integer.class, "TQ_SERVER_PORT", 9850, Integer::parseInt, v -> v >= 0 && v < 65536);

	/**
	 * The directory where the application's work files are located.
	 */
	public static final File SERVER_WORK_DIRECTORY = env(File.class, "TQ_SERVER_PATH", new File(System.getProperty("user.dir")), File::new, File::isDirectory);

	/**
	 * The length of the salt used for hashing the users' passwords.
	 */
	public static final int SECURITY_SALT_LENGTH = env(Integer.class, "TQ_SECURITY_SALT_SIZE", 16, Integer::parseInt, v -> v > 0 && v <= 64);

	/**
	 * Whether to setup the database's schema on the application's start.
	 */
	public static final boolean DATABASE_SETUP = env(Boolean.class, "TQ_DATABASE_SETUP", false, Boolean::parseBoolean);

	/**
	 * The maximum time the application will wait for a connection from the database
	 * connection pool.
	 */
	public static final long DATABASE_TIMEOUT = env(Long.class, "TQ_DATABASE_TIMEOUT", 30000L, Long::parseLong, v -> v >= 250L);

	/**
	 * The maximum time a database connection will stay idle in the database
	 * connection pool.
	 */
	public static final long DATABASE_IDLE_TIMEOUT = env(Long.class, "TQ_DATABASE_IDLE_TIMEOUT", 600000L, Long::parseLong, v -> v == 0L || v >= 10000L);

	/**
	 * The maximum lifetime of a database connection in the database connection
	 * pool.
	 */
	public static final long DATABASE_LIFETIME = env(Long.class, "TQ_DATABASE_LIFETIME", 1800000L, Long::parseLong, v -> v >= 30000L);

	/**
	 * The maximum size of the pool of connections to the database.
	 */
	public static final int DATABASE_POOL_SIZE = env(Integer.class, "TQ_DATABASE_POOL_SIZE", 10, Integer::parseInt, v -> v > 0);

	/**
	 * Whether to enable the Swagger API documentation tool.
	 */
	public static final boolean DOCS_SWAGGER = env(Boolean.class, "TQ_DOCS_SWAGGER", false, Boolean::parseBoolean);

	/**
	 * The {@link IConverter} functional interface is used to define how a string
	 * can be converted to the target type.
	 * 
	 * @param <T> The target type
	 */
	@FunctionalInterface
	private interface IConverter<T> {

		/**
		 * This method converts the {@link String} provided into an object of type
		 * {@link T}.
		 * 
		 * @param original The string to convert to the target type
		 * @return The converted value
		 * @throws Exception If an error occurs during the conversion
		 */
		T convert(String original);

	}

	/**
	 * The {@link IConstraint} functional interface is used to define the boundaries
	 * for whether the provided value is valid.
	 * 
	 * @param <T> The checked value type
	 */
	@FunctionalInterface
	private interface IConstraint<T> {

		/**
		 * This method checks whether the given value of type {@link T} falls within the
		 * constraints of the application.
		 * 
		 * @param value The value to check
		 * @return Whether the value is valid
		 */
		boolean check(T value);

	}

	/**
	 * This constructor will throw an {@link IllegalConstructionException} whenever
	 * invoked. {@link Environment} objects should <b>not</b> be constructible.
	 * 
	 * @throws IllegalConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private Environment() throws IllegalConstructionException {
		throw new IllegalConstructionException("Objects of type %s should not be constructed!".formatted(getClass().getName()));
	}

	/**
	 * This method returns value obtained from the environment variables using the
	 * given key. If the variable found, it is converted to the {@link T} type using
	 * the given converter. If the conversion fails, the application will terminate.
	 * If the converted value falls outside the predefined constraints, the
	 * application will terminate. If the variable is not found, the given fallback
	 * value is used instead.
	 * 
	 * @param clazz      The type of the variable
	 * @param key        The key of the environment variable
	 * @param fallback   The value to use if the key is not found in the environment
	 * @param converter  The {@link IConverter} that defines how to convert the
	 *                   value from a {@link String}
	 * @param constraint The constraints for the value of the variable
	 * @return The value of the environment variable
	 */
	private static <T> T env(Class<T> clazz, String key, T fallback, IConverter<? extends T> converter, IConstraint<? super T> constraint) {
		final String original = System.getenv(key);
		T value = fallback;
		if (original != null) {
			try {
				value = converter.convert(original);
			} catch (RuntimeException exception) {
				log.fatal("An exception was thrown while converting %s!".formatted(key), exception);
				System.exit(1);
			}
		}
		if (constraint != null && !constraint.check(value)) {
			log.fatal("The value of %s is outside the defined contraints!".formatted(key));
			System.exit(1);
		}
		return value;
	}

	/**
	 * This method returns value obtained from the environment variables using the
	 * given key. If the variable found, it is converted to the {@link T} type using
	 * the given converter. If the conversion fails, the application will terminate.
	 * If the variable is not found, the given fallback value is used instead.
	 * 
	 * @param clazz     The type of the variable
	 * @param key       The key of the environment variable
	 * @param fallback  The value to use if the key is not found in the environment
	 * @param converter The {@link IConverter} that defines how to convert the value
	 *                  from a {@link String}
	 * @return The value of the environment variable
	 */
	private static <T> T env(Class<T> clazz, String key, T fallback, IConverter<? extends T> converter) {
		return env(clazz, key, fallback, converter, null);
	}

}
