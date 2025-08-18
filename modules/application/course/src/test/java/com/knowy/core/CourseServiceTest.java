package com.knowy.core;

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

import java.util.List;

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
}
