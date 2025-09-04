package com.knowy.core.usecase.adjust;

import com.knowy.core.domain.*;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.exercise.GetAllUserExercisesByCourseIdAndLessonIdUseCase;
import com.knowy.core.usecase.lesson.UpdateUserLessonStatusUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class AdjustLessonToSurveyResponseUseCaseTest {

	private GetAllUserExercisesByCourseIdAndLessonIdUseCase getAllUserExercisesByCourseIdAndLessonIdUseCase;
	private AdjustExerciseToSurveyResponseUseCase adjustExerciseToSurveyResponseUseCase;
	private UpdateUserLessonStatusUseCase updateUserLessonStatusUseCase;

	private AdjustLessonToSurveyResponseUseCase adjustLessonToSurveyResponseUseCase;

	@BeforeEach
	void setUp() {
		getAllUserExercisesByCourseIdAndLessonIdUseCase = Mockito.mock(GetAllUserExercisesByCourseIdAndLessonIdUseCase.class);
		adjustExerciseToSurveyResponseUseCase = Mockito.mock(AdjustExerciseToSurveyResponseUseCase.class);
		updateUserLessonStatusUseCase = Mockito.mock(UpdateUserLessonStatusUseCase.class);

		adjustLessonToSurveyResponseUseCase = new AdjustLessonToSurveyResponseUseCase(
			getAllUserExercisesByCourseIdAndLessonIdUseCase,
			adjustExerciseToSurveyResponseUseCase,
			updateUserLessonStatusUseCase
		);
	}

	@Test
	void given_constructWithRepositories_when_newClass_then_createdSuccessfully() {
		UserExerciseRepository userExerciseRepository = Mockito.mock(UserExerciseRepository.class);
		UserLessonRepository userLessonRepository = Mockito.mock(UserLessonRepository.class);

		AdjustLessonToSurveyResponseUseCase useCase = new AdjustLessonToSurveyResponseUseCase(
			userExerciseRepository,
			userLessonRepository
		);
		assertNotNull(useCase);
	}

	@Test
	void given_surveyResponseThatNotCompleteLesson_when_adjustLesson_then_returnAdjustedResult()
		throws KnowyDataAccessException {

		int userId = 23;
		ExerciseDifficult exerciseDifficult = ExerciseDifficult.EASY;

		Exercise exercise1 = Mockito.mock(Exercise.class);
		Exercise exercise2 = Mockito.mock(Exercise.class);
		Exercise exercise3 = Mockito.mock(Exercise.class);
		UserExercise userExercise1 = new UserExercise(userId, exercise1, 50, LocalDateTime.now());
		UserExercise userExercise2 = new UserExercise(userId, exercise2, 76, LocalDateTime.now());
		UserExercise userExercise3 = new UserExercise(userId, exercise3, 70, LocalDateTime.now());
		List<UserExercise> userExercises = List.of(userExercise1, userExercise2, userExercise3);

		Mockito.when(getAllUserExercisesByCourseIdAndLessonIdUseCase.execute(userId, exercise2.lessonId()))
			.thenReturn(userExercises);

		AdjustLessonToSurveyResponseResult result = assertDoesNotThrow(() ->
			adjustLessonToSurveyResponseUseCase.execute(exerciseDifficult, userExercise2)
		);

		Mockito.verify(adjustExerciseToSurveyResponseUseCase, Mockito.times(1))
			.execute(exerciseDifficult, userExercise2);
		Mockito.verify(getAllUserExercisesByCourseIdAndLessonIdUseCase, Mockito.times(1))
			.execute(userId, exercise2.lessonId());
		assertAll(
			() -> assertEquals(userId, result.userId()),
			() -> assertEquals(exercise2.lessonId(), result.lessonId()),
			() -> assertEquals(exercise2.id(), result.exerciseId()),
			() -> assertEquals(userExercises, result.userExercises()),
			() -> assertEquals("0.65", String.format(Locale.US, "%.2f", result.lessonProgress())),
			() -> assertEquals(UserLesson.ProgressStatus.IN_PROGRESS, result.lessonStatus())
		);
	}

	@Test
	void given_surveyResponseCompleteLesson_when_adjustLesson_then_returnAdjustedResult() throws KnowyDataAccessException {
		int userId = 23;
		ExerciseDifficult exerciseDifficult = ExerciseDifficult.EASY;

		Exercise exercise1 = Mockito.mock(Exercise.class);
		Exercise exercise2 = Mockito.mock(Exercise.class);
		Exercise exercise3 = Mockito.mock(Exercise.class);
		UserExercise userExercise1 = new UserExercise(userId, exercise1, 100, LocalDateTime.now());
		UserExercise userExercise2 = new UserExercise(userId, exercise2, 100, LocalDateTime.now());
		UserExercise userExercise3 = new UserExercise(userId, exercise3, 100, LocalDateTime.now());
		List<UserExercise> userExercises = List.of(userExercise1, userExercise2, userExercise3);

		UserLesson userLesson = new UserLesson(
			userId, Mockito.mock(Lesson.class), LocalDate.now(), UserLesson.ProgressStatus.COMPLETED
		);

		Mockito.when(getAllUserExercisesByCourseIdAndLessonIdUseCase.execute(userId, exercise2.lessonId()))
			.thenReturn(userExercises);
		Mockito.when(updateUserLessonStatusUseCase.execute(UserLesson.ProgressStatus.COMPLETED, userId, exercise2.lessonId()))
			.thenReturn(userLesson);

		AdjustLessonToSurveyResponseResult result = assertDoesNotThrow(() ->
			adjustLessonToSurveyResponseUseCase.execute(exerciseDifficult, userExercise2)
		);

		Mockito.verify(adjustExerciseToSurveyResponseUseCase, Mockito.times(1))
			.execute(exerciseDifficult, userExercise2);
		Mockito.verify(getAllUserExercisesByCourseIdAndLessonIdUseCase, Mockito.times(1))
			.execute(userId, exercise2.lessonId());
		assertAll(
			() -> assertEquals(userId, result.userId()),
			() -> assertEquals(exercise2.lessonId(), result.lessonId()),
			() -> assertEquals(exercise2.id(), result.exerciseId()),
			() -> assertEquals(userExercises, result.userExercises()),
			() -> assertEquals("1.00", String.format(Locale.US, "%.2f", result.lessonProgress())),
			() -> assertEquals(UserLesson.ProgressStatus.COMPLETED, result.lessonStatus())
		);
	}

	@Test
	void given_surveyResponseButNotFoundExercises_when_adjustLesson_then_throwKnowyDataAccessException()
		throws KnowyDataAccessException {

		int userId = 23;
		ExerciseDifficult exerciseDifficult = ExerciseDifficult.EASY;
		Exercise exercise = Mockito.mock(Exercise.class);
		UserExercise userExercise = new UserExercise(userId, exercise, 50, LocalDateTime.now());

		Mockito.when(getAllUserExercisesByCourseIdAndLessonIdUseCase.execute(Mockito.anyInt(), Mockito.anyInt()))
			.thenReturn(List.of());

		assertThrows(
			KnowyInconsistentDataException.class,
			() -> adjustLessonToSurveyResponseUseCase.execute(exerciseDifficult, userExercise)
		);
	}
}
