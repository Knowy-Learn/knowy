package com.knowy.core.domain;

import java.util.List;

public interface ExerciseData<O extends OptionUnidentifiedData> {

	String statement();

	List<O> options();

}