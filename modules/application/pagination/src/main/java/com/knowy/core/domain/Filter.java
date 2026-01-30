package com.knowy.core.domain;

import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Represents a filtering criterion used for querying or data processing. * @param fieldName the name of the attribute
 * or field to filter on.
 *
 * @param operator the comparison operator to be applied.
 * @param value    the value to compare against the field.
 */
public record Filter(
	String fieldName,
	Operator operator,
	Object value
) {

	/**
	 * Compact constructor for Filter. Validates that the provided value is compatible with the specified operator.
	 *
	 * @throws KnowyIllegalArgumentRuntimeException if the value fails the operator's validation.
	 */
	public Filter {
		operator.validate(value);
	}

	/**
	 * Enumeration of supported filtering operators. Each operator includes a validation predicate to ensure data
	 * integrity.
	 */
	public enum Operator {
		IN(Collection.class::isInstance),
		EQUALS(java.util.Objects::nonNull);

		private final Predicate<Object> predicate;

		Operator(Predicate<Object> predicate) {
			this.predicate = predicate;
		}

		/**
		 * Validates the given value against the operator's rules.
		 *
		 * @param value the object to validate.
		 * @throws KnowyIllegalArgumentRuntimeException if the validation predicate returns false.
		 */
		public void validate(Object value) {
			if (!predicate.test(value)) {
				throw new KnowyIllegalArgumentRuntimeException("Invalid value for operator " + this.name());
			}
		}
	}
}