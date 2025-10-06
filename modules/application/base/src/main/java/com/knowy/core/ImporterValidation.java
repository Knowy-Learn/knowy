package com.knowy.core;

public class ImporterValidation {

	private ImporterValidation() {
	}

	public static <T> T requireNonNullChecked(T obj, String name) throws Importer.KnowyImporterParseException {
		if (obj == null) {
			throw new Importer.KnowyImporterParseException(name + " cannot be null");
		}
		return obj;
	}
}
