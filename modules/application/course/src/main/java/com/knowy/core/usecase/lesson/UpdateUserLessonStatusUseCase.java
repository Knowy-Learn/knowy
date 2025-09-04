package com.knowy.core.usecase.lesson;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyUnsupportedOperationRuntimeException;
import com.knowy.core.exception.KnowyUserLessonNotFoundException;
import com.knowy.core.port.UserLessonRepository;

/**
 * Use case for updating the progress status of a {@link UserLesson}.
 * <p>
 * Currently, only updating the status to {@link UserLesson.ProgressStatus#COMPLETED} is supported. Attempting to set
 * any other status will throw a {@link KnowyUnsupportedOperationRuntimeException}. When completing a lesson, the next
 * lesson (if it exists) will automatically be advanced to the next logical status.
 * </p>
 */
public class UpdateUserLessonStatusUseCase {

	private final UserLessonRepository userLessonRepository;

	/**
	 * Constructs a new {@code UpdateUserLessonStatusUseCase} with the provided repository.
	 *
	 * @param userLessonRepository repository for persisting {@link UserLesson} updates
	 */
	public UpdateUserLessonStatusUseCase(UserLessonRepository userLessonRepository) {
		this.userLessonRepository = userLessonRepository;
	}

	/**
	 * Updates the progress status of a user's lesson.
	 * <p>
	 * If {@code statusToUpdate} is {@link UserLesson.ProgressStatus#COMPLETED}, this method will also update the next
	 * lesson (if any) to its next logical status. Only {@code COMPLETED} is supported.
	 * </p>
	 *
	 * @param statusToUpdate the new progress status to set; only {@code COMPLETED} is supported
	 * @param userId         the ID of the user
	 * @param lessonId       the ID of the lesson
	 * @return the updated {@link UserLesson} instance
	 * @throws KnowyInconsistentDataException            if the user lesson cannot be found or persisted
	 * @throws KnowyUnsupportedOperationRuntimeException if the status is not {@code COMPLETED}
	 */
	public UserLesson execute(UserLesson.ProgressStatus statusToUpdate, int userId, int lessonId)
		throws KnowyInconsistentDataException, KnowyUnsupportedOperationRuntimeException {

		if (!statusToUpdate.equals(UserLesson.ProgressStatus.COMPLETED)) {
			throw new KnowyUnsupportedOperationRuntimeException("Updating to " + statusToUpdate + " not implemented yet");
		}

		UserLesson userLesson = getUserLessonByIdOrThrow(userId, lessonId);
		if (userLesson.lesson().nextLessonId() != null) {
			updateNextLessonStatus(userId, userLesson.lesson().nextLessonId());
		}
		return saveUpdatedUserLesson(userLesson, statusToUpdate.getNextStatus());
	}

	private void updateNextLessonStatus(int userId, int nextLessonId) throws KnowyInconsistentDataException {
		UserLesson userLesson = getUserLessonByIdOrThrow(userId, nextLessonId);
		saveUpdatedUserLesson(userLesson, userLesson.status().getNextStatus());
	}

	private UserLesson getUserLessonByIdOrThrow(int userId, int lessonId) throws KnowyInconsistentDataException {
		return userLessonRepository.findById(userId, lessonId)
			.orElseThrow(() -> new KnowyUserLessonNotFoundException(
				"UserLesson not found: lessonId=" + lessonId + " userId=" + userId
			));
	}

	private UserLesson saveUpdatedUserLesson(UserLesson userLesson, UserLesson.ProgressStatus status) throws KnowyInconsistentDataException {
		return userLessonRepository.save(new UserLesson(
			userLesson.userId(),
			userLesson.lesson(),
			userLesson.startDate(),
			status
		));
	}
}
