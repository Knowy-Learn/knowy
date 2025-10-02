package com.knowy.core.domain;

import java.util.Set;

public interface LessonData<D extends DocumentationData, E extends ExerciseData<? extends OptionData>> extends LessonMinData {

	Set<D> documentations();

	Set<E> exercises();

	record InmutableLessonData(
		String title,
		String explanation,
		Set<DocumentationData> documentations,
		Set<ExerciseData<OptionData>> exercises
	) implements LessonData<DocumentationData, ExerciseData<OptionData>> {
	}
}
