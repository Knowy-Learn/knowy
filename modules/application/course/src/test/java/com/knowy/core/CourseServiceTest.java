package com.knowy.core;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CategoryRepository;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserLessonRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

			Mockito.when(courseRepository.findAllRandom())
				.thenReturn(courses);

			List<Course> result = assertDoesNotThrow(() -> courseService.findAllRandom());
			assertNotNull(result);
			assertEquals(courses.size(), result.size());
			Mockito.verify(courseRepository, Mockito.times(1)).findAllRandom();
		}

		@Test
		void given_inconsistentCoursesData_when_getAllCoursesRandomized_then_throwKnowyInconsistentDataException()
			throws KnowyInconsistentDataException {

			Mockito.when(courseRepository.findAllRandom())
				.thenThrow(new KnowyInconsistentDataException("Inconsistent Data of courses"));

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.findAllRandom()
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

			Mockito.when(courseRepository.findAllRandomUserIsNotSubscribed(userId))
				.thenReturn(List.of(course1, course2, course3, course4, course5));

			List<Course> result = assertDoesNotThrow(() -> courseService.getRecommendedCourses(userId, categories));
			assertAll(
				() -> assertEquals(3, result.size()),
				() -> assertIterableEquals(List.of(course2, course5, course1), result)
			);
		}

		@Test
		void given_userWithoutCategories_when_getUserRecommendedCourses_then_returnCourses() throws KnowyInconsistentDataException {

			int userId = 1;
			Course course1 = Mockito.mock(Course.class);
			Course course2 = Mockito.mock(Course.class);
			Course course3 = Mockito.mock(Course.class);
			Course course4 = Mockito.mock(Course.class);

			Mockito.when(courseRepository.findAllRandomUserIsNotSubscribed(userId))
				.thenReturn(List.of(course1, course2, course3, course4));

			List<Course> result = assertDoesNotThrow(() -> courseService.getRecommendedCourses(userId, Set.of()));
			assertAll(
				() -> assertEquals(3, result.size()),
				() -> assertIterableEquals(List.of(course1, course2, course3), result)
			);
		}

		@Test
		void given_inconsistentData_when_getUserRecommendedCourses_then_throwKnowyInconsistentDataException()
			throws KnowyInconsistentDataException {

			int userId = 1;
			Set<Category> categories = Set.of(new Category(3, "Photography"));

			Mockito.when(courseRepository.findAllRandomUserIsNotSubscribed(userId))
					.thenThrow(new KnowyInconsistentDataException("Inconsistent Data of courses"));

			assertThrows(
				KnowyInconsistentDataException.class,
				() -> courseService.getRecommendedCourses(userId, categories)
			);
		}
	}
}
