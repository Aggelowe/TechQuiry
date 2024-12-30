package com.aggelowe.techquiry.common;

import static com.aggelowe.techquiry.common.Constants.HASHING_ALGORITHM;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import com.aggelowe.techquiry.common.exception.IllegalConstructionException;

import lombok.extern.log4j.Log4j2;

/**
 * The {@link SecurityUtils} contains several security methods that are
 * important for the functionality of the TechQuiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Log4j2
public final class SecurityUtils {

	/**
	 * This constructor will throw an {@link IllegalConstructionException} whenever
	 * invoked. {@link SecurityUtils} objects should <b>not</b> be constructible.
	 * 
	 * @throws IllegalConstructionException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private SecurityUtils() throws IllegalConstructionException {
		throw new IllegalConstructionException(getClass().getName() + " objects should not be constructed!");
	}

	/**
	 * This method is responsible for encoding a byte array to a string using the
	 * Base64 format.
	 * 
	 * @param source The source byte array
	 * @return The encoded string
	 */
	public static String encodeBase64(byte[] source) {
		Encoder encoder = Base64.getEncoder();
		byte[] raw = encoder.encode(source);
		String encoded = new String(raw, StandardCharsets.UTF_8);
		return encoded;
	}

	/**
	 * This method is responsible for decoding a Base64 encoded string to a raw byte
	 * array.
	 * 
	 * @param encoded The encoded string
	 * @return The decoded byte array
	 * @throws IllegalArgumentException If the string is not in the Base64 scheme
	 */
	public static byte[] decodeBase64(String encoded) {
		Decoder decoder = Base64.getDecoder();
		byte[] raw = encoded.getBytes(StandardCharsets.UTF_8);
		byte[] decoded = decoder.decode(raw);
		return decoded;
	}

	/**
	 * This method generates a secure random salt of the length specified by the
	 * respective environment variable.
	 * 
	 * @return The bytes containing the generated salt
	 */
	public static byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		int length = Environment.getSaltLength();
		byte[] salt = new byte[length];
		random.nextBytes(salt);
		return salt;
	}

	/**
	 * This method verifies the given password against the given hash using the
	 * algorithm defined in {@link Constants} and the given salt.
	 * 
	 * @param password The plain password to verify
	 * @param salt     The salt used in the hashing
	 * @param hash     The hash to compare the password against
	 * @return Whether the hashed password compares the given hash
	 */
	public static boolean verifyPassword(String password, byte[] salt, byte[] hash) {
		byte[] attempt = hashPassword(password, salt);
		return MessageDigest.isEqual(hash, attempt);
	}

	/**
	 * This method hashes the given password using the algorithm defined in
	 * {@link Constants} and the given salt.
	 * 
	 * @param password The password to hash
	 * @param salt     The salt to use in the hashing
	 * @return The hashed password bytes
	 */
	public static byte[] hashPassword(String password, byte[] salt) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance(HASHING_ALGORITHM);
		} catch (NoSuchAlgorithmException exception) {
			log.fatal(exception);
			System.exit(1);
		}
		byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
		digest.update(salt);
		return digest.digest(bytes);
	}

}
