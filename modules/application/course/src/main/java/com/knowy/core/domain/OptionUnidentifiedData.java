package com.knowy.core.domain;

import java.util.Objects;

/**
 * Content Contract for Exercise Options (Identity-less).
 * <p>
 * Defines the essential state of an answer choice. This interface is used primarily for creating new options before
 * they are assigned a persistent ID.
 */
public interface OptionUnidentifiedData {

	String value();

	boolean isValid();

	record InmutableOptionUnidentifiedData(String value, boolean isValid) implements OptionUnidentifiedData {
		public InmutableOptionUnidentifiedData(String value, boolean isValid) {
			this.value = Objects.requireNonNull(value, "value cannot be null");
			this.isValid = isValid;
		}
	}
}
