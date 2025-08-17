package com.knowy.core.port;

import com.knowy.core.domain.Exercise;

import java.util.Optional;

public interface ExerciseRepository {
	Optional<Exercise> findById(int id);
}
