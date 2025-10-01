package com.knowy.core.domain;

import java.util.Set;

public interface LessonData extends LessonMinData {

	Set<Documentation> documentations();

	Set<Exercise> exercises();

	record InmutableLessonData(
		String title,
		String explanation,
		Set<Documentation> documentations,
		Set<Exercise> exercises
	) implements LessonData {

	}
}
