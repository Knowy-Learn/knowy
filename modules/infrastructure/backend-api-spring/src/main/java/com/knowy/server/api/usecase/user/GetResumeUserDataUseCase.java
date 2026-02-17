package com.knowy.server.api.usecase.user;

import com.knowy.core.CourseService;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.course.FindCoursesWithProgressResult;
import com.knowy.core.user.domain.User;
import com.knowy.server.api.controller.exception.KnowyUnauthorizedRuntimeException;
import com.knowy.server.api.dto.GenderEnum;
import com.knowy.server.api.dto.UserResumeGet200Response;
import com.knowy.server.api.util.SecurityHelper;

import java.util.List;

/**
 * Use case responsible for retrieving a summarized view of the authenticated user's data.
 * <p>
 * This class aggregates user profile information with courses progress data calculated across all enrolled courses to
 * provide a "resume" or dashboard-level overview.
 */
public class GetResumeUserDataUseCase {

	private final CourseService courseService;

	/**
	 * Constructs a new {@code GetResumeUserDataUseCase} by initializing the required {@code CourseService}.
	 *
	 * @param courseRepository     the repository for accessing course data.
	 * @param lessonRepository     the repository for accessing lesson data.
	 * @param userLessonRepository the repository for tracking user-specific lesson progress.
	 */
	public GetResumeUserDataUseCase(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.courseService = new CourseService(
			courseRepository,
			lessonRepository,
			userLessonRepository,
			userCourseRepository
		);
	}

	/**
	 * Executes the logic to fetch and calculate the resume data for the currently authenticated user.
	 *
	 * @return a {@link UserResumeGet200Response} containing the username, gender, total courses, and overall average
	 * progress.
	 * @throws KnowyUnauthorizedRuntimeException     if no authenticated user is found in the security context.
	 * @throws KnowyInconsistentDataException if there is a mismatch or error in the retrieved progress data.
	 */
	public UserResumeGet200Response execute() throws KnowyInconsistentDataException {
		User user = new SecurityHelper().getAuthenticatedUser();
		List<FindCoursesWithProgressResult> values = courseService.getAllCourseProgress(user.id());

		getAverageProgress(values);

		return new UserResumeGet200Response()
			.username(user.nickname())
			.gender(GenderEnum.fromValue(user.gender().toString().toLowerCase()))
			.totalCourses(values.size())
			.completedCourses((float) getAverageProgress(values));
	}

	private double getAverageProgress(List<FindCoursesWithProgressResult> courses) {
		return courses.stream()
			.mapToDouble(FindCoursesWithProgressResult::progress)
			.average()
			.orElse(0.0);
	}
}
