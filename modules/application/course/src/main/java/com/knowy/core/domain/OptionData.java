package com.knowy.core.domain;

import java.util.Objects;

public interface OptionData {

	String value();

	boolean isValid();

	record InmutableOptionData(String value, boolean isValid) implements OptionData {
		public InmutableOptionData(String value, boolean isValid) {
			this.value = Objects.requireNonNull(value, "value cannot be null");
			this.isValid = isValid;
		}
	}
}
