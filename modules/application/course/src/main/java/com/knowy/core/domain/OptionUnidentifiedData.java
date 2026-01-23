package com.knowy.core.domain;

import java.util.Objects;

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
