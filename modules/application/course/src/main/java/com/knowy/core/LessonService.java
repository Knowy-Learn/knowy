package com.knowy.core;

import com.knowy.core.domain.ExerciseDifficult;
import com.knowy.core.domain.LessonBase;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyUnsupportedOperationRuntimeException;
import com.knowy.core.port.LessonBaseRepository;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.adjust.AdjustLessonToSurveyResponseResult;
import com.knowy.core.usecase.adjust.AdjustLessonToSurveyResponseUseCase;
import com.knowy.core.usecase.lesson.GetAllUserLessonByCourseIdUseCase;
import com.knowy.core.usecase.lesson.GetUserLessonByIdUseCase;
import com.knowy.core.usecase.lesson.UpdateUserLessonStatusUseCase;
import com.knowy.core.usecase.lessonbase.GetLessonBaseByIdUserCase;

import java.util.List;

/**
 * Service for managing lessons and user progress within lessons.
 */
public class LessonService {

	private final GetLessonBaseByIdUserCase getLessonBaseByIdUserCase;
	private final GetUserLessonByIdUseCase getUserLessonByIdUseCase;
	private final GetAllUserLessonByCourseIdUseCase getAllUserLessonByCourseIdUseCase;
	private final UpdateUserLessonStatusUseCase updateUserLessonStatusUseCase;
	private final AdjustLessonToSurveyResponseUseCase adjustLessonToSurveyResponseUseCase;

	/**
	 * Constructs a new {@code LessonService} with the required repositories.
	 *
	 * @param userLessonRepository   repository for accessing user lessons
	 * @param userExerciseRepository repository for accessing user exercises
	 * @param lessonBaseRepository   repository for accessing lesson base data
	 */
	public LessonService(
		UserLessonRepository userLessonRepository,
		UserExerciseRepository userExerciseRepository,
		LessonBaseRepository lessonBaseRepository
	) {
		this.getLessonBaseByIdUserCase = new GetLessonBaseByIdUserCase(lessonBaseRepository);
		this.getUserLessonByIdUseCase = new GetUserLessonByIdUseCase(userLessonRepository);
		this.getAllUserLessonByCourseIdUseCase = new GetAllUserLessonByCourseIdUseCase(userLessonRepository);
		this.updateUserLessonStatusUseCase = new UpdateUserLessonStatusUseCase(userLessonRepository);
		this.adjustLessonToSurveyResponseUseCase = new AdjustLessonToSurveyResponseUseCase(
			userExerciseRepository, userLessonRepository
		);
	}

	/**
	 * Retrieves the {@link LessonBase} for a given lesson ID.
	 *
	 * @param lessonId the ID of the lesson
	 * @return the corresponding {@link LessonBase} object
	 * @throws KnowyDataAccessException if the lesson cannot be found
	 */
	public LessonBase getLessonBaseById(int lessonId) throws KnowyDataAccessException {
		return getLessonBaseByIdUserCase.execute(lessonId);
	}

	/**
	 * Retrieves the {@link UserLesson} associated with the given user and lesson IDs.
	 * <p>
	 * If no relationship exists between the user and the lesson, a
	 * {@link com.knowy.core.exception.KnowyUserLessonNotFoundException} is thrown.
	 *
	 * @param userId   the ID of the user
	 * @param lessonId the ID of the lesson
	 * @return the corresponding {@link UserLesson} instance
	 * @throws KnowyInconsistentDataException if no lesson is found or inconsistent data is detected
	 */
	public UserLesson getUserLessonById(int userId, int lessonId) throws KnowyInconsistentDataException {
		return getUserLessonByIdUseCase.execute(userId, lessonId);
	}

	/**
	 * Retrieves all lessons associated with a specific user in a given course.
	 *
	 * <p>This method delegates to {@link GetAllUserLessonByCourseIdUseCase} to fetch the data.</p>
	 *
	 * @param userId   the ID of the user
	 * @param courseId the ID of the course
	 * @return a {@link List} of {@link UserLesson} for the specified user and course
	 * @throws KnowyInconsistentDataException if there is an issue retrieving the user lessons
	 */
	public List<UserLesson> getUserLessonAllByCourseId(int userId, int courseId) throws KnowyInconsistentDataException {
		return getAllUserLessonByCourseIdUseCase.execute(userId, courseId);
	}

	/**
	 * Updates the progress status of a user's lesson.
	 * <p>
	 * Currently, only {@link UserLesson.ProgressStatus#COMPLETED} is supported. Attempting to update to any other
	 * status will throw a {@link KnowyUnsupportedOperationRuntimeException}.
	 * </p>
	 *
	 * @param statusToUpdate the new progress status to set; only {@code COMPLETED} is currently supported
	 * @param userId         the ID of the user
	 * @param lessonId       the ID of the lesson
	 * @throws KnowyInconsistentDataException            if the user lesson cannot be found or saved
	 * @throws KnowyUnsupportedOperationRuntimeException if the given status is not {@code COMPLETED}
	 */
	public void updateUserLessonStatus(UserLesson.ProgressStatus statusToUpdate, int userId, int lessonId)
		throws KnowyInconsistentDataException, KnowyUnsupportedOperationRuntimeException {

		updateUserLessonStatusUseCase.execute(statusToUpdate, userId, lessonId);
	}


	/**
	 * Adjusts a lesson based on a user's response to an exercise.
	 * <p>
	 * This method updates the user's exercise rate and progress, calculates the lesson progress, and updates the lesson
	 * status if the progress threshold is met.
	 * </p>
	 *
	 * @param exerciseDifficult the difficulty of the user's response
	 * @param userExercise      the {@link UserExercise} to adjust
	 * @return an {@link AdjustLessonToSurveyResponseResult} containing the updated state
	 * @throws KnowyDataAccessException if there is an issue accessing or updating the exercises
	 */
	public AdjustLessonToSurveyResponseResult adjustLessonToSurvey(ExerciseDifficult exerciseDifficult, UserExercise userExercise)
		throws KnowyDataAccessException {

		return adjustLessonToSurveyResponseUseCase.execute(exerciseDifficult, userExercise);
	}
}
