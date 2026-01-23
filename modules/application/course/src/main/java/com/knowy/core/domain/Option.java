package com.knowy.core.domain;

public record Option(
	int id,
	String value,
	boolean isValid
) implements OptionIdentifier, OptionUnidentifiedData {
	public Option(int id, OptionUnidentifiedData optionData) {
		this(id, optionData.value(), optionData.isValid());
	}
}
