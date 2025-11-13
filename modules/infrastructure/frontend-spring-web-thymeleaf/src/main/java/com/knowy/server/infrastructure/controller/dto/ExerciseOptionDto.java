package com.knowy.server.infrastructure.controller.dto;

import com.knowy.core.domain.Option;

public record ExerciseOptionDto(
	int id,
	String text,
	AnswerStatus status
) {

	public static ExerciseOptionDto fromDomain(Option option) {
		return new ExerciseOptionDto(option.id(), option.value(), AnswerStatus.NO_RESPONSE);
	}

	public static ExerciseOptionDto fromDomain(Option option, int answerId) {
		return new ExerciseOptionDto(option.id(), option.value(), AnswerStatus.from(option, answerId));
	}

	public enum AnswerStatus {
		NO_RESPONSE,
		RESPONSE_FAIL,
		RESPONSE_SUCCESS;

		public static AnswerStatus from(Option option, int answerId) {
			if (option.id() == answerId && !option.isValid()) {
				return RESPONSE_FAIL;
			}
			if (option.isValid()) {
				return RESPONSE_SUCCESS;
			}
			return NO_RESPONSE;
		}
	}
}
