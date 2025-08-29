package com.knowy.core;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyExerciseNotFoundException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyUnsupportedOperationRuntimeException;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.lesson.GetAllUserLessonByCourseIdUseCase;
import com.knowy.core.usecase.lesson.GetUserLessonByIdUseCase;
import com.knowy.core.usecase.lesson.UpdateUserLessonStatusUseCase;

import java.util.List;
import java.util.Optional;

public class LessonService {

	private final UserLessonRepository userLessonRepository;
	private final UserExerciseRepository userExerciseRepository;
	private final LessonRepository lessonRepository;
	private final GetUserLessonByIdUseCase getUserLessonByIdUseCase;
	private final GetAllUserLessonByCourseIdUseCase getAllUserLessonByCourseIdUseCase;
	private final UpdateUserLessonStatusUseCase updateUserLessonStatusUseCase;

	/**
	 * The constructor
	 *
	 * @param userLessonRepository the publicUserLessonRepository
	 */
	public LessonService(
		UserLessonRepository userLessonRepository,
		UserExerciseRepository userExerciseRepository,
		LessonRepository lessonRepository
	) {
		this.userLessonRepository = userLessonRepository;
		this.userExerciseRepository = userExerciseRepository;
		this.lessonRepository = lessonRepository;

		this.getUserLessonByIdUseCase = new GetUserLessonByIdUseCase(userLessonRepository);
		this.getAllUserLessonByCourseIdUseCase = new GetAllUserLessonByCourseIdUseCase(userLessonRepository);
		this.updateUserLessonStatusUseCase = new UpdateUserLessonStatusUseCase(userLessonRepository);
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

	public Optional<Double> findAverageRateByLessonId(int lessonId) throws KnowyDataAccessException {
		return userExerciseRepository.findAverageRateByLessonId(lessonId);
	}

	public double getAverageRateByLessonId(int lessonId) throws KnowyDataAccessException {
		return findAverageRateByLessonId(lessonId)
			.orElseThrow(() -> new KnowyExerciseNotFoundException(
				"No average rate found for lesson ID " + lessonId));
	}
}
