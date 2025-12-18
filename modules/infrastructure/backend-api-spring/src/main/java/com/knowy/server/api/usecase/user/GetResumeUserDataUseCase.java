package com.knowy.server.api.usecase.user;

import com.knowy.core.CourseService;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.usecase.course.GetAllCoursesWithProgressResult;
import com.knowy.core.user.domain.User;
import com.knowy.server.api.controller.exception.KnowyUnauthorizedException;
import com.knowy.server.api.dto.GenderEnum;
import com.knowy.server.api.dto.UserResumeGet200Response;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class GetResumeUserDataUseCase {

	private final CourseService courseService;

	public GetResumeUserDataUseCase(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository
	) {
		this.courseService = new CourseService(courseRepository, lessonRepository, userLessonRepository);
	}

	// TODO:
	public UserResumeGet200Response execute() throws KnowyInconsistentDataException {

		User user = getUserByAuthentication();
		List<GetAllCoursesWithProgressResult> values = courseService.getAllCourseProgress(user.id());

		getTotalProgress(values);

		return new UserResumeGet200Response()
			.username(user.nickname())
			.gender(GenderEnum.fromValue(user.gender().toString().toLowerCase()))
			.totalCourses(values.size())
			.completedCourses((float) getTotalProgress(values));
	}

	private User getUserByAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();

		if (!(principal instanceof User user)) {
			String principalClass = principal != null ? principal.getClass().getName() : "null";
			throw new KnowyUnauthorizedException("Expected principal of type User, but got: " + principalClass);
		}

		return user;
	}

	private double getTotalProgress(List<GetAllCoursesWithProgressResult> courses) {
		return courses.stream()
			.mapToDouble(GetAllCoursesWithProgressResult::progress)
			.average()
			.orElse(0.0);
	}
}
