package com.knowy.core.domain;

import java.util.Objects;
import java.util.Set;

public interface LessonData<D extends DocumentationData, E extends ExerciseData<? extends OptionData>> extends LessonMinData {

	Set<D> documentations();

	Set<E> exercises();

	record InmutableLessonData(
		String title,
		String explanation,
		Set<DocumentationData> documentations,
		Set<ExerciseUnidentifiedData> exercises
	) implements LessonUnidentifiedData {
		public InmutableLessonData(String title, String explanation, Set<DocumentationData> documentations, Set<ExerciseUnidentifiedData> exercises) {
			this.title = Objects.requireNonNull(title, "title cannot be null");
			this.explanation = Objects.requireNonNull(explanation, "explanation cannot be null");
			this.documentations = Objects.requireNonNull(documentations, "documentations cannot be null");
			this.exercises = Objects.requireNonNull(exercises, "exercises cannot be null");
		}
	}
}