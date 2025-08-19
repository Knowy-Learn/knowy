package com.knowy.core;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;
import com.knowy.core.domain.Lesson;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.KnowyCourseSubscriptionException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CategoryRepository;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.GetAllCoursesRandomized;
import com.knowy.core.usecase.GetRecommendedCoursesByCategoriesUseCase;
import com.knowy.core.usecase.GetUserCoursesUseCase;
import com.knowy.core.usecase.SubscribeUserToCourseUseCase;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class CourseService {

	private final CourseRepository courseRepository;
	private final LessonRepository lessonRepository;
	private final UserLessonRepository userLessonRepository;
	private final CategoryRepository categoryRepository;
	private final GetUserCoursesUseCase getUserCoursesUseCase;
	private final GetAllCoursesRandomized getAllCoursesRandomized;
	private final GetRecommendedCoursesByCategoriesUseCase getRecommendedCoursesByCategoriesUseCase;
	private final SubscribeUserToCourseUseCase subscribeUserToCourseUseCase;

	public CourseService(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		CategoryRepository categoryRepository
	) {
		this.courseRepository = courseRepository;
		this.lessonRepository = lessonRepository;
		this.userLessonRepository = userLessonRepository;
		this.categoryRepository = categoryRepository;
		this.getUserCoursesUseCase = new GetUserCoursesUseCase(userLessonRepository, courseRepository);
		this.getAllCoursesRandomized = new GetAllCoursesRandomized(courseRepository);
		this.getRecommendedCoursesByCategoriesUseCase = new GetRecommendedCoursesByCategoriesUseCase(courseRepository);
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
	public List<Course> findAllRandom() throws KnowyInconsistentDataException {
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

	// TODO
	public void subscribeUserToCourse(int userId, int courseId)
		throws KnowyCourseSubscriptionException, KnowyInconsistentDataException {
		subscribeUserToCourseUseCase.execute(userId, courseId);
	}

	public List<Course> findAllCourses() throws KnowyInconsistentDataException {
		return courseRepository.findAll();
	}

	public int getCourseProgress(Integer userId, Integer courseId) {
		int totalLessons = lessonRepository.countByCourseId(courseId);
		if (totalLessons == 0) return 0;
		int completedLessons;
		try {
			completedLessons = userLessonRepository.countByUserIdAndCourseIdAndStatus(
				userId,
				courseId,
				UserLesson.ProgressStatus.COMPLETED
			);
		} catch (KnowyInconsistentDataException e) {
			return -1;
		}
		return (int) Math.round((completedLessons * 100.0 / totalLessons));
	}

	public List<String> findAllLanguages() {
		return categoryRepository.findAll()
			.stream()
			.map(Category::name)
			.toList();
	}

	public Course findById(int id) throws KnowyInconsistentDataException {
		return courseRepository.findById(id)
			.orElseThrow(() -> new KnowyCourseNotFound("Not found course with  id: " + id));
	}

	public long getCoursesCompleted(int userId) throws KnowyInconsistentDataException {
		List<Course> userCourses = findAllByUserId(userId);
		return userCourses
			.stream()
			.filter(course -> getCourseProgress(userId, course.id()) == 100)
			.count();
	}

	public long getTotalCourses(int userId) throws KnowyInconsistentDataException {
		return findAllByUserId(userId).size();
	}

	public long getCoursesPercentage(int userId) throws KnowyInconsistentDataException {
		long totalCourses = getTotalCourses(userId);
		long coursesCompleted = getCoursesCompleted(userId);
		return (totalCourses == 0)
			? 0
			: (int) Math.round((coursesCompleted * 100.0) / totalCourses);
	}
}
