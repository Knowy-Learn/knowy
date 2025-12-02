package com.knowy.server.api.controller.exception;

import com.knowy.server.api.dto.KnowyErrorReportDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalErrorHandlerController {
	private static final Logger logger = LoggerFactory.getLogger(GlobalErrorHandlerController.class);

	/**
	 * Handles Bean Validation constraint violations.
	 *
	 * @param ex      the ConstraintViolationException thrown
	 * @param request the current HTTP request
	 * @return a ResponseEntity containing the error report with HTTP 400
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<KnowyErrorReportDto> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
		UUID errorCode = UUID.randomUUID();

		Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
		String message = violations.stream()
			.map(this::formatViolationMessage)
			.collect(Collectors.joining("; "));

		logger.warn("Validation Error Code: {}\nRequest: {}\nViolations: {}", errorCode, request.getRequestURI(), message, ex);

		return ResponseEntity.badRequest()
			.body(new KnowyErrorReportDto(errorCode, message));
	}

	private String formatViolationMessage(ConstraintViolation<?> violation) {
		return extractFieldName(violation) + ": " + violation.getMessage();
	}

	private String extractFieldName(ConstraintViolation<?> violation) {
		String fullPathName = violation.getPropertyPath().toString();
		if (fullPathName.contains(".")) {
			return fullPathName.substring(fullPathName.lastIndexOf('.') + 1);
		}
		return fullPathName;
	}

	/**
	 * Handles KnowyBadRequestRuntimeException and returns a 400 Bad Request response with error details.
	 *
	 * @param ex      the thrown exception
	 * @param request the HTTP request that caused the exception
	 * @return ResponseEntity with the exception UUID and message
	 */
	@ExceptionHandler(KnowyBadRequestRuntimeException.class)
	public ResponseEntity<KnowyErrorReportDto> handleBadRequestRuntimeException(
		KnowyBadRequestRuntimeException ex,
		HttpServletRequest request
	) {
		logger.warn("Bad Request Error Code: {}\nRequest: {}\nMessage: {}",
			ex.getExceptionUUID(), request.getRequestURI(), ex.getMessage(), ex
		);

		return ResponseEntity.badRequest()
			.body(new KnowyErrorReportDto(ex.getExceptionUUID(), ex.getMessage()));
	}

	/**
	 * Handles KnowyConflictRuntimeException and returns a 409 Conflict
	 * response with error details.
	 *
	 * @param ex      the thrown exception
	 * @param request the HTTP request that caused the exception
	 * @return ResponseEntity with the exception UUID and message
	 */
	@ExceptionHandler(KnowyConflictRuntimeException.class)
	public ResponseEntity<KnowyErrorReportDto> handleConflictRuntimeException(KnowyConflictRuntimeException ex, HttpServletRequest request) {
		logger.warn("Conflict Error Code: {}\nRequest: {}\nMessage: {}"
			, ex.getExceptionUUID(), request.getRequestURI(), ex.getMessage(), ex
		);

		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new KnowyErrorReportDto(ex.getExceptionUUID(), ex.getMessage()));
	}

	/**
	 * Handles KnowyUnauthorizedException and returns a 401 Unauthorized
	 * response with error details.
	 *
	 * @param ex      the thrown exception
	 * @param request the HTTP request that caused the exception
	 * @return ResponseEntity with the exception UUID and message
	 */
	@ExceptionHandler(KnowyUnauthorizedException.class)
	public ResponseEntity<KnowyErrorReportDto> handleUnauthorizedRuntimeException(
		KnowyUnauthorizedException ex,
		HttpServletRequest request
	) {
		logger.warn("Unauthorized Error Code: {}\nRequest: {}\nMessage: {}"
			, ex.getExceptionUUID(), request.getRequestURI(), ex.getMessage(), ex
		);

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(new KnowyErrorReportDto(ex.getExceptionUUID(), ex.getMessage()));
	}

	/**
	 * Handles all uncaught exceptions as internal server errors.
	 *
	 * @param ex      the Exception thrown
	 * @param request the current HTTP request
	 * @return a ResponseEntity containing the error report with HTTP 500
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<KnowyErrorReportDto> handleServerError(Exception ex, HttpServletRequest request) {
		UUID errorCode = UUID.randomUUID();

		logger.error("Error Code: {}\nRequest: {}\nException: ", errorCode, request.getRequestURI(), ex);

		KnowyErrorReportDto errorReport = new KnowyErrorReportDto(
			errorCode,
			"Internal Server Error. Please send this error code to support."
		);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorReport);
	}
}