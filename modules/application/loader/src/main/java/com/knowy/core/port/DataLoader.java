package com.knowy.core.port;

import com.knowy.core.exception.KnowyValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Interface for loading data from an input stream using a schema.
 */
public interface DataLoader {

	/**
	 * Loads data from the given input stream and validates it against the provided schema.
	 *
	 * @param inputStream the input stream containing the data to load
	 * @param schema the URL of the schema used for validation
	 * @return a map containing the loaded data as key-value pairs
	 * @throws KnowyValidationException if the data does not conform to the schema
	 * @throws IOException if an I/O error occurs while reading the input stream
	 */
	Map<String, Object> loadData(InputStream inputStream, URL schema)
		throws KnowyValidationException, IOException;
}