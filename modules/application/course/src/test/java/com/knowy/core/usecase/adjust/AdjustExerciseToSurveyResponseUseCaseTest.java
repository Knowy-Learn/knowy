package com.knowy.core.usecase.adjust;

import com.knowy.core.domain.Exercise;
import com.knowy.core.domain.ExerciseDifficult;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.port.UserExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdjustExerciseToSurveyResponseUseCaseTest {

	@Mock
	private UserExerciseRepository userExerciseRepository;

	@InjectMocks
	private AdjustExerciseToSurveyResponseUseCase adjustExerciseToSurveyResponseUseCase;

	@Test
	void given_validUserAnswer_when_processAnswer_then_saveUpdateData() throws KnowyDataAccessException {
		int userId = 12;
		UserExercise userExercise = new UserExercise(userId, Mockito.mock(Exercise.class), 50, LocalDateTime.now());
		UserExercise userExerciseRateZero = new UserExercise(
			userId, Mockito.mock(Exercise.class), 0, LocalDateTime.now()
		);
		UserExercise userExerciseRateHigh = new UserExercise(
			userId, Mockito.mock(Exercise.class), 90, LocalDateTime.now()
		);

		verifyProcessAnswer(userExerciseRateZero, ExerciseDifficult.EASY, 45, Duration.ofMinutes(15));
		verifyProcessAnswer(userExercise, ExerciseDifficult.EASY, 95, Duration.ofDays(1));
		verifyProcessAnswer(userExercise, ExerciseDifficult.MEDIUM, 70, Duration.ofMinutes(7));
		verifyProcessAnswer(userExerciseRateHigh, ExerciseDifficult.MEDIUM, 100, Duration.ofDays(1));
		verifyProcessAnswer(userExercise, ExerciseDifficult.HARD, 35, Duration.ofMinutes(5));
		verifyProcessAnswer(userExercise, ExerciseDifficult.FAIL, 20, Duration.ofMinutes(1));
	}


	@Test
	void given_userExerciseRateMinOrMax_when_executeWithMinAndMax() throws KnowyDataAccessException {
		int userId = 12;
		int maxRate = 100;
		int minRate = 0;

		UserExercise userExerciseRateMax = new UserExercise(
			userId, Mockito.mock(Exercise.class), 100, LocalDateTime.now()
		);
		UserExercise userExerciseRateMin = new UserExercise(
			userId, Mockito.mock(Exercise.class), 0, LocalDateTime.now()
		);

		verifyProcessAnswer(userExerciseRateMax, ExerciseDifficult.EASY, maxRate, Duration.ofDays(15));
		verifyProcessAnswer(userExerciseRateMin, ExerciseDifficult.FAIL, minRate, Duration.ofMinutes(1));
	}

	private void verifyProcessAnswer(
		UserExercise userExercise,
		ExerciseDifficult difficulty,
		int expectedRate,
		Duration expectedNextReviewDelta
	) throws KnowyDataAccessException {
		assertDoesNotThrow(() -> adjustExerciseToSurveyResponseUseCase.execute(difficulty, userExercise));

		ArgumentCaptor<UserExercise> captor = ArgumentCaptor.forClass(UserExercise.class);
		Mockito.verify(userExerciseRepository, Mockito.times(1)).save(captor.capture());
		UserExercise saved = captor.getValue();

		assertAll(
			() -> assertEquals(userExercise.userId(), saved.userId()),
			() -> assertEquals(expectedRate, saved.rate()),
			() -> assertTrue(saved.nextReview().isBefore(LocalDateTime.now().plus(expectedNextReviewDelta)))
		);
		Mockito.clearInvocations(userExerciseRepository);
	}

	@Test
	void given_dataAccessException_when_processAnswer_then_throwKnowyAccessException() throws KnowyDataAccessException {
		int userId = 12;
		UserExercise userExercise = new UserExercise(userId, Mockito.mock(Exercise.class), 50, LocalDateTime.now());

		Mockito.doThrow(KnowyDataAccessException.class)
			.when(userExerciseRepository)
			.save(Mockito.any(UserExercise.class));

		assertThrows(
			KnowyDataAccessException.class,
			() -> adjustExerciseToSurveyResponseUseCase.execute(ExerciseDifficult.EASY, userExercise)
		);
	}

}
