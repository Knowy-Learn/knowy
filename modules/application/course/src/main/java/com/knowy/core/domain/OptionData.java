package com.knowy.core.domain;

public interface OptionData {

	String value();

	boolean isValid();

	record InmutableOptionData(String value, boolean isValid) implements OptionData {
		InmutableOptionData(OptionData optionData) {
			this(optionData.value(), optionData.isValid());
		}
	}
}
