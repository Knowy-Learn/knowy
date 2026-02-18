package com.knowy.core.util;

import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;

import java.util.Collection;

/**
 * Common utility methods for collection handling.
 */
public class CommonUtils {

	private CommonUtils() {
	}

	/**
	 * Returns the default value if the provided collection is empty.
	 *
	 * @param collection   the collection to check
	 * @param defaultValue the fallback collection
	 * @return the original collection if not empty, otherwise defaultValue
	 */
	public static <T extends Collection<?>> T nonEmptyElse(T collection, T defaultValue) {
		return collection.isEmpty() ? defaultValue : collection;
	}

	/**
	 * Validates that a collection is neither null nor empty.
	 *
	 * @param collection the collection to validate
	 * @return the collection if valid
	 * @throws KnowyIllegalArgumentRuntimeException if the collection is null or empty
	 */
	public static <T extends Collection<?>> T nonEmpty(T collection) {
		if (collection == null || collection.isEmpty()) {
			throw new KnowyIllegalArgumentRuntimeException("Required collection is null or empty");
		}
		return collection;
	}
}