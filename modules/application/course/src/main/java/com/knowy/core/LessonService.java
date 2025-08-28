package com.knowy.core;

import com.knowy.core.domain.Lesson;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.*;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.lesson.GetAllUserLessonByCourseIdUseCase;
import com.knowy.core.usecase.lesson.GetUserLessonByIdUseCase;

import java.util.List;
import java.util.Optional;

public class LessonService {

	private final UserLessonRepository userLessonRepository;
	private final UserExerciseRepository userExerciseRepository;
	private final LessonRepository lessonRepository;
	private final GetUserLessonByIdUseCase getUserLessonByIdUseCase;
	private final GetAllUserLessonByCourseIdUseCase getAllUserLessonByCourseIdUseCase;

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
	 * Marks the current lesson as completed for the given user and, if a next lesson exists, sets its status to
	 * "in_progress".
	 *
	 * @throws KnowyUserLessonNotFoundException if the relationship for the given user and lesson is not found
	 */
	public void updateLessonStatusToCompleted(int userId, Lesson lesson)
		throws KnowyInconsistentDataException {
		UserLesson userLesson = getUserLessonById(userId, lesson.id());

		updateNextLessonStatusToInProgress(userId, lesson.id());
		userLessonRepository.save(new UserLesson(
			userId,
			lesson,
			userLesson.startDate(),
			UserLesson.ProgressStatus.COMPLETED
		));
	}

	private void updateNextLessonStatusToInProgress(int userId, int lessonId) throws KnowyInconsistentDataException {
		Lesson lesson = lessonRepository.findById(lessonId)
			.orElseThrow(() -> new KnowyLessonNotFoundException("Not found lesson with Id: " + lessonId));
		if (lesson.nextLessonId() == null) {
			return;
		}

		UserLesson userLesson = userLessonRepository.findById(userId, lesson.nextLessonId())
			.orElseThrow(() -> new KnowyUserLessonNotFoundException(
				"Not found lesson with id: " + lesson.nextLessonId() + "by user with id: " + userId
			));
		userLessonRepository.save(new UserLesson(
			userLesson.userId(),
			userLesson.lesson(),
			userLesson.startDate(),
			UserLesson.ProgressStatus.IN_PROGRESS
		));
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
