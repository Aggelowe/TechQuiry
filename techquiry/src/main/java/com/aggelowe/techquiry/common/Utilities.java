package com.aggelowe.techquiry.common;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import com.aggelowe.techquiry.common.exceptions.ConstructorException;

/**
 * The {@link Utilities} contains several utility methods that are important for
 * the functionality of the TechQUiry application.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public final class Utilities {

	/**
	 * This constructor will throw an {@link ConstructorException} whenever
	 * invoked. {@link Utilities} objects should <b>not</b> be constructible.
	 * 
	 * @throws ConstructorException Will always be thrown when the
	 *                                      constructor is invoked.
	 */
	private Utilities() {
		throw new ConstructorException(getClass().getName() + " objects should not be constructed!");
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
		String encoded = new String(raw);
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
		byte[] raw = encoded.getBytes();
		byte[] decoded = decoder.decode(raw);
		return decoded;
	}

}
