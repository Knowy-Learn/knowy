package com.knowy.core;

/**
 * Utility class for performing validations during the import process.
 */
public class ImporterValidation {

	private ImporterValidation() {
		// Private constructor to prevent instantiation
	}

	/**
	 * Ensures that the given object is not null.
	 *
	 * @param obj  the object to check
	 * @param name the name of the object (used in the exception message)
	 * @param <T>  the type of the object
	 * @return the validated object if it is not null
	 * @throws Importer.KnowyImporterParseException if the object is null
	 */
	public static <T> T requireNonNullChecked(T obj, String name) throws Importer.KnowyImporterParseException {
		if (obj == null) {
			throw new Importer.KnowyImporterParseException(name + " cannot be null");
		}
		return obj;
	}
}
