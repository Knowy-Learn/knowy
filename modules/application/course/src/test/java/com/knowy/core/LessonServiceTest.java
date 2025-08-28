package com.knowy.core;

import com.knowy.core.domain.Lesson;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyUserLessonNotFoundException;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.core.port.UserLessonRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class LessonServiceTest {

	@Mock
	private UserLessonRepository userLessonRepository;

	@Mock
	private UserExerciseRepository userExerciseRepository;

	@Mock
	private LessonRepository lessonRepository;

	@InjectMocks
	private LessonService lessonService;

	@Nested
	class GetUserLessonByIdUseCaseTest {

		@Test
		void given_validUserLessonId_when_getById_then_returnUserLesson() throws KnowyInconsistentDataException {
			int userId = 23;
			int lessonId = 40;

			Lesson lesson = new Lesson(lessonId, 12, 41, "Title", "Desc");
			UserLesson userLesson = new UserLesson(
				userId, lesson, LocalDate.now(), UserLesson.ProgressStatus.IN_PROGRESS);

			Mockito.when(userLessonRepository.findById(userId, lessonId))
				.thenReturn(Optional.of(userLesson));

			assertDoesNotThrow(() -> lessonService.getUserLessonById(userId, lessonId));
		}

		@Test
		void given_nonExistUserLessonId_when_getById_then_throwKnowyUserLessonNotFoundException() throws KnowyInconsistentDataException {
			int userId = 23;
			int lessonId = 40;

			Mockito.when(userLessonRepository.findById(userId, lessonId))
				.thenThrow(new KnowyUserLessonNotFoundException("UserLesson not found"));

			assertThrows(
				KnowyUserLessonNotFoundException.class,
				() -> lessonService.getUserLessonById(userId, lessonId)
			);
		}
	}

	@Nested
	class GetAllUserLessonByCourseIdUseCaseTest {

		@Test
		void given_validUserCourseIds_when_getUserLessonByCourseId_then_returnListOfUserLessons()
			throws KnowyInconsistentDataException {

			int userId = 23;
			int courseId = 12;

			Mockito.when(userLessonRepository.findAllByUserIdAndCourseId(userId, courseId))
				.thenReturn(List.of(
					Mockito.mock(UserLesson.class), Mockito.mock(UserLesson.class), Mockito.mock(UserLesson.class)
				));

			assertDoesNotThrow(() -> lessonService.getUserLessonAllByCourseId(userId, courseId));
		}

		@Test
		void given_courseWithoutLesson_when_getUserLessonByCourseId_then_throw() throws KnowyInconsistentDataException {
			int userId = 23;
			int courseId = 12;

			Mockito.when(userLessonRepository.findAllByUserIdAndCourseId(userId, courseId))
				.thenThrow(new KnowyUserLessonNotFoundException("User Lesson Not Found"));

			assertThrows(
				KnowyUserLessonNotFoundException.class,
				() -> lessonService.getUserLessonAllByCourseId(userId, courseId)
			);
		}
	}
}
