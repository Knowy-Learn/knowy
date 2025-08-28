package com.knowy.server.infrastructure.controller;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyExerciseNotFoundException;
import com.knowy.core.exception.KnowyUserLessonNotFoundException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.CourseService;
import com.knowy.core.ExerciseService;
import com.knowy.core.LessonService;
import com.knowy.core.domain.ExerciseDifficult;
import com.knowy.server.infrastructure.controller.dto.ExerciseDto;
import com.knowy.server.infrastructure.controller.dto.ExerciseOptionDto;
import com.knowy.server.infrastructure.security.UserSecurityDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ExerciseController {

	private static final String USER_LESSON_NOT_FOUND_TEMPLATE = "UserLesson not found for user ID: %s and lesson ID: %s";
	private static final String EXERCISE_MODEL_ATTRIBUTE = "exercise";
	private static final String EXERCISE_HTML_URL = "pages/exercise";

	private final ExerciseService exerciseService;
	private final LessonService lessonService;
	private final CourseService courseService;

	/**
	 * The constructor
	 *
	 * @param exerciseService the publicUserExerciseService
	 */
	public ExerciseController(ExerciseService exerciseService, LessonService lessonService, CourseService courseService) {
		this.exerciseService = exerciseService;
		this.lessonService = lessonService;
		this.courseService = courseService;
	}

	/**
	 * Handles GET requests to display the next exercise for review within a lesson.
	 *
	 * @param lessonId    the ID of the lesson to get the exercise from
	 * @param userDetails the authenticated user details
	 * @param model       the model to pass data to the view
	 * @return the name of the exercise view page, or error page if exercise not found
	 */
	@GetMapping("/course/{lessonId}/exercise/review")
	public String exerciseLesson(
		@PathVariable("lessonId") int lessonId,
		@AuthenticationPrincipal UserSecurityDetails userDetails,
		Model model
	) {
		try {
			UserExercise userExercise = exerciseService
				.getNextExerciseByLessonId(userDetails.getUser().id(), lessonId);

			UserLesson userLesson = lessonService.getUserLessonById(userDetails.getUser().id(), lessonId);

			Course course = courseService.getById(userLesson.lesson().courseId());

			model.addAttribute(EXERCISE_MODEL_ATTRIBUTE, ExerciseDto.fromDomain(userExercise, course));
			model.addAttribute("mode", "ANSWERING");
			model.addAttribute("formReviewUrl", "/course/exercise/review");
			return EXERCISE_HTML_URL;
		} catch (KnowyDataAccessException e) {
			return "error/error";
		}
	}

	/**
	 * Handles POST requests when a user submits an answer for an exercise review.
	 *
	 * @param userDetails the authenticated user details
	 * @param exerciseId  the ID of the exercise being answered
	 * @param answerId    the ID of the selected answer option
	 * @param model       the model to pass data to the view
	 * @return the name of the exercise view page showing if the answer was correct or not
	 * @throws KnowyExerciseNotFoundException if the exercise is not found
	 * @throws KnowyUserNotFoundException     if the user is not found
	 */
	@PostMapping("/course/exercise/review")
	public String exerciseLessonReview(
		@AuthenticationPrincipal UserSecurityDetails userDetails,
		@RequestParam("exerciseId") int exerciseId,
		@RequestParam("answerId") int answerId,
		Model model
	) throws KnowyDataAccessException {

		UserExercise userExercise = exerciseService.getByIdOrCreate(userDetails.getUser().id(),
			exerciseId);

		UserLesson userLesson = lessonService
			.getUserLessonById(userDetails.getUser().id(), userExercise.exercise().lessonId());

		Course course = courseService.getById(userLesson.lesson().courseId());
		ExerciseDto exerciseDto = ExerciseDto.fromDomain(userExercise, course, answerId);

		if (!isCorrectAnswer(exerciseDto.options(), answerId)) {
			model.addAttribute("mode", "FAILING");
		} else {
			model.addAttribute("mode", "REVIEWING");
		}
		model.addAttribute(EXERCISE_MODEL_ATTRIBUTE, exerciseDto);
		model.addAttribute("formEvaluateUrl", "/course/exercise/evaluate");
		return EXERCISE_HTML_URL;
	}

	private boolean isCorrectAnswer(List<ExerciseOptionDto> options, int answer) {
		return options.stream()
			.filter(option -> option.id() == answer)
			.anyMatch(option -> option.status() == ExerciseOptionDto.AnswerStatus.RESPONSE_SUCCESS);
	}

	/**
	 * Handles POST requests to evaluate the user's difficulty rating for an exercise.
	 *
	 * @param userDetails the authenticated user details
	 * @param exerciseId  the ID of the exercise being evaluated
	 * @param evaluation  the difficulty rating provided by the user
	 * @return a redirect to the lesson page if average rate >= 80, otherwise to the exercise review page
	 * @throws KnowyExerciseNotFoundException   if the exercise is not found
	 * @throws KnowyUserNotFoundException       if the user is not found
	 * @throws KnowyUserLessonNotFoundException if the publicUserLesson is not found
	 */
	@PostMapping("/course/exercise/evaluate")
	public String exerciseLessonEvaluate(
		@AuthenticationPrincipal UserSecurityDetails userDetails,
		@RequestParam("exerciseId") int exerciseId,
		@RequestParam("evaluation") ExerciseDifficult evaluation
	) throws KnowyDataAccessException {
		UserExercise userExercise = exerciseService
			.getByIdOrCreate(userDetails.getUser().id(), exerciseId);

		UserLesson userLesson = lessonService
			.getUserLessonById(userDetails.getUser().id(), userExercise.exercise().lessonId());

		exerciseService.processUserAnswer(evaluation, userExercise);

		int lessonId = userExercise.exercise().lessonId();
		int courseId = userLesson.lesson().courseId();

		double average = lessonService.getAverageRateByLessonId(lessonId);
		if (average >= 80) {
			lessonService.updateLessonStatusToCompleted(userDetails.getUser().id(), userLesson.lesson());
			return "redirect:/course/%d".formatted(courseId);
		}
		return "redirect:/course/%d/exercise/review".formatted(lessonId);
	}

	/**
	 * Handles GET requests to show the next exercise for the authenticated user.
	 *
	 * @param userDetails the authenticated user details
	 * @param model       the model to add attributes for the view
	 * @return the exercise page view, or error page if no exercise is found
	 */
	@GetMapping("/exercise/review")
	public String exerciseLesson(
		@AuthenticationPrincipal UserSecurityDetails userDetails,
		Model model
	) {
		try {
			UserExercise userExercise = exerciseService
				.getNextExerciseByUserId(userDetails.getUser().id());

			UserLesson userLesson = lessonService
				.getUserLessonById(userDetails.getUser().id(), userExercise.exercise().lessonId());

			Course course = courseService.getById(userLesson.lesson().courseId());

			model.addAttribute(EXERCISE_MODEL_ATTRIBUTE, ExerciseDto.fromDomain(userExercise, course));
			model.addAttribute("mode", "ANSWERING");
			model.addAttribute("formReviewUrl", "/exercise/review");
			return EXERCISE_HTML_URL;
		} catch (KnowyDataAccessException e) {
			return "error/error";
		}
	}

	/**
	 * Handles POST requests when a user submits an answer to an exercise.
	 *
	 * @param userDetails the authenticated user details
	 * @param exerciseId  the ID of the exercise being answered
	 * @param answerId    the ID of the selected answer option
	 * @param model       the model to add attributes for the view
	 * @return the exercise page view with feedback on the answer
	 * @throws KnowyExerciseNotFoundException if the exercise cannot be found
	 * @throws KnowyUserNotFoundException     if the user cannot be found
	 */
	@PostMapping("/exercise/review")
	public String exerciseReview(
		@AuthenticationPrincipal UserSecurityDetails userDetails,
		@RequestParam("exerciseId") int exerciseId,
		@RequestParam("answerId") int answerId,
		Model model
	) throws KnowyDataAccessException {

		UserExercise userExercise = exerciseService
			.getByIdOrCreate(userDetails.getUser().id(), exerciseId);

		UserLesson userLesson = lessonService
			.getUserLessonById(userDetails.getUser().id(), userExercise.exercise().lessonId());

		Course course = courseService.getById(userLesson.lesson().courseId());

		ExerciseDto exerciseDto = ExerciseDto.fromDomain(
			userExercise,
			course,
			answerId
		);

		if (!isCorrectAnswer(exerciseDto.options(), answerId)) {
			model.addAttribute("mode", "FAILING");
		} else {
			model.addAttribute("mode", "REVIEWING");
		}
		model.addAttribute(EXERCISE_MODEL_ATTRIBUTE, exerciseDto);
		model.addAttribute("formEvaluateUrl", "/exercise/evaluate");
		return EXERCISE_HTML_URL;
	}


	/**
	 * Handles POST requests to evaluate the user's difficulty rating for an exercise.
	 *
	 * @param userDetails the authenticated user details
	 * @param exerciseId  the ID of the exercise being evaluated
	 * @param evaluation  the difficulty rating provided by the user
	 * @return a redirect to the exercise review page
	 * @throws KnowyExerciseNotFoundException if the exercise cannot be found
	 * @throws KnowyUserNotFoundException     if the user cannot be found
	 */
	@PostMapping("/exercise/evaluate")
	public String exerciseEvaluate(
		@AuthenticationPrincipal UserSecurityDetails userDetails,
		@RequestParam("exerciseId") int exerciseId,
		@RequestParam("evaluation") ExerciseDifficult evaluation
	) throws KnowyDataAccessException {
		UserExercise userExercise = exerciseService
			.getByIdOrCreate(userDetails.getUser().id(), exerciseId);

		exerciseService.processUserAnswer(evaluation, userExercise);
		return "redirect:/exercise/review";
	}
}
