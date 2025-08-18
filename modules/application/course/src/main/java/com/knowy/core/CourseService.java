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
import com.knowy.core.usecase.GetUserCoursesUseCase;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseService {

	private final CourseRepository courseRepository;
	private final LessonRepository lessonRepository;
	private final UserLessonRepository userLessonRepository;
	private final CategoryRepository categoryRepository;
	private final GetUserCoursesUseCase getUserCoursesUseCase;
	private final GetAllCoursesRandomized getAllCoursesRandomized;

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
	 * which fetches all courses from the repository and returns them shuffled.
	 * Useful for providing variety in course recommendations or avoiding fixed ordering.</p>
	 *
	 * @return a randomized list of {@link Course} entities
	 * @throws KnowyInconsistentDataException if inconsistencies occur when retrieving course data
	 */
	public List<Course> findAllRandom() throws KnowyInconsistentDataException {
		return getAllCoursesRandomized.execute();
	}

	public List<Course> getRecommendedCourses(Integer userId) throws KnowyInconsistentDataException {
		List<Course> userCourses = findAllByUserId(userId);

		List<Integer> userCourseIds = userCourses.stream()
			.map(Course::id)
			.toList();

		Set<String> userLanguages = userCourses.stream()
			.flatMap(course -> findLanguagesForCourse(course).stream())
			.collect(Collectors.toSet());

		List<Course> allCourses = findAllCourses()
			.stream()
			.filter(course -> !userCourseIds.contains(course.id()))
			.toList();

		List<Course> langMatching = allCourses.stream()
			.filter(course -> {
				List<String> courseLanguages = findLanguagesForCourse(course);
				return courseLanguages.stream().anyMatch(userLanguages::contains);
			}).toList();

		List<Course> recommendations = langMatching
			.stream()
			.limit(3)
			.collect(Collectors.toList());

		if (recommendations.size() < 3) {
			List<Course> remaining = allCourses.stream()
				.filter(course -> !langMatching.contains(course))
				.toList();

			for (Course course : remaining) {
				if (recommendations.size() >= 3) {
					break;
				}
				recommendations.add(course);
			}
		}
		return recommendations;
	}

	public void subscribeUserToCourse(int userId, int courseId) throws KnowyCourseSubscriptionException,
		KnowyInconsistentDataException {
		List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
		if (lessons.isEmpty()) {
			throw new KnowyCourseSubscriptionException("El curso no tiene lecciones disponibles");
		}

		ensureAlreadySubscribed(lessons, userId);

		lessons = lessons.stream()
			.sorted(Comparator.comparing(Lesson::id))
			.toList();

		for (int index = 0; index < lessons.size(); index++) {
			Lesson lesson = lessons.get(index);
			if (!userLessonRepository.existsById(userId, lesson.id())) {
				UserLesson userLesson = new UserLesson(
					userId,
					lesson,
					LocalDate.now(),
					index == 0 ? UserLesson.ProgressStatus.IN_PROGRESS : UserLesson.ProgressStatus.PENDING
				);
				userLessonRepository.save(userLesson);
			}
		}
	}

	private void ensureAlreadySubscribed(List<Lesson> lessons, Integer userId)
		throws KnowyInconsistentDataException, KnowyCourseSubscriptionException {

		for (Lesson lesson : lessons) {
			if (userLessonRepository.existsByUserIdAndLessonId(userId, lesson.id())) {
				throw new KnowyCourseSubscriptionException("Ya estás suscrito a este curso");
			}
		}
	}

	public List<Course> findAllCourses() throws KnowyInconsistentDataException {
		return courseRepository.findAll();
	}

	public String findCourseImage(Course course) {
		return course.image() != null ? course.image() : "https://picsum.photos/seed/picsum/200/300";
	}

	public List<String> findLanguagesForCourse(Course course) {
		return course.categories().stream()
			.map(Category::name)
			.toList();
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
