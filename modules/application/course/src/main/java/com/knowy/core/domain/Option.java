package com.knowy.core.domain;

public record Option(
	int id,
	String value,
	boolean isValid
) implements OptionIdentifier, OptionData {
	public Option(int id, OptionData optionData) {
		this(id, optionData.value(), optionData.isValid());
	}
}
