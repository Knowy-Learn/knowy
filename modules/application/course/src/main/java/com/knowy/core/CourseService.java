package com.knowy.core;

import com.knowy.core.domain.*;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.KnowyCourseSubscriptionException;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.course.*;

import java.util.List;
import java.util.Set;

public class CourseService {

	private final GetUserCoursesUseCase getUserCoursesUseCase;
	private final GetAllCoursesRandomized getAllCoursesRandomized;
	private final GetRecommendCoursesUseCase getRecommendCoursesUseCase;
	private final GetAllCoursesUseCase getAllCoursesUseCase;
	private final GetCourseWithProgressUseCase getCourseWithProgressUseCase;
	private final GetAllCoursesWithProgressUseCase getAllCoursesWithProgressUseCase;
	private final GetCourseByIdUseCase getCourseByIdUseCase;
	private final SubscribeUserToCourseUseCase subscribeUserToCourseUseCase;
	private final GetAllUserCoursesByUserIdUseCase getAllUserCoursesByUserIdUseCase;
	private final FindNotSubscribedUseCase findNotSubscribedUseCase;

	public CourseService(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.getUserCoursesUseCase = new GetUserCoursesUseCase(userLessonRepository, courseRepository);
		this.getAllCoursesRandomized = new GetAllCoursesRandomized(courseRepository);
		this.getRecommendCoursesUseCase = new GetRecommendCoursesUseCase(courseRepository);
		this.getAllCoursesUseCase = new GetAllCoursesUseCase(courseRepository);
		this.getCourseWithProgressUseCase = new GetCourseWithProgressUseCase(
			courseRepository, userLessonRepository
		);
		this.getAllCoursesWithProgressUseCase = new GetAllCoursesWithProgressUseCase(userLessonRepository);
		this.getCourseByIdUseCase = new GetCourseByIdUseCase(courseRepository);
		this.subscribeUserToCourseUseCase = new SubscribeUserToCourseUseCase(lessonRepository, userLessonRepository);
		this.getAllUserCoursesByUserIdUseCase = new GetAllUserCoursesByUserIdUseCase(userCourseRepository);
		this.findNotSubscribedUseCase = new FindNotSubscribedUseCase(courseRepository);
	}

	/**
	 * Retrieves all courses associated with a given user.
	 *
	 * <p>This method delegates to {@link GetUserCoursesUseCase} to fetch the
	 * list of courses the user is enrolled in.</p>
	 *
	 * @param userId the ID of the user whose courses should be retrieved
	 * @return a list of {@link Course} entities, or an empty list if the user has no courses
	 * @throws KnowyInconsistentDataException if inconsistencies occur while retrieving course data
	 */
	public List<Course> findAllByUserId(Integer userId) throws KnowyInconsistentDataException {
		return getUserCoursesUseCase.execute(userId);
	}

	/**
	 * Retrieves all courses in a randomized order.
	 *
	 * <p>This method delegates to the {@link GetAllCoursesRandomized} use case,
	 * which fetches all courses from the repository and returns them shuffled. Useful for providing variety in course
	 * recommendations or avoiding fixed ordering.</p>
	 *
	 * @return a randomized list of {@link Course} entities
	 * @throws KnowyInconsistentDataException if inconsistencies occur when retrieving course data
	 */
	public List<Course> findAllInRandomOrder() throws KnowyInconsistentDataException {
		return getAllCoursesRandomized.execute();
	}

	/**
	 * Retrieves a paginated list of recommended courses for a specific user.
	 * <p>
	 * This method delegates the logic to {@link GetRecommendCoursesUseCase}, which filters out courses the user is
	 * already enrolled in and provides a randomized selection based on the provided pagination and filtering criteria.
	 *
	 * @param userId     the unique identifier of the user.
	 * @param pagination the object containing page size, current page, sorting, and filters.
	 * @return a {@link PagedResult} containing the recommended {@link Course} entities.
	 * @throws KnowyDataAccessException if there is an error during the retrieval process.
	 */
	public PagedResult<Course> findRecommended(int userId, Pagination pagination) throws KnowyDataAccessException {
		return getRecommendCoursesUseCase.execute(new GetRecommendCoursesCommand(userId, pagination));
	}

	// JAVADOC
	public PagedResult<Course> findNotSubscribed(int userId, Pagination pagination) throws KnowyDataAccessException {
		return findNotSubscribedUseCase.execute(userId, pagination);
	}

	/**
	 * Subscribes a user to all available lessons of a given course.
	 * <p>
	 * This method delegates the subscription process to the {@link SubscribeUserToCourseUseCase}. It ensures that the
	 * user is subscribed to all lessons they are not yet enrolled in.
	 *
	 * @param userId   the identifier of the user subscribing to the course
	 * @param courseId the identifier of the course to subscribe to
	 * @throws KnowyCourseSubscriptionException if the user is already subscribed to all lessons in the course, or if
	 *                                          the course has no available lessons
	 * @throws KnowyInconsistentDataException   if the course lesson sequence is inconsistent
	 */
	public void subscribeUserToCourse(int userId, int courseId)
		throws KnowyCourseSubscriptionException, KnowyInconsistentDataException {
		subscribeUserToCourseUseCase.execute(userId, courseId);
	}

	/**
	 * Retrieves a list of all courses according to the given pagination.
	 *
	 * @param pagination the pagination parameters
	 * @return a list of courses
	 * @throws KnowyCourseNotFound if no courses are found
	 */
	public PagedResult<Course> getAllCourses(Pagination pagination) throws KnowyCourseNotFound {
		return getAllCoursesUseCase.execute(pagination);
	}

	/**
	 * Retrieves a course by its unique identifier.
	 * <p>
	 * This method delegates to the {@link GetCourseByIdUseCase} to fetch the course. If no course exists with the given
	 * ID, an exception is thrown.
	 *
	 * @param id the unique identifier of the course
	 * @return the {@link Course} with the specified ID
	 * @throws KnowyInconsistentDataException if the course with the given ID does not exist
	 */
	public Course getById(int id) throws KnowyInconsistentDataException {
		return getCourseByIdUseCase.execute(id);
	}

	/**
	 * Retrieves a course along with the progress of a specific user in that course.
	 *
	 * <p>Delegates the operation to {@link GetCourseWithProgressUseCase#execute(int, int)} to fetch
	 * all lessons of the course the user is enrolled in and calculate overall progress.</p>
	 *
	 * @param userId   the ID of the user
	 * @param courseId the ID of the course
	 * @return a {@link GetCourseWithProgressResult} containing the course and the user's progress
	 * @throws KnowyInconsistentDataException if no lessons are found for the user in the given course
	 */
	public GetCourseWithProgressResult getCourseProgress(Integer userId, Integer courseId)
		throws KnowyInconsistentDataException {

		return getCourseWithProgressUseCase.execute(userId, courseId);
	}

	/**
	 * Retrieves all courses along with the progress of a specific user in each course.
	 * <p>
	 * Delegates to {@link GetAllCoursesWithProgressUseCase} to fetch all courses and calculate the user's progress for
	 * each course.
	 *
	 * @param userId the ID of the user whose course progress should be retrieved
	 * @return a list of {@link GetAllCoursesWithProgressResult} containing course data and user progress
	 * @throws KnowyInconsistentDataException if there is an inconsistency while retrieving course or progress data
	 */
	public List<GetAllCoursesWithProgressResult> getAllCourseProgress(int userId) throws KnowyInconsistentDataException {
		return getAllCoursesWithProgressUseCase.execute(userId);
	}

	/**
	 * Retrieves a paginated list of courses associated with a specific user by delegating to the appropriate use case.
	 *
	 * @param userId     the unique identifier of the user
	 * @param pagination the pagination parameters to apply to the result set
	 * @return a {@link PagedResult} containing the list of {@link UserCourse} records and metadata
	 * @throws KnowyDataAccessException if any error occurs while retrieving or processing data from the repository,
	 *                                  including connectivity issues, retrieval failures, or data integrity violations
	 */
	public PagedResult<UserCourse> getAllUserCoursesByUserId(
		int userId,
		Set<CourseStatus> courseStatusIds,
		Pagination pagination
	) throws KnowyDataAccessException {
		return getAllUserCoursesByUserIdUseCase.execute(userId, courseStatusIds, pagination);
	}
}
