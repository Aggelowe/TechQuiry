package net.techquiry.app.mapper.exception;

/**
 * {@link MissingValueException} is a {@link MapperException} subclass that is
 * thrown when a value required by a mapper is missing from a DTO.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class MissingValueException extends MapperException {

	/**
	 * Constructs a new {@link MissingValueException} with the given message.
	 *
	 * @param message The details message of the exception.
	 */
	public MissingValueException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link MissingValueException} with the given message and
	 * cause.
	 *
	 * @param message The details message of the exception.
	 * @param cause   The <i>cause</i> of the exception.
	 */
	public MissingValueException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
