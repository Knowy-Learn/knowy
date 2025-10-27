package com.knowy.core;

import com.knowy.core.util.ThrowableFunction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Utility class providing helper methods for data extraction and mapping during the import process.
 */
public class ImporterHelper {

	private ImporterHelper() {
		// Private constructor to prevent instantiation
	}

	/**
	 * Retrieves a required string value from a map by key.
	 *
	 * @param map the map containing the values
	 * @param key the key to retrieve
	 * @return the string value associated with the key
	 * @throws Importer.KnowyImporterParseException if the value is null or missing
	 */
	public static String getRequiredString(Map<String, Object> map, String key) throws Importer.KnowyImporterParseException {
		String value = (String) map.get(key);
		ImporterValidation.requireNonNullChecked(value, key);
		return value;
	}

	/**
	 * Maps elements from an iterable into a collection using a mapper function.
	 *
	 * @param elements    the elements to map
	 * @param mapper      the mapping function that may throw an exception
	 * @param accumulator a supplier providing the target collection
	 * @param <T>         the input element type
	 * @param <R>         the result element type
	 * @param <A>         the type of the resulting collection
	 * @return the mapped collection
	 * @throws Importer.KnowyImporterParseException if mapping fails
	 */
	public static <T, R, A extends Collection<R>> A mapping(
		Iterable<T> elements, ThrowableFunction<T, R> mapper, Supplier<A> accumulator
	) throws Importer.KnowyImporterParseException {
		try {
			A result = accumulator.get();
			for (T element : elements) {
				result.add(mapper.apply(element));
			}
			return result;
		} catch (Importer.KnowyImporterParseException e) {
			throw e;
		} catch (Exception e) {
			throw new Importer.KnowyImporterParseException("Error parsing elements", e);
		}
	}

	/**
	 * Ensures an object is treated as a list of maps.
	 *
	 * @param obj  the object to convert
	 * @param name the property name for error messages
	 * @return a list of maps
	 * @throws Importer.KnowyImporterParseException if the object is null
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static List<Map<String, Object>> ensureList(Object obj, String name) throws Importer.KnowyImporterParseException {
		return switch (obj) {
			case null -> throw new Importer.KnowyImporterParseException(name + " cannot be null");
			case List ignored -> (List<Map<String, Object>>) obj;
			case Map ignored -> List.of((Map<String, Object>) obj);
			default -> List.of();
		};
	}

	/**
	 * Creates a {@link PropertyExtractor} for the given container map.
	 *
	 * @param container the map containing properties
	 * @return a {@link PropertyExtractor} instance
	 */
	public static PropertyExtractor extractorFor(Map<String, Object> container) {
		return new PropertyExtractor(container);
	}

	/**
	 * Retrieves a value from a container as a list of maps.
	 *
	 * @param container the container map
	 * @param key       the key to look up
	 * @return the value as a list of maps
	 * @throws Importer.KnowyImporterParseException if the value is null or cannot be converted
	 */
	public static List<Map<String, Object>> getAsList(Map<String, Object> container, String key) throws Importer.KnowyImporterParseException {
		return ensureList(container.get(key), key);
	}


	/**
	 * Helper class for extracting and mapping properties from a container map.
	 */
	public static class PropertyExtractor {
		private final Map<String, Object> container;

		private PropertyExtractor(Map<String, Object> container) {
			this.container = container;
		}

		/**
		 * Extracts a property from the container and maps its elements into a collection.
		 *
		 * @param key         the property key
		 * @param mapper      function to map each element
		 * @param accumulator supplier providing the target collection
		 * @param <R>         the type of mapped elements
		 * @param <A>         the type of the resulting collection
		 * @return a collection of mapped elements
		 * @throws Importer.KnowyImporterParseException if extraction or mapping fails
		 */
		public <R, A extends Collection<R>> A extract(
			String key, ThrowableFunction<Map<String, Object>, R> mapper, Supplier<A> accumulator
		) throws Importer.KnowyImporterParseException {
			List<Map<String, Object>> propertyElements = getAsList(container, key);
			return mapping(propertyElements, mapper, accumulator);
		}
	}

}
