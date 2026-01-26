package com.knowy.core.domain;

import java.util.List;

/**
 * Structural Contract for Exercise data.
 * <p>
 * This interface serves as the generic blueprint for any exercise view, enforcing a consistent structure while allowing
 * flexibility in how options are represented.
 *
 * @param <O> the specific type of option, constrained to the unidentified data contract
 */
public interface ExerciseData<O extends OptionUnidentifiedData> {

	String statement();

	List<O> options();

}