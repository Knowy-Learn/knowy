package com.knowy.core.usecase.adjust;

import com.knowy.core.domain.ExerciseDifficult;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.exercise.GetAllUserExercisesByCourseIdAndLessonIdUseCase;
import com.knowy.core.usecase.lesson.UpdateUserLessonStatusUseCase;

import java.util.List;

/**
 * Use case for updating a lesson's progress after a user answers an exercise.
 *
 * <p>It updates the exercise progress, recalculates the lesson progress,
 * and marks the lesson as COMPLETED if progress ≥ 80%, otherwise keeps it IN_PROGRESS.</p>
 */
public class AdjustLessonToSurveyResponseUseCase {

	private final GetAllUserExercisesByCourseIdAndLessonIdUseCase getAllUserExercisesByCourseIdAndLessonIdUseCase;
	private final AdjustExerciseToSurveyResponseUseCase adjustExerciseToSurveyResponseUseCase;
	private final UpdateUserLessonStatusUseCase updateUserLessonStatusUseCase;

	/**
	 * Creates a new {@code AdjustLessonToSurveyResponseUseCase} using repository dependencies.
	 * <p>
	 * Instantiates the required use cases for adjusting exercises, retrieving user exercises, and updating lesson
	 * status.
	 *
	 * @param userExerciseRepository repository for accessing and saving user exercises
	 * @param userLessonRepository   repository for accessing and updating user lessons
	 */
	public AdjustLessonToSurveyResponseUseCase(
		UserExerciseRepository userExerciseRepository,
		UserLessonRepository userLessonRepository
	) {
		this(
			new GetAllUserExercisesByCourseIdAndLessonIdUseCase(userExerciseRepository),
			new AdjustExerciseToSurveyResponseUseCase(userExerciseRepository),
			new UpdateUserLessonStatusUseCase(userLessonRepository)
		);
	}

	/**
	 * Package-private constructor for injecting specific use case implementations.
	 * <p>
	 * Useful for testing or advanced configuration.
	 *
	 * @param getAllUserExercisesByCourseIdAndLessonIdUseCase use case for retrieving user exercises of a lesson
	 * @param adjustExerciseToSurveyResponseUseCase           use case for adjusting a user's exercise answer
	 * @param updateUserLessonStatusUseCase                   use case for updating lesson status
	 */
	AdjustLessonToSurveyResponseUseCase(
		GetAllUserExercisesByCourseIdAndLessonIdUseCase getAllUserExercisesByCourseIdAndLessonIdUseCase,
		AdjustExerciseToSurveyResponseUseCase adjustExerciseToSurveyResponseUseCase,
		UpdateUserLessonStatusUseCase updateUserLessonStatusUseCase
	) {
		this.getAllUserExercisesByCourseIdAndLessonIdUseCase = getAllUserExercisesByCourseIdAndLessonIdUseCase;
		this.adjustExerciseToSurveyResponseUseCase = adjustExerciseToSurveyResponseUseCase;
		this.updateUserLessonStatusUseCase = updateUserLessonStatusUseCase;
	}

	/**
	 * Executes the adjustment of a lesson based on a user's answer.
	 *
	 * @param exerciseDifficult difficulty selected by the user (EASY, MEDIUM, HARD, FAIL)
	 * @param userExercise      the answered exercise
	 * @return result with updated exercises, progress, and lesson status
	 * @throws KnowyDataAccessException       if exercise data cannot be accessed or saved
	 * @throws KnowyInconsistentDataException if the lesson has no exercises to evaluate
	 */
	public AdjustLessonToSurveyResponseResult execute(ExerciseDifficult exerciseDifficult, UserExercise userExercise)
		throws KnowyDataAccessException {

		int userId = userExercise.userId();
		int lessonId = userExercise.exercise().lessonId();
		int exerciseId = userExercise.exercise().id();

		adjustExerciseToSurveyResponseUseCase.execute(exerciseDifficult, userExercise);

		List<UserExercise> updatedUserExercises = loadUserExercises(userId, lessonId);
		double lessonProgress = calculateLessonProgress(updatedUserExercises);

		UserLesson.ProgressStatus lessonStatus = updateLessonStatusIfCompleted(userId, lessonId, lessonProgress);
		return new AdjustLessonToSurveyResponseResult(
			userId, lessonId, exerciseId, updatedUserExercises, lessonProgress, lessonStatus
		);
	}

	private List<UserExercise> loadUserExercises(int userId, int lessonId) throws KnowyDataAccessException {
		return getAllUserExercisesByCourseIdAndLessonIdUseCase.execute(userId, lessonId);
	}

	private UserLesson.ProgressStatus updateLessonStatusIfCompleted(int userId, int lessonId, double progress) throws KnowyInconsistentDataException {
		if (progress >= 0.8) {
			UserLesson userLesson = updateUserLessonStatusUseCase.execute(
				UserLesson.ProgressStatus.COMPLETED, userId, lessonId);
			return userLesson.status();
		}
		return UserLesson.ProgressStatus.IN_PROGRESS;
	}

	private double calculateLessonProgress(List<UserExercise> userExercises) throws KnowyInconsistentDataException {
		if (userExercises.isEmpty()) {
			throw new KnowyInconsistentDataException("Expected user exercises, but list is empty: " + userExercises);
		}
		return UserLesson.calculateLessonProgressById(userExercises);
	}
}

