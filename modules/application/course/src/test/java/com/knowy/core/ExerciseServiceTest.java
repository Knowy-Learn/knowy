package com.knowy.core;

import com.knowy.core.domain.Exercise;
import com.knowy.core.domain.Option;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyExerciseNotFoundException;
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
import java.util.List;
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

	@Nested
	class GetNextExerciseByUserIdUseCaseTest {

		@Test
		void given_validUserAndLesson_when_getNextExercise_then_returnNextUserExercise() throws KnowyDataAccessException {
			int userId = 6;

			UserExercise userExercise = new UserExercise(
				userId, Mockito.mock(Exercise.class), 45, LocalDateTime.now());

			Mockito.when(userExerciseRepository.findNextExerciseByUserId(userId))
				.thenReturn(Optional.of(userExercise));

			UserExercise result = assertDoesNotThrow(() -> exerciseService.getNextExerciseByUserId(userId));
			assertEquals(userExercise, result);
		}

		@Test
		void given_dataAccessException_when_getNextExercise_then_throwKnowyDataAccessException() throws KnowyDataAccessException {
			int userId = 6;

			Mockito.when(userExerciseRepository.findNextExerciseByUserId(userId))
				.thenThrow(new KnowyDataAccessException("Data Access"));

			assertThrows(
				KnowyDataAccessException.class,
				() -> exerciseService.getNextExerciseByUserId(userId)
			);
		}
	}

	@Nested
	class GetUserExerciseByIdOrCreate {

		@Test
		void given_createdUserExercise_when_getByIdOrCreate_then_returnUserExercise() throws KnowyDataAccessException {
			int userId = 23;
			int exerciseId = 93;

			UserExercise userExercise = new UserExercise(
				userId, Mockito.mock(Exercise.class), 25, LocalDateTime.now());

			Mockito.when(userExerciseRepository.findById(userId, exerciseId))
				.thenReturn(Optional.of(userExercise));

			UserExercise result = assertDoesNotThrow(() -> exerciseService.getByIdOrCreate(userId, exerciseId));
			assertAll(
				() -> assertEquals(userExercise, result),
				() -> Mockito.verify(exerciseRepository, Mockito.times(0)).findById(Mockito.anyInt())
			);
		}

		@Test
		void given_nonCreatedUserExercise_when_getByIdOrCreate_then_returnNewUserExercise() throws KnowyDataAccessException {
			int userId = 23;
			int exerciseId = 93;

			List<Option> options = List.of(Mockito.mock(Option.class), Mockito.mock(Option.class), Mockito.mock(Option.class));
			Exercise exercise = new Exercise(93, 2334, "What is Java?", options);

			UserExercise userExercise = new UserExercise(userId, exercise, 0, LocalDateTime.now());

			Mockito.when(userExerciseRepository.findById(userId, exerciseId))
				.thenReturn(Optional.empty());
			Mockito.when(exerciseRepository.findById(exerciseId))
				.thenReturn(Optional.of(exercise));

			UserExercise result = assertDoesNotThrow(() -> exerciseService.getByIdOrCreate(userId, exerciseId));
			assertAll(
				() -> assertEquals(userExercise.userId(), result.userId()),
				() -> assertEquals(userExercise.exercise(), result.exercise()),
				() -> assertEquals(userExercise.rate(), result.rate()),
				() -> Mockito.verify(exerciseRepository, Mockito.times(1)).findById(Mockito.anyInt())
			);
		}

		@Test
		void given_nonExistExerciseId_when_getByIdOrCreate_then_throwKnowyDataAccessException() throws KnowyDataAccessException {
			int userId = 23;
			int exerciseId = 93;

			Mockito.when(userExerciseRepository.findById(userId, exerciseId))
				.thenReturn(Optional.empty());
			Mockito.when(exerciseRepository.findById(exerciseId))
				.thenAnswer(invocation -> {
					throw new KnowyExerciseNotFoundException("Exercise not found");
				});

			assertThrows(
				KnowyExerciseNotFoundException.class,
				() -> exerciseService.getByIdOrCreate(userId, exerciseId)
			);
		}
	}
}
