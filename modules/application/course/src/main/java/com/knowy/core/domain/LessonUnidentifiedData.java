package com.knowy.core.domain;

import java.util.Objects;
import java.util.Set;

public interface LessonUnidentifiedData extends LessonData<DocumentationUnidentifiedData, ExerciseUnidentifiedData> {
	record InmutableLessonData(
		String title,
		String explanation,
		Set<DocumentationUnidentifiedData> documentations,
		Set<ExerciseUnidentifiedData> exercises
	) implements LessonUnidentifiedData {
		public InmutableLessonData(String title, String explanation, Set<DocumentationUnidentifiedData> documentations, Set<ExerciseUnidentifiedData> exercises) {
			this.title = Objects.requireNonNull(title, "title cannot be null");
			this.explanation = Objects.requireNonNull(explanation, "explanation cannot be null");
			this.documentations = Objects.requireNonNull(documentations, "documentations cannot be null");
			this.exercises = Objects.requireNonNull(exercises, "exercises cannot be null");
		}
	}
}
