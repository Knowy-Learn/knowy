package com.knowy.server.api.usecase.user;

import com.knowy.core.CourseService;
import com.knowy.core.LessonService;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.LessonBaseRepository;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.user.domain.User;
import com.knowy.server.api.controller.exception.KnowyBadRequestRuntimeException;
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorRuntimeException;
import com.knowy.server.api.dto.UserLessonDto;
import com.knowy.server.api.mapper.LessonMapper;
import com.knowy.server.api.mapper.ProgressStatusMapper;
import com.knowy.server.api.util.SecurityHelper;

public class GetUserLessonByIdAndCourseIdUseCase {

    private final LessonService lessonService;
	private final LessonMapper lessonMapper = new LessonMapper();
	private final ProgressStatusMapper progressStatusMapper = new ProgressStatusMapper();

	public GetUserLessonByIdAndCourseIdUseCase(
		UserLessonRepository userLessonRepository,
		UserExerciseRepository userExerciseRepository,
		LessonBaseRepository lessonBaseRepository
	) {
		this.lessonService = new LessonService(userLessonRepository, userExerciseRepository, lessonBaseRepository);
	}

	public UserLessonDto execute(int courseId, int lessonId) {
		User user = new SecurityHelper().getAuthenticatedUser();

		try {
			UserLesson userLesson = lessonService.getUserLessonById(user.id(), lessonId);
			assertUserLessonHasCourseId(userLesson, courseId);

			return  new UserLessonDto(
				lessonMapper.toLessonDto(userLesson.lesson()),
				progressStatusMapper.fromDomain(userLesson.status())
			);

		} catch (KnowyInconsistentDataException e) {
			throw new KnowyInternalServerErrorRuntimeException("", e);
		}
	}

	private void assertUserLessonHasCourseId(UserLesson userLesson, int courseId) {
		if (userLesson.lesson().courseId() != courseId) {
			throw new KnowyBadRequestRuntimeException("Lesson with id " + courseId + " does not exist or not enrolled for user");
		}
	}
}
