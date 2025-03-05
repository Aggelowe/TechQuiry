package com.aggelowe.techquiry.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.aggelowe.techquiry.mapper.exception.MapperException;
import com.aggelowe.techquiry.mapper.exception.MissingValueException;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;
import com.aggelowe.techquiry.service.exception.UnauthorizedOperationException;

import lombok.extern.log4j.Log4j2;

/**
 * The {@link ErrorHelper} class contains methods on how to handle the
 * exceptions that are thrown by the controller methods.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@RestControllerAdvice
@Log4j2
public class ErrorHelper {

	/**
	 * Handles exceptions of type {@link UnauthorizedOperationException} when thrown
	 * by a controller by returning a {@link ResponseEntity} with the UNAUTHORIZED
	 * (401) status code and an {@link ErrorResponse} containing the exception
	 * message.
	 *
	 * @param exception The exception that was thrown
	 * @return The {@link ResponseEntity} containing the {@link ErrorResponse}
	 */
	@ExceptionHandler(UnauthorizedOperationException.class)
	public ResponseEntity<ErrorResponse> resolveUnauthorized(Exception exception) {
		return resolveException(exception, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handles exceptions of type {@link ForbiddenOperationException} when thrown by
	 * a controller by returning a {@link ResponseEntity} with the FORBIDDEN (403)
	 * status code and an {@link ErrorResponse} containing the exception message.
	 *
	 * @param exception The exception that was thrown
	 * @return The {@link ResponseEntity} containing the {@link ErrorResponse}
	 */
	@ExceptionHandler(ForbiddenOperationException.class)
	public ResponseEntity<ErrorResponse> resolveForbidden(Exception exception) {
		return resolveException(exception, HttpStatus.FORBIDDEN);
	}

	/**
	 * Handles exceptions of type {@link EntityNotFoundException} when thrown by a
	 * controller by returning a {@link ResponseEntity} with the NOT_FOUND (404)
	 * status code and an {@link ErrorResponse} containing the exception message.
	 *
	 * @param exception The exception that was thrown
	 * @return The {@link ResponseEntity} containing the {@link ErrorResponse}
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> resolveNotFound(Exception exception) {
		return resolveException(exception, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles exceptions of type {@link InvalidRequestException} and
	 * {@link MissingValueException} when thrown by a controller by returning a
	 * {@link ResponseEntity} with the BAD_REQUEST (400) status code and an
	 * {@link ErrorResponse} containing the exception message.
	 *
	 * @param exception The exception that was thrown
	 * @return The {@link ResponseEntity} containing the {@link ErrorResponse}
	 */
	@ExceptionHandler({ InvalidRequestException.class, MissingValueException.class })
	public ResponseEntity<ErrorResponse> resolveBadRequest(Exception exception) {
		return resolveException(exception, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles exceptions of type {@link InternalServerError},
	 * {@link ServiceException} and {@link MapperException} when thrown by a
	 * controller by returning a {@link ResponseEntity} with the
	 * INTERNAL_SERVER_ERROR (500) status code and an {@link ErrorResponse}
	 * containing the exception message.
	 *
	 * @param exception The exception that was thrown
	 * @return The {@link ResponseEntity} containing the {@link ErrorResponse}
	 */
	@ExceptionHandler({ InternalServerError.class, ServiceException.class, MapperException.class })
	public ResponseEntity<ErrorResponse> resolveInternalServerError(Exception exception) {
		return resolveException(exception, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Resolves a given exception by returning a new {@link ErrorResponse} with the
	 * specified {@link HttpStatus} and the exception's message.
	 *
	 * @param exception The exception that was thrown
	 * @param status    The {@link HttpStatus} to be used in the response
	 * @return The {@link ResponseEntity} containing the {@link ErrorResponse}
	 */
	private ResponseEntity<ErrorResponse> resolveException(Exception exception, HttpStatus status) {
		ErrorResponse response = new ErrorResponse(status.value(), exception.getMessage());
		log.warn("Resolved " + status.value() + ": " + response);
		return ResponseEntity.status(status).body(response);
	}

}
