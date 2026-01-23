package com.knowy.core.domain;

import java.util.Set;

public interface LessonData<D extends DocumentationUnidentifiedData, E extends ExerciseData<? extends OptionUnidentifiedData>> extends LessonMinData {

	Set<D> documentations();

	Set<E> exercises();

}