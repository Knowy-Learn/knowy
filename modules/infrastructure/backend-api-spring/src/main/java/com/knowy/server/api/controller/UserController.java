package com.knowy.server.api.controller;

import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorException;
import com.knowy.server.api.dto.*;
import com.knowy.server.api.usecase.user.*;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class UserController implements UserApi {

	private final GetNavbarUserDataUseCase getNavbarUserDataUseCase;
	private final GetResumeUserDataUseCase getResumeUserDataUseCase;
	private final FindUserCoursesUseCase findUserCoursesUseCase;
	private final FindUserRecommendationUseCase findUserRecommendationUseCase;
	private final FindNotSubscribedCoursesUseCase findNotSubscribedCoursesUseCase;

	public UserController(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.findUserRecommendationUseCase = new FindUserRecommendationUseCase(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
		this.getNavbarUserDataUseCase = new GetNavbarUserDataUseCase();
		this.getResumeUserDataUseCase = new GetResumeUserDataUseCase(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
		this.findUserCoursesUseCase = new FindUserCoursesUseCase(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
		this.findNotSubscribedCoursesUseCase = new FindNotSubscribedCoursesUseCase(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
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
	public ResponseEntity<PaginatedCourseResponseWrapper> userCoursesGet(PaginationData paging, @Nullable List<String> categories, @Nullable Set<CourseStatusEnum> courseStatus) {
		return ResponseEntity.ok(findUserCoursesUseCase.execute(paging, courseStatus, categories));
	}

	/**
	 * GET /user/navbar : Get navbar user data Retrieve the data needed to display the user&#39;s navbar, such as name,
	 * avatar, and notifications.
	 *
	 * @return Navbar data retrieved successfully (status code 200) or Access unauthorized. The request requires valid
	 * authentication credentials (e.g., a valid token). (status code 401)
	 */
	@Override
	public ResponseEntity<UserNavbarGet200Response> userNavbarGet() {
		return ResponseEntity.ok(getNavbarUserDataUseCase.execute());
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
	 * @return A list of recommended courses (status code 200) or Access unauthorized. The request requires valid
	 * authentication credentials (e.g., a valid token). (status code 401) or Internal Server Error. Something went
	 * wrong on the server. (status code 500)
	 */
	@Override
	public ResponseEntity<PaginatedCourseResponseWrapper> userRecommendationsGet(PaginationData paginationData) {
		return ResponseEntity.ok(findUserRecommendationUseCase.execute(paginationData));
	}

	/**
	 * GET /user/resume : Get resume user data Retrieve a summary of the user&#39;s profile, including basic information
	 * and course progress.
	 *
	 * @return Resume data retrieved successfully (status code 200) or Access unauthorized. The request requires valid
	 * authentication credentials (e.g., a valid token). (status code 401)
	 */
	@Override
	public ResponseEntity<UserResumeGet200Response> userResumeGet() {
		try {
			return ResponseEntity.ok(getResumeUserDataUseCase.execute());
		} catch (KnowyInconsistentDataException e) {
			throw new KnowyInternalServerErrorException("Data inconsistency detected while fetching user resume", e);
		}
	}
}
