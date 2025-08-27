package com.knowy.core;

import com.knowy.core.domain.*;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.KnowyCourseSubscriptionException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CategoryRepository;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.course.GetAllCoursesWithProgressResult;
import com.knowy.core.usecase.course.GetCourseWithProgressResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

	@Mock
	private CourseRepository courseRepository;

	@Mock
	private LessonRepository lessonRepository;

	@Mock
	private UserLessonRepository userLessonRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CourseService courseService;

	@Nested
	class GetUserCoursesUseCaseTest {

		@Test
		void given_userHasCourses_when_getUserCourses_then_returnListOfCourses() throws KnowyInconsistentDataException {

			Integer userId = 1;
			List<Integer> courseIds = List.of(1, 2, 3);
			Course course1 = Mockito.mock(Course.class);
			Course course2 = Mockito.mock(Course.class);
			Course course3 = Mockito.mock(Course.class);
			List<Course> courses = List.of(course1, course2, course3);

			Mockito.when(userLessonRepository.findCourseIdsByUserId(userId))
				.thenReturn(courseIds);
			Mockito.when(courseRepository.findAllById(courseIds))
				.thenReturn(courses);

			List<Course> result = assertDoesNotThrow(() -> courseService.findAllByUserId(userId));
			assertEquals(courses, result);
			assertEquals(courseIds.size(), result.size());
			assertEquals(courses.size(), result.size());
			Mockito.verify(courseRepository).findAllById(courseIds);
		}

		@Test
		void given_userDoesNotHaveCourses_when_getUserCourses_then_returnEmptyList() throws KnowyInconsistentDataException {
			Integer userId = 1;
			List<Integer> courseIds = List.of();
			List<Course> courses = List.of();

			Mockito.when(userLessonRepository.findCourseIdsByUserId(userId))
				.thenReturn(courseIds);

			List<Course> result = assertDoesNotThrow(() -> courseService.findAllByUserId(userId));
			assertEquals(courses, result);
		}

		@Test
		void given_inconsistentCourseIdsForUser_when_getUserCourses_then_throwKnowyInconsistentDataException()
			throws KnowyInconsistentDataException {

			Integer userId = 1;

			Mockito.when(userLessonRepository.findCourseIdsByUserId(userId))
				.thenThrow(new KnowyInconsistentDataException("Inconsistent Data at find courses ids by user Id" + userId));

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.findAllByUserId(userId)
			);
		}

		@Test
		void given_inconsistentCourseIds_when_getUserCourses_then_throwsKnowyInconsistentDataException()
			throws KnowyInconsistentDataException {

			Integer userId = 1;
			List<Integer> courseIds = List.of(1, 2, 3);

			Mockito.when(userLessonRepository.findCourseIdsByUserId(userId))
				.thenReturn(courseIds);
			Mockito.when(courseRepository.findAllById(courseIds))
				.thenThrow(new KnowyInconsistentDataException("Inconsistent Data of courses with ids: " + courseIds));

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.findAllByUserId(userId)
			);
		}
	}

	@Nested
	class GetAllCoursesRandomized {

		@Test
		void given_courses_when_getAllCoursesRandomized_then_returnCourses() throws KnowyInconsistentDataException {
			Course course1 = Mockito.mock(Course.class);
			Course course2 = Mockito.mock(Course.class);
			Course course3 = Mockito.mock(Course.class);
			List<Course> courses = List.of(course1, course2, course3);

			Mockito.when(courseRepository.findAllRandomOrder())
				.thenReturn(courses);

			List<Course> result = assertDoesNotThrow(() -> courseService.findAllInRandomOrder());
			assertNotNull(result);
			assertEquals(courses.size(), result.size());
			Mockito.verify(courseRepository, Mockito.times(1)).findAllRandomOrder();
		}

		@Test
		void given_inconsistentCoursesData_when_getAllCoursesRandomized_then_throwKnowyInconsistentDataException()
			throws KnowyInconsistentDataException {

			Mockito.when(courseRepository.findAllRandomOrder())
				.thenThrow(new KnowyInconsistentDataException("Inconsistent Data of courses"));

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.findAllInRandomOrder()
			);
		}
	}

	@Nested
	class GetRecommendedCoursesByCategoriesUseCase {

		@Test
		void given_userCategories_when_getUserRecommendedCourses_then_returnFirstPreferenceCourses() throws KnowyInconsistentDataException {

			int userId = 1;
			Set<Category> categories = Set.of(new Category(3, "Photography"));

			Course course1 = new Course(
				1, "Title", "Desc", "img", "Autor", LocalDateTime.now(),
				Set.of(new Category(1, "Java"), new Category(2, "Spring Boot")),
				new HashSet<>()
			);
			Course course2 = new Course(
				2, "Title", "Desc", "img", "Autor", LocalDateTime.now(),
				Set.of(new Category(3, "Photography")),
				new HashSet<>()
			);
			Course course3 = new Course(
				3, "Title", "Desc", "img", "Autor", LocalDateTime.now(),
				Set.of(new Category(3, "Sociology"), new Category(4, "Economics")),
				new HashSet<>()
			);
			Course course4 = new Course(
				4, "Title", "Desc", "img", "Autor", LocalDateTime.now(),
				Set.of(new Category(5, "Physics"), new Category(6, "Mathematics")),
				new HashSet<>()
			);
			Course course5 = new Course(
				5, "Title", "Desc", "img", "Autor", LocalDateTime.now(),
				Set.of(new Category(3, "Photography")),
				new HashSet<>()
			);
			Stream<Course> courseStream = Stream.of(course5, course2, course1, course3, course4);

			Mockito.when(courseRepository.findByCategoriesStreamingInRandomOrder(categories))
				.thenReturn(courseStream);
			Mockito.when(courseRepository.findAllWhereUserIsSubscribed(userId))
				.thenReturn(Set.of(course3));

			List<Course> result = assertDoesNotThrow(() -> courseService.getRecommendedCourses(userId, categories));
			assertAll(
				() -> assertEquals(3, result.size()),
				() -> assertIterableEquals(List.of(course5, course2, course1), result),
				() -> assertTrue(result.getFirst().categories().contains(new Category(3, "Photography"))),
				() -> assertTrue(result.get(1).categories().contains(new Category(3, "Photography"))),
				() -> assertFalse(result.get(2).categories().contains(new Category(3, "Photography")))
			);
		}

		@Test
		void given_userWithoutCategories_when_getUserRecommendedCourses_then_returnCourses() throws KnowyInconsistentDataException {

			int userId = 1;
			Course course1 = Mockito.mock(Course.class);
			Course course2 = Mockito.mock(Course.class);
			Course course3 = Mockito.mock(Course.class);
			Course course4 = Mockito.mock(Course.class);
			Course course5 = Mockito.mock(Course.class);

			Stream<Course> courseStream = Stream.of(course3, course1, course5, course2, course4);

			Mockito.when(courseRepository.findByCategoriesStreamingInRandomOrder(Set.of()))
				.thenReturn(courseStream);
			Mockito.when(courseRepository.findAllWhereUserIsSubscribed(userId))
				.thenReturn(Set.of(course3));

			List<Course> result = assertDoesNotThrow(() -> courseService.getRecommendedCourses(userId, Set.of()));
			assertAll(
				() -> assertEquals(3, result.size()),
				() -> assertIterableEquals(List.of(course1, course5, course2), result)
			);
		}

		@Test
		void given_inconsistentData_when_getUserRecommendedCourses_then_throwKnowyInconsistentDataException()
			throws KnowyInconsistentDataException {

			int userId = 1;
			Set<Category> categories = Set.of(new Category(3, "Photography"));

			Mockito.when(courseRepository.findAllWhereUserIsSubscribed(userId))
				.thenThrow(new KnowyInconsistentDataException("Inconsistent Data of courses"));

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.getRecommendedCourses(userId, categories)
			);
		}
	}

	@Nested
	class SubscribeUserToCourseUseCaseTest {

		@Test
		void given_userAndCourse_when_subscribe_then_userSubscribedToLessons() throws KnowyInconsistentDataException {
			int userId = 2;
			int courseId = 5;
			Lesson lesson1 = new Lesson(24, 5, 25, "Title 1", "Desc 1");
			Lesson lesson2 = new Lesson(25, 5, 26, "Title 2", "Desc 2");
			Lesson lesson3 = new Lesson(26, 5, 27, "Title 3", "Desc 3");
			Lesson lesson4 = new Lesson(27, 5, null, "Title 4", "Desc 4");
			Set<Lesson> lessons = Set.of(lesson1, lesson2, lesson3, lesson4);

			UserLesson userLesson1 = new UserLesson(
				2, lesson1, LocalDate.now(), UserLesson.ProgressStatus.IN_PROGRESS);
			UserLesson userLesson2 = new UserLesson(
				2, lesson2, LocalDate.now(), UserLesson.ProgressStatus.PENDING);
			UserLesson userLesson3 = new UserLesson(
				2, lesson3, LocalDate.now(), UserLesson.ProgressStatus.PENDING);
			UserLesson userLesson4 = new UserLesson(
				2, lesson4, LocalDate.now(), UserLesson.ProgressStatus.PENDING);
			List<UserLesson> userLessons = List.of(userLesson1, userLesson2, userLesson3, userLesson4);

			Mockito.when(lessonRepository.findAllByCourseId(courseId))
				.thenReturn(lessons.stream().toList());
			Mockito.when(lessonRepository.findAllWhereUserIsSubscribedTo(userId))
				.thenReturn(Set.of());

			assertDoesNotThrow(() -> courseService.subscribeUserToCourse(userId, courseId));
			Mockito.verify(lessonRepository, Mockito.times(1))
				.findAllWhereUserIsSubscribedTo(userId);
			Mockito.verify(userLessonRepository, Mockito.times(1))
				.saveAll(userLessons);
		}

		@Test
		void given_subscribeToCourse_when_subscribe_then_throwKnowyCourseSubscriptionException() {
			int userId = 2;
			int courseId = 5;

			Mockito.when(lessonRepository.findAllWhereUserIsSubscribedTo(userId))
				.thenReturn(Set.of());

			assertThrows(
				KnowyCourseSubscriptionException.class,
				() -> courseService.subscribeUserToCourse(userId, courseId)
			);
		}

		@Test
		void given_courseWithoutStartingLesson_when_subscribe_then_throwKnowyInconsistentDataException() {
			int userId = 2;
			int courseId = 5;
			Lesson lesson1 = new Lesson(24, 5, 25, "Title 1", "Desc 1");
			Lesson lesson2 = new Lesson(25, 5, 26, "Title 2", "Desc 2");
			Lesson lesson3 = new Lesson(26, 5, 27, "Title 3", "Desc 3");
			Lesson lesson4 = new Lesson(27, 5, 24, "Title 4", "Desc 4");
			Set<Lesson> lessons = Set.of(lesson1, lesson2, lesson3, lesson4);

			Mockito.when(lessonRepository.findAllByCourseId(courseId))
				.thenReturn(lessons.stream().toList());
			Mockito.when(lessonRepository.findAllWhereUserIsSubscribedTo(userId))
				.thenReturn(Set.of());

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.subscribeUserToCourse(userId, courseId)
			);
		}

		@Test
		void given_courseWithMultipleStartingLesson_when_subscribe_then_throwKnowyInconsistentDataException() {
			int userId = 2;
			int courseId = 5;
			Lesson lesson1 = new Lesson(24, 5, 25, "Title 1", "Desc 1");
			Lesson lesson2 = new Lesson(25, 5, null, "Title 2", "Desc 2");
			Lesson lesson3 = new Lesson(26, 5, 27, "Title 3", "Desc 3");
			Lesson lesson4 = new Lesson(27, 5, null, "Title 4", "Desc 4");
			Set<Lesson> lessons = Set.of(lesson1, lesson2, lesson3, lesson4);

			Mockito.when(lessonRepository.findAllByCourseId(courseId))
				.thenReturn(lessons.stream().toList());
			Mockito.when(lessonRepository.findAllWhereUserIsSubscribedTo(userId))
				.thenReturn(Set.of());

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.subscribeUserToCourse(userId, courseId)
			);
		}
	}

	@Nested
	class GetAllCoursesUseCase {

		@Test
		void given_validPaginationData_when_getAllCourses_then_returnListOfCourses() throws KnowyInconsistentDataException {
			Pagination pagination = new Pagination(1, 4);
			Course course5 = Mockito.mock(Course.class);
			Course course6 = Mockito.mock(Course.class);
			Course course7 = Mockito.mock(Course.class);
			Course course8 = Mockito.mock(Course.class);
			List<Course> courses = List.of(course5, course6, course7, course8);

			Mockito.when(courseRepository.findAll(pagination))
				.thenReturn(courses);

			List<Course> result = assertDoesNotThrow(() -> courseService.getAllCourses(pagination));
			assertEquals(result.size(), pagination.size());
		}

		@Test
		void given_invalidPaginationData_when_getAllCourses_then_throw() throws KnowyInconsistentDataException {
			Pagination pagination = new Pagination(1, 4);

			Mockito.when(courseRepository.findAll(pagination))
				.thenThrow(KnowyCourseNotFound.class);

			assertThrows(
				KnowyCourseNotFound.class,
				() -> courseService.getAllCourses(pagination)
			);
		}
	}

	@Nested
	class GetUSerLessonByCourseIdWithProgressUseCase {

		@Test
		void given_validCommand_when_getCourseProgress_then_returnCourseWithProgress()
			throws KnowyInconsistentDataException {

			int userId = 3;
			int courseId = 20;

			UserLesson userLesson1 = new UserLesson(
				userId, Mockito.mock(Lesson.class), LocalDate.now(), UserLesson.ProgressStatus.COMPLETED);
			UserLesson userLesson2 = new UserLesson(
				userId, Mockito.mock(Lesson.class), LocalDate.now(), UserLesson.ProgressStatus.COMPLETED);
			UserLesson userLesson3 = new UserLesson(
				userId, Mockito.mock(Lesson.class), LocalDate.now(), UserLesson.ProgressStatus.IN_PROGRESS);
			UserLesson userLesson4 = new UserLesson(
				userId, Mockito.mock(Lesson.class), LocalDate.now(), UserLesson.ProgressStatus.PENDING);
			List<UserLesson> userLessons = List.of(userLesson1, userLesson2, userLesson3, userLesson4);

			Course course = Mockito.mock(Course.class);

			Mockito.when(userLessonRepository.findAllByUserIdAndCourseId(userId, courseId))
				.thenReturn(userLessons);
			Mockito.when(courseRepository.findById(courseId))
				.thenReturn(Optional.ofNullable(course));

			GetCourseWithProgressResult result = assertDoesNotThrow(() ->
				courseService.getCourseProgress(userId, courseId)
			);
			assertEquals(0.625f, result.progress());
		}

		@Test
		void given_courseWithoutLesson_when_getCourseProgress_then_throwKnowyCourseNotFound() throws KnowyInconsistentDataException {
			int userId = 3;
			int courseId = 20;

			Mockito.when(userLessonRepository.findAllByUserIdAndCourseId(userId, courseId))
				.thenReturn(List.of());

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.getCourseProgress(userId, courseId)
			);
		}

		@Test
		void given_inconsistentData_when_getCourseProgress_then_returnCourseWithProgress()
			throws KnowyInconsistentDataException {

			int userId = 3;
			int courseId = 20;

			UserLesson userLesson1 = new UserLesson(
				userId, Mockito.mock(Lesson.class), LocalDate.now(), UserLesson.ProgressStatus.COMPLETED);
			UserLesson userLesson2 = new UserLesson(
				userId, Mockito.mock(Lesson.class), LocalDate.now(), UserLesson.ProgressStatus.COMPLETED);
			UserLesson userLesson3 = new UserLesson(
				userId, Mockito.mock(Lesson.class), LocalDate.now(), UserLesson.ProgressStatus.IN_PROGRESS);
			UserLesson userLesson4 = new UserLesson(
				userId, Mockito.mock(Lesson.class), LocalDate.now(), UserLesson.ProgressStatus.PENDING);
			List<UserLesson> userLessons = List.of(userLesson1, userLesson2, userLesson3, userLesson4);

			Mockito.when(userLessonRepository.findAllByUserIdAndCourseId(userId, courseId))
				.thenReturn(userLessons);
			Mockito.when(courseRepository.findById(courseId))
				.thenThrow(new KnowyInconsistentDataException("Inconsistent Data"));

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.getCourseProgress(userId, courseId)
			);
		}
	}

	@Nested
	class GetCourseByIdUseCaseTest {

		@Test
		void given_validCourseId_when_getCourseById_then_returnCourse() throws KnowyInconsistentDataException {
			int courseId = 34;
			Course course = Mockito.mock(Course.class);

			Mockito.when(courseRepository.findById(courseId))
				.thenReturn(Optional.ofNullable(course));

			Course result = assertDoesNotThrow(() -> courseService.getById(courseId));
			assertEquals(course, result);
		}

		@Test
		void given_inconsistentData_when_getCourseById_then_throwKnowyInconsistentDataException() throws KnowyInconsistentDataException {
			int courseId = 34;

			Mockito.when(courseRepository.findById(courseId))
				.thenThrow(new KnowyInconsistentDataException("Inconsistent Data"));

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.getById(courseId)
			);
		}

		@Test
		void given_nonExistCourse_when_getCourseById_then_throwKnowyCourseNotFound() throws KnowyInconsistentDataException {
			int courseId = 34;

			Mockito.when(courseRepository.findById(courseId))
				.thenReturn(Optional.empty());

			assertThrows(
				KnowyCourseNotFound.class,
				() -> courseService.getById(courseId)
			);
		}
	}

	@Nested
	class GetAllCoursesWithProgressUseCaseTest {

		@Test
		void given_validUserId_when_getAllCourseProgress_then_returnAllCoursesWithTheirProgress()
			throws KnowyInconsistentDataException {

			int userId = 100;
			int courseId1 = 10;
			int courseId2 = 29;

			Lesson l1 = new Lesson(23, courseId1, 24, "Title1", "Desc1");
			Lesson l2 = new Lesson(24, courseId1, 25, "Title2", "Desc2");
			Lesson l3 = new Lesson(25, courseId1, 26, "Title3", "Desc3");
			Lesson l4 = new Lesson(26, courseId1, null, "Title4", "Desc4");
			Lesson l5 = new Lesson(30, courseId2, 31, "Title5", "Desc5");
			Lesson l6 = new Lesson(31, courseId2, null, "Title6", "Desc6");

			List<UserLesson> userLessons = List.of(
				new UserLesson(userId, l1, LocalDate.now(), UserLesson.ProgressStatus.COMPLETED),
				new UserLesson(userId, l2, LocalDate.now(), UserLesson.ProgressStatus.COMPLETED),
				new UserLesson(userId, l3, LocalDate.now(), UserLesson.ProgressStatus.IN_PROGRESS),
				new UserLesson(userId, l4, LocalDate.now(), UserLesson.ProgressStatus.PENDING),
				new UserLesson(userId, l5, LocalDate.now(), UserLesson.ProgressStatus.IN_PROGRESS),
				new UserLesson(userId, l6, LocalDate.now(), UserLesson.ProgressStatus.PENDING)
			);

			Mockito.when(userLessonRepository.findAllWhereUserIsSubscribed(userId))
				.thenReturn(userLessons);

			List<GetAllCoursesWithProgressResult> results = assertDoesNotThrow(
				() -> courseService.getAllCourseProgress(userId)
			);
			assertAll(
				() -> assertEquals(2, results.size()),
				() -> assertEquals(4, results.getFirst().userLessons().size()),
				() -> assertEquals(2, results.get(1).userLessons().size()),
				() -> assertEquals(0.625f, results.getFirst().progress()),
				() -> assertEquals(0.25f, results.get(1).progress())
			);
		}

		@Test
		void given_inconsistentData_when_getAllCourseProgress_then_throwKnowyInconsistentDataException()
			throws KnowyInconsistentDataException {

			int userId = 100;

			Mockito.when(userLessonRepository.findAllWhereUserIsSubscribed(userId))
				.thenThrow(new KnowyInconsistentDataException("Inconsistent Data"));

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.getAllCourseProgress(userId)
			);
		}
	}
}
