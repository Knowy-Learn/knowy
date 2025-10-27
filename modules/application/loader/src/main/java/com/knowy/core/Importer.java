package com.knowy.core;

import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
	T execute(InputStream inputStream, URL schema) throws KnowyInconsistentDataException, IOException, KnowyValidationException;

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