package net.techquiry.app.common;

import java.io.File;
import java.util.function.Function;
import java.util.function.Predicate;

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
	public static final int SRV_PORT = env(Integer.class, "TQ_BE_SRV_PORT", 9850, Integer::parseInt, num -> num >= 0 && num < 65536);

	/**
	 * The directory where the application's work files are located.
	 */
	public static final File SRV_WORKING_DIRECTORY = env(File.class, "TQ_BE_SRV_WORKING_DIRECTORY", new File(System.getProperty("user.dir")), File::new,
			File::isDirectory);

	/**
	 * Whether to enable the API documentation.
	 */
	public static final boolean DOC_API = env(Boolean.class, "TQ_BE_DOC_API", false, Boolean::parseBoolean);

	/**
	 * The size of the salt used for hashing the users' passwords.
	 */
	public static final int SEC_SALT_SIZE = env(Integer.class, "TQ_BE_SEC_SALT_SIZE", 16, Integer::parseInt, num -> num >= 0);

	/**
	 * The maximum size of the users' usernames.
	 */
	public static final int SEC_USERNAME_MAX_SIZE = env(Integer.class, "TQ_BE_SEC_USERNAME_MAX_SIZE", 15, Integer::parseInt, num -> num >= 3);

	/**
	 * Whether to setup the database's schema on the application's start.
	 */
	public static final boolean DB_CREATE_SCHEMA = env(Boolean.class, "TQ_BE_DB_CREATE_SCHEMA", false, Boolean::parseBoolean);

	/**
	 * The maximum time the application will wait for a connection from the database
	 * connection pool.
	 */
	public static final long DB_TIMEOUT = env(Long.class, "TQ_BE_DB_TIMEOUT", 30000L, Long::parseLong, num -> num >= 250L);

	/**
	 * The maximum time a database connection will stay idle in the database
	 * connection pool.
	 */
	public static final long DB_IDLE_TIMEOUT = env(Long.class, "TQ_BE_DB_IDLE_TIMEOUT", 600000L, Long::parseLong, num -> num == 0L || num >= 10000L);

	/**
	 * The maximum lifetime of a database connection in the database connection
	 * pool.
	 */
	public static final long DB_LIFETIME = env(Long.class, "TQ_BE_DB_LIFETIME", 1800000L, Long::parseLong, num -> num >= 30000L);

	/**
	 * The maximum size of the pool of connections to the database.
	 */
	public static final int DB_POOL_SIZE = env(Integer.class, "TQ_BE_DB_POOL_SIZE", 10, Integer::parseInt, num -> num > 0);

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
	 * @param converter  The {@link Function} that defines how to convert the value
	 *                   from a {@link String}
	 * @param constraint The constraints for the value of the variable
	 * @return The value of the environment variable
	 */
	private static <T> T env(Class<T> clazz, String key, T fallback, Function<String, ? extends T> converter, Predicate<? super T> constraint) {
		final String original = System.getenv(key);
		T value = fallback;
		if (original != null) {
			try {
				value = converter.apply(original);
			} catch (RuntimeException exception) {
				log.fatal("An exception was thrown while converting %s!".formatted(key), exception);
				System.exit(1);
			}
		}
		if (constraint != null && !constraint.test(value)) {
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
	 * @param converter The {@link Function} that defines how to convert the value
	 *                  from a {@link String}
	 * @return The value of the environment variable
	 */
	private static <T> T env(Class<T> clazz, String key, T fallback, Function<String, ? extends T> converter) {
		return env(clazz, key, fallback, converter, null);
	}

}
