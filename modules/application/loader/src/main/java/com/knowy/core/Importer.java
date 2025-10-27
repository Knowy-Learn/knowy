package com.knowy.core;

import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Defines a contract for importing data from an {@link InputStream}, optionally using a schema for validation.
 *
 * @param <T> the type of object resulting from the import process
 */
public interface Importer<T> {

	/**
	 * Executes the import process using the given input stream and schema.
	 *
	 * @param inputStream the input stream containing the data to be imported
	 * @param schema      the URL of the schema used for validation
	 * @return the imported object of type {@code T}
	 * @throws KnowyImporterParseException    if the input cannot be parsed or validated
	 * @throws KnowyInconsistentDataException if the data is inconsistent with expected rules
	 * @throws IOException                    if an I/O error occurs while reading the input stream
	 * @throws KnowyValidationException       if the data fails validation against the schema
	 */
	T execute(InputStream inputStream, URL schema)
		throws KnowyInconsistentDataException, IOException, KnowyValidationException;

	/**
	 * Exception thrown when an error occurs while parsing or validating input data.
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
