package com.knowy.server.api.controller;

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
public class ErrorHandlerController {
	private static final Logger logger = LoggerFactory.getLogger(ErrorHandlerController.class);

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

	// TODO: Rate Limit 429

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