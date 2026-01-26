package com.knowy.core.domain;

/**
 * Root domain record for an Exercise Option.
 * <p>
 * Represents a concrete, identified answer choice within an exercise, linking its persistence identity with its
 * business value.
 *
 * @param id      The unique identifier of the option.
 * @param value   The text content or value of the answer.
 * @param isValid Indicates if this is a correct answer for the exercise.
 */
public record Option(
	int id,
	String value,
	boolean isValid
) implements OptionIdentifier, OptionUnidentifiedData {
	public Option(int id, OptionUnidentifiedData optionData) {
		this(id, optionData.value(), optionData.isValid());
	}
}
