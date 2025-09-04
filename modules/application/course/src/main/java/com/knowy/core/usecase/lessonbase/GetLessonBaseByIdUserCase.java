package com.knowy.core.usecase.lessonbase;

import com.knowy.core.domain.LessonBase;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyLessonNotFoundException;
import com.knowy.core.port.LessonBaseRepository;

/**
 * Use case for retrieving a {@link LessonBase} by its ID.
 * <p>
 * This use case fetches a lesson from the {@link LessonBaseRepository} by its unique identifier. If no lesson is found
 * with the given ID, a {@link KnowyLessonNotFoundException} is thrown.
 * </p>
 */
public class GetLessonBaseByIdUserCase {

	private final LessonBaseRepository lessonBaseRepository;

	/**
	 * Constructs a new {@code GetLessonBaseByIdUserCase} with the provided repository.
	 *
	 * @param lessonBaseRepository the repository used to fetch lesson data
	 */
	public GetLessonBaseByIdUserCase(LessonBaseRepository lessonBaseRepository) {
		this.lessonBaseRepository = lessonBaseRepository;
	}

	/**
	 * Retrieves a lesson by its ID.
	 *
	 * @param lessonId the ID of the lesson to retrieve
	 * @return the {@link LessonBase} with the given ID
	 * @throws KnowyDataAccessException     if there is an error accessing the repository
	 * @throws KnowyLessonNotFoundException if no lesson exists with the given ID
	 */
	public LessonBase execute(int lessonId) throws KnowyDataAccessException {
		return lessonBaseRepository.findById(lessonId)
			.orElseThrow(() -> new KnowyLessonNotFoundException(
				"Lesson with id " + lessonId + " not found"
			));
	}
}
