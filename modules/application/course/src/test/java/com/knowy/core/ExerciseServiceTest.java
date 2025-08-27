package com.knowy.core;

import com.knowy.core.domain.Exercise;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.port.ExerciseRepository;
import com.knowy.core.port.UserExerciseRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

	@Mock
	private UserExerciseRepository userExerciseRepository;

	@Mock
	private ExerciseRepository exerciseRepository;

	@InjectMocks
	private ExerciseService exerciseService;

	@Nested
	class GetNextExerciseByLessonIdUseCaseTest {

		@Test
		void given_validUserAndLesson_when_getNextExercise_then_returnNextUserExercise() throws KnowyDataAccessException {
			int userId = 6;
			int lessonId = 129;

			UserExercise userExercise = new UserExercise(
				userId, Mockito.mock(Exercise.class), 45, LocalDateTime.now());

			Mockito.when(userExerciseRepository.findNextExerciseByLessonId(userId, lessonId))
				.thenReturn(Optional.of(userExercise));

			UserExercise result = assertDoesNotThrow(() -> exerciseService.getNextExerciseByLessonId(userId, lessonId));
			assertEquals(userExercise, result);
		}

		@Test
		void given_dataAccessException_when_getNextExercise_then_throwKnowyDataAccessException() throws KnowyDataAccessException {
			int userId = 6;
			int lessonId = 129;

			Mockito.when(userExerciseRepository.findNextExerciseByLessonId(userId, lessonId))
				.thenThrow(new KnowyDataAccessException("Data Access"));

			assertThrows(
				KnowyDataAccessException.class,
				() -> exerciseService.getNextExerciseByLessonId(userId, lessonId)
			);
		}
	}
}
