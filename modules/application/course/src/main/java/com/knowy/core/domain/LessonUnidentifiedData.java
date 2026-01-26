package com.knowy.core.domain;

import java.util.Objects;
import java.util.Set;

/**
 * Creation Contract for Lessons (Identity-less).
 * <p>
 * This interface specializes {@link LessonData} by locking its nested components to unidentified states. It represents
 * the complete data package required to create a new lesson without needing database identifiers.
 */
public interface LessonUnidentifiedData extends LessonData<DocumentationUnidentifiedData, ExerciseUnidentifiedData> {

	/**
	 * Immutable implementation of identity-less lesson data.
	 * <p>
	 * Provides a validated snapshot of a lesson's content, ensuring that all mandatory fields and collections are
	 * present before persistence.
	 *
	 * @param title          The lesson's title.
	 * @param explanation    The instructional text.
	 * @param documentations The set of new documentation (ID-less).
	 * @param exercises      The set of new exercises (ID-less).
	 */
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
