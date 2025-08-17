package com.knowy.core.domain;

public record Option(
	int id,
	String optionText,
	boolean isCorrect
) {
}
