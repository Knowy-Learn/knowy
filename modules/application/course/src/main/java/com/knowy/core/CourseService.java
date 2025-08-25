package com.knowy.core;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.KnowyCourseSubscriptionException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.*;

import java.util.List;
import java.util.Set;

public class CourseService {

	private final CourseRepository courseRepository;
	private final GetUserCoursesUseCase getUserCoursesUseCase;
	private final GetAllCoursesRandomized getAllCoursesRandomized;
	private final GetRecommendedCoursesByCategoriesUseCase getRecommendedCoursesByCategoriesUseCase;
	private final GetAllCoursesUseCase getAllCoursesUseCase;
	private final GetCourseWithProgressUseCase getCourseWithProgressUseCase;
	private final SubscribeUserToCourseUseCase subscribeUserToCourseUseCase;

	public CourseService(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository
	) {
		this.courseRepository = courseRepository;
		this.getUserCoursesUseCase = new GetUserCoursesUseCase(userLessonRepository, courseRepository);
		this.getAllCoursesRandomized = new GetAllCoursesRandomized(courseRepository);
		this.getRecommendedCoursesByCategoriesUseCase = new GetRecommendedCoursesByCategoriesUseCase(courseRepository);
		this.getAllCoursesUseCase = new GetAllCoursesUseCase(courseRepository);
		this.getCourseWithProgressUseCase = new GetCourseWithProgressUseCase(
			courseRepository, userLessonRepository
		);
		this.subscribeUserToCourseUseCase = new SubscribeUserToCourseUseCase(lessonRepository, userLessonRepository);
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
	 * Retrieves a list of recommended courses for a specific user based on the provided categories.
	 *
	 * <p>This method delegates to the use case {@link GetRecommendedCoursesByCategoriesUseCase} to
	 * fetch and prioritize courses relevant to the given categories. The result is tailored for the specific user and
	 * limited to the most relevant courses.</p>
	 *
	 * @param userId     the ID of the user for whom the recommendations are generated
	 * @param categories a set of {@link Category} entities to guide the course recommendations
	 * @return a list of {@link Course} entities recommended for the user
	 * @throws KnowyInconsistentDataException if inconsistencies occur when retrieving course data
	 */
	public List<Course> getRecommendedCourses(int userId, Set<Category> categories) throws KnowyInconsistentDataException {
		return getRecommendedCoursesByCategoriesUseCase.execute(userId, categories);
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
	public List<Course> getAllCourses(Pagination pagination) throws KnowyCourseNotFound {
		return getAllCoursesUseCase.execute(pagination);
	}

	/**
	 * Retrieves a course along with the progress of a specific user in that course.
	 *
	 * <p>Delegates the operation to {@link GetCourseWithProgressUseCase#execute(int, int)} to fetch
	 * all lessons of the course the user is enrolled in and calculate overall progress.</p>
	 *
	 * @param userId   the ID of the user
	 * @param courseId the ID of the course
	 * @return a {@link GetUserLessonByCourseIdWithProgressResult} containing the course and the user's progress
	 * @throws KnowyInconsistentDataException if no lessons are found for the user in the given course
	 */
	public GetUserLessonByCourseIdWithProgressResult getCourseProgress(Integer userId, Integer courseId)
		throws KnowyInconsistentDataException {

		return getCourseWithProgressUseCase.execute(userId, courseId);
	}

	public Course findById(int id) throws KnowyInconsistentDataException {
		return courseRepository.findById(id)
			.orElseThrow(() -> new KnowyCourseNotFound("Not found course with  id: " + id));
	}

	public long getCoursesCompleted(int userId) throws KnowyInconsistentDataException {
		List<Course> userCourses = findAllByUserId(userId);
		return userCourses
			.stream()
			.filter(course -> {
				try {
					return getCourseProgress(userId, course.id()).progress() == 100f;
				} catch (KnowyInconsistentDataException e) {
					throw new RuntimeException(e);
				}
			})
			.count();
	}

	public long getCoursesPercentage(int userId) throws KnowyInconsistentDataException {
		long totalCourses = courseRepository.findAllWhereUserIsSubscribed(userId).size();
		long coursesCompleted = getCoursesCompleted(userId);
		return (totalCourses == 0)
			? 0
			: (int) Math.round((coursesCompleted * 100.0) / totalCourses);
	}
}
