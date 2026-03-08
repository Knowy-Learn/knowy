package com.knowy.server.api.controller;

import com.knowy.core.port.*;
import com.knowy.server.api.dto.*;
import com.knowy.server.api.usecase.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

public class CourseController implements CourseApi {

	private final GetUserCourseByIdUseCase getUserCourseByIdUseCase;
	private final GetUserLessonByIdAndCourseIdUseCase getUserLessonByIdAndCourseIdUseCase;
	private final SubscribeToCourseUseCase subscribeToCourseUseCase;
	private final FindUserCoursesUseCase findUserCoursesUseCase;
	private final FindNotSubscribedCoursesUseCase findNotSubscribedCoursesUseCase;
	private final FindUserRecommendationUseCase findUserRecommendationUseCase;

	public CourseController(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository,
		UserExerciseRepository userExerciseRepository,
		LessonBaseRepository lessonBaseRepository
	) {
		this.getUserCourseByIdUseCase = new GetUserCourseByIdUseCase(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
		this.getUserLessonByIdAndCourseIdUseCase = new GetUserLessonByIdAndCourseIdUseCase(
			userLessonRepository, userExerciseRepository, lessonBaseRepository
		);
		this.subscribeToCourseUseCase = new SubscribeToCourseUseCase(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
		this.findUserCoursesUseCase = new FindUserCoursesUseCase(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
		this.findNotSubscribedCoursesUseCase = new FindNotSubscribedCoursesUseCase(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
		this.findUserRecommendationUseCase = new FindUserRecommendationUseCase(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
	}

	/**
	 * GET /user/course : Get user course Fetch the user&#39;s personal course details by its ID.
	 *
	 * @param course The unique numerical ID of the course. (required)
	 * @return Successfully retrieved the course details. (status code 200) or Bad Request. The request is invalid or
	 * cannot be processed. (status code 400) or Access unauthorized. The request requires valid authentication
	 * credentials (e.g., a valid token). (status code 401) or Internal Server Error. Something went wrong on the
	 * server. (status code 500)
	 */
	@Override
	public ResponseEntity<UserCourseDto> userCourseGet(Integer course) {
		UserCourseDto userCourseDto = getUserCourseByIdUseCase.execute(course);
		return ResponseEntity.ok(userCourseDto);
	}

	/**
	 * GET /user/course/lesson : Get user lesson details Retrieve detailed information about a specific lesson within a
	 * course for the authenticated user, including content and progress status.
	 *
	 * @param course The unique identifier of the course. (required)
	 * @param lesson The unique identifier of the lesson to retrieve. (required)
	 * @return Successfully retrieved lesson details. (status code 200) or Bad Request. The request is invalid or cannot
	 * be processed. (status code 400) or Access unauthorized. The request requires valid authentication credentials
	 * (e.g., a valid token). (status code 401) or Internal Server Error. Something went wrong on the server. (status
	 * code 500)
	 */
	@Override
	public ResponseEntity<UserLessonDto> userCourseLessonGet(Integer course, Integer lesson) {
		UserLessonDto userLessonDto = getUserLessonByIdAndCourseIdUseCase.execute(course, lesson);
		return ResponseEntity.ok(userLessonDto);
	}

	/**
	 * POST /user/course/subscribe : Subscribe user to a course Enrolls the authenticated user into a specific Java
	 * course using its ID.
	 *
	 * @param userCourseSubscribePostRequest (required)
	 * @return Successfully subscribed to the course. (status code 201) or Bad Request. The request is invalid or cannot
	 * be processed. (status code 400) or Access unauthorized. The request requires valid authentication credentials
	 * (e.g., a valid token). (status code 401) or Internal Server Error. Something went wrong on the server. (status
	 * code 500)
	 */
	@Override
	public ResponseEntity<Void> userCourseSubscribePost(UserCourseSubscribePostRequest userCourseSubscribePostRequest) {
		subscribeToCourseUseCase.execute(userCourseSubscribePostRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * GET /user/courses : Get user courses Fetches the user&#39;s personal course collection. Supports filtering by
	 * category or status, pagination, and custom sorting.
	 *
	 * @param paging       Pagination and sorting criteria. (required)
	 * @param categories   Filter by programming languages or categories (e.g., &#39;java&#39;, &#39;python&#39;).
	 *                     (optional)
	 * @param courseStatus Filter by course progress status. (optional)
	 * @return A paginated list of courses was successfully retrieved. (status code 200) or Bad Request. The request is
	 * invalid or cannot be processed. (status code 400) or Access unauthorized. The request requires valid
	 * authentication credentials (e.g., a valid token). (status code 401) or Internal Server Error. Something went
	 * wrong on the server. (status code 500)
	 */
	@Override
	public ResponseEntity<PaginatedCourseResponseWrapper> userCoursesGet(
		PaginationData paging,
		@Nullable List<String> categories,
		@Nullable Set<ProgressStatusEnum> courseStatus
	) {
		return ResponseEntity.ok(findUserCoursesUseCase.execute(paging, courseStatus, categories));
	}

	/**
	 * GET /user/courses/unsubscribed : Get available courses Fetches courses that are available for the user to enroll
	 * in. Supports filtering, pagination, and custom sorting.
	 *
	 * @param paging     Pagination and sorting criteria. (required)
	 * @param categories Filter by category languages. (optional)
	 * @return A paginated list of courses was successfully retrieved. (status code 200) or Access unauthorized. The
	 * request requires valid authentication credentials (e.g., a valid token). (status code 401) or Internal Server
	 * Error. Something went wrong on the server. (status code 500)
	 */
	@Override
	public ResponseEntity<PaginatedCourseResponseWrapper> userCoursesUnsubscribedGet(PaginationData paging, @Nullable List<String> categories) {
		var findNotSubscribedCoursesCommand = new FindNotSubscribedCoursesCommand(paging, categories);
		return ResponseEntity.ok(findNotSubscribedCoursesUseCase.execute(findNotSubscribedCoursesCommand));
	}

	/**
	 * GET /user/recommendations : Get course recommendations for the current user Returns a personalized list of
	 * courses based on the user&#39;s interests, past enrollments, and browsing history.
	 *
	 * @param paging Pagination and sorting criteria (page, size, order, direction). (required)
	 * @return A paginated list of courses was successfully retrieved. (status code 200) or Access unauthorized. The
	 * request requires valid authentication credentials (e.g., a valid token). (status code 401) or Internal Server
	 * Error. Something went wrong on the server. (status code 500)
	 */
	@Override
	public ResponseEntity<PaginatedCourseResponseWrapper> userRecommendationsGet(PaginationData paging) {
		return ResponseEntity.ok(findUserRecommendationUseCase.execute(paging));
	}
}
