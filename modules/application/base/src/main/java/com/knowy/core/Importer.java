package com.knowy.core;

import com.knowy.core.exception.KnowyValidationException;

import java.io.InputStream;

/**
 * Defines a contract for importing data from an {@link InputStream}.
 *
 * @param <T> the type of object resulting from the import process
 */
public interface Importer<T> {

	/**
	 * Executes the import process using the given input stream.
	 *
	 * @param inputStream the input stream containing the data to be imported
	 * @return the imported object of type {@code T}
	 * @throws KnowyImporterParseException if the input cannot be parsed or validated
	 */
	T execute(InputStream inputStream) throws KnowyImporterParseException;

	/**
	 * Exception thrown when an error occurs while parsing or validating.
	 */
	class KnowyImporterParseException extends KnowyValidationException {

		public KnowyImporterParseException(String message) {
			super(message);
		}

		public KnowyImporterParseException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}