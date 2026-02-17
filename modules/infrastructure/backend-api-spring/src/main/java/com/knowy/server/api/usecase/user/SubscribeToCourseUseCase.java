package com.knowy.server.api.usecase.user;

import com.knowy.core.CourseService;
import com.knowy.core.exception.KnowyCourseSubscriptionException;
import com.knowy.core.exception.KnowyException;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.user.domain.User;
import com.knowy.core.util.KnowyUseCase;
import com.knowy.server.api.controller.exception.KnowyBadRequestRuntimeException;
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorException;
import com.knowy.server.api.dto.UserCourseSubscribePostRequest;
import com.knowy.server.api.util.SecurityHelper;

public class SubscribeToCourseUseCase implements KnowyUseCase<UserCourseSubscribePostRequest, Void> {

	private final CourseService courseService;

	public SubscribeToCourseUseCase(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.courseService = new CourseService(
			courseRepository, lessonRepository, userLessonRepository, userCourseRepository
		);
	}

	@Override
	public Void execute(UserCourseSubscribePostRequest param) throws KnowyException {
		User user = new SecurityHelper().getAuthenticatedUser();

		try {
			courseService.subscribeUserToCourse(user.id(), param.getCourseId());
			return null;
		} catch (KnowyCourseSubscriptionException e) {
			throw new KnowyBadRequestRuntimeException(
				"Unable to complete subscription. Please verify if the user is already enrolled", e
			);
		} catch (KnowyInconsistentDataException e) {
			throw new KnowyInternalServerErrorException(
				"An unexpected internal error occurred while processing the enrollment. Please try again later.", e
			);
		}
	}
}
