package com.aggelowe.techquiry.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.aggelowe.techquiry.mapper.exception.MapperException;
import com.aggelowe.techquiry.mapper.exception.MissingValueException;
import com.aggelowe.techquiry.service.exception.EntityNotFoundException;
import com.aggelowe.techquiry.service.exception.ForbiddenOperationException;
import com.aggelowe.techquiry.service.exception.InvalidRequestException;
import com.aggelowe.techquiry.service.exception.ServiceException;

import lombok.extern.log4j.Log4j2;

/**
 * This {@link ErrorHelper} class is responsible for handling all the exceptions
 * that are thrown by the controller methods.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@ControllerAdvice
@Log4j2
public class ErrorHelper {

	/**
	 * This method is invoked when a controller method throws an exception for a
	 * forbidden operation.
	 * 
	 * @param exception The causing exception
	 * @return The constructed error response
	 */
	@ExceptionHandler(ForbiddenOperationException.class)
	public ResponseEntity<ErrorResponse> resolveForbidden(Exception exception) {
		return resolveException(exception, HttpStatus.FORBIDDEN);
	}

	/**
	 * This method is invoked when a controller method throws an exception for a
	 * requested entity that was not found.
	 * 
	 * @param exception The causing exception
	 * @return The constructed error response
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> resolveNotFound(Exception exception) {
		return resolveException(exception, HttpStatus.NOT_FOUND);
	}

	/**
	 * This method is invoked when a controller method throws an exception for an
	 * invalid received request.
	 * 
	 * @param exception The causing exception
	 * @return The constructed error response
	 */
	@ExceptionHandler({ InvalidRequestException.class, MissingValueException.class })
	public ResponseEntity<ErrorResponse> resolveBadRequest(Exception exception) {
		return resolveException(exception, HttpStatus.BAD_REQUEST);
	}

	/**
	 * This method is invoked when a controller method throws an exception for an
	 * internal server error that occured.
	 * 
	 * @param exception The causing exception
	 * @return The constructed error response
	 */
	@ExceptionHandler({ InternalServerError.class, ServiceException.class, MapperException.class })
	public ResponseEntity<ErrorResponse> resolveInternalServerError(Exception exception) {
		return resolveException(exception, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * This method returns a response containing the message of the given exception
	 * and the given status.
	 * 
	 * @param exception The exception to resolve
	 * @param status    The status of the error
	 * @return The constructed error response
	 */
	private ResponseEntity<ErrorResponse> resolveException(Exception exception, HttpStatus status) {
		ErrorResponse response = new ErrorResponse(status.value(), exception.getMessage());
		log.warn("Resolved " + status.value() + ": " + response);
		return ResponseEntity.status(status).body(response);
	}

}
