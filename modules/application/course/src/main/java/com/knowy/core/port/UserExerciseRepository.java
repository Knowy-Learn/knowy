package com.knowy.core.port;

import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository {

	UserExercise save(UserExercise userExercise) throws KnowyDataAccessException;

	Optional<UserExercise> findById(int userId, int exerciseId) throws KnowyDataAccessException;

	List<UserExercise> findAll() throws KnowyDataAccessException;

	List<UserExercise> findAllByUserIdAndLessonId(int userId, int lessonId) throws KnowyDataAccessException;

	Optional<UserExercise> findNextExerciseByLessonId(int publicUserId, int lessonId) throws KnowyDataAccessException;

	Optional<UserExercise> findNextExerciseByUserId(int userId) throws KnowyDataAccessException;
}