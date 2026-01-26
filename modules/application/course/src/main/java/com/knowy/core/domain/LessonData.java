package com.knowy.core.domain;

import java.util.Set;

/**
 * Structural Generic Contract for Lesson content.
 * <p>
 * This interface defines the complete data tree of a lesson, enforcing that associated documentation and exercises
 * follow their respective domain constraints.
 *
 * @param <D> the documentation view (constrained to unidentified content)
 * @param <E> the exercise view (enforcing a tree with valid options)
 */
public interface LessonData<D extends DocumentationUnidentifiedData, E extends ExerciseData<? extends OptionUnidentifiedData>> extends LessonMinData {

	Set<D> documentations();

	Set<E> exercises();

}