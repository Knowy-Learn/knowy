package com.knowy.server.infrastructure.config;

import com.knowy.core.CategoryService;
import com.knowy.core.CourseService;
import com.knowy.core.UserExerciseService;
import com.knowy.core.UserLessonService;
import com.knowy.core.port.*;
import com.knowy.core.user.UserPrivateService;
import com.knowy.core.user.UserService;
import com.knowy.core.user.port.*;
import com.knowy.core.user.usercase.manage.DeactivateAccountUseCase;
import com.knowy.core.user.usercase.register.UserSignUpUseCase;
import com.knowy.core.user.usercase.update.email.UserUpdateEmailUseCase;
import com.knowy.core.user.usercase.update.password.UserUpdatePasswordUseCase;
import com.knowy.core.user.util.TokenUserPrivateTool;
import com.knowy.security.adapter.jwt.PasswordEncoderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public UserSignUpUseCase userSignUpUseCase(
		UserRepository userRepository,
		UserPrivateRepository privateUserRepository,
		KnowyPasswordEncoder knowyPasswordEncoder,
		ProfileImageRepository profileImageRepository
	) {
		return new UserSignUpUseCase(
			userRepository,
			privateUserRepository,
			knowyPasswordEncoder,
			profileImageRepository
		);
	}

	@Bean
	public UserUpdateEmailUseCase userUpdateEmailUseCase(
		UserPrivateRepository userPrivateRepository,
		PasswordEncoderAdapter passwordEncoderAdapter
	) {
		return new UserUpdateEmailUseCase(userPrivateRepository, passwordEncoderAdapter);
	}

	@Bean
	public UserUpdatePasswordUseCase userUpdatePasswordUseCase(
		UserPrivateRepository userPrivateRepository,
		PasswordEncoderAdapter passwordEncoderAdapter,
		KnowyTokenTools knowyTokenTools
	) {
		return new UserUpdatePasswordUseCase(
			userPrivateRepository,
			passwordEncoderAdapter,
			knowyTokenTools
		);
	}

	@Bean
	public DeactivateAccountUseCase userRecoveryAccountUseCase(
		TokenUserPrivateTool tokenUserPrivateTool,
		KnowyNotificationDispatcher knowyNotificationDispatcher,
		PasswordEncoderAdapter passwordEncoderAdapter,
		UserPrivateRepository userPrivateRepository
	) {
		return new DeactivateAccountUseCase(
			tokenUserPrivateTool, knowyNotificationDispatcher, passwordEncoderAdapter, userPrivateRepository
		);
	}

	@Bean
	public UserExerciseService exerciseService(
		UserExerciseRepository userExerciseRepository,
		UserRepository userRepository,
		ExerciseRepository exerciseRepository
	) {
		return new UserExerciseService(userExerciseRepository, exerciseRepository);
	}

	@Bean
	public UserLessonService userLessonService(
		UserLessonRepository userLessonRepository,
		LessonRepository lessonRepository
	) {
		return new UserLessonService(userLessonRepository, lessonRepository);
	}

	@Bean
	public UserService userService(
		UserRepository userRepository,
		CategoryRepository categoryRepository,
		ProfileImageRepository profileImageRepository
	) {
		return new UserService(userRepository, categoryRepository, profileImageRepository);
	}

	@Bean
	public UserPrivateService userPrivateService(
		UserRepository userRepository,
		UserPrivateRepository userPrivateRepository,
		ProfileImageRepository profileImageRepository,
		KnowyPasswordEncoder knowyPasswordEncoder,
		KnowyTokenTools knowyTokenTools,
		KnowyNotificationDispatcher knowyNotificationDispatcher
	) {
		return new UserPrivateService(
			userRepository,
			userPrivateRepository,
			profileImageRepository,
			knowyPasswordEncoder,
			knowyTokenTools,
			knowyNotificationDispatcher
		);
	}

	@Bean
	public TokenUserPrivateTool tokenUserPrivateTool(KnowyTokenTools knowyTokenTools, UserPrivateRepository userPrivateRepository) {
		return new TokenUserPrivateTool(knowyTokenTools, userPrivateRepository);
	}

	@Bean
	public CategoryService categoryService(CategoryRepository categoryRepository) {
		return new CategoryService(categoryRepository);
	}

	@Bean
	public CourseService courseService(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		CategoryRepository categoryRepository
	) {
		return new CourseService(courseRepository, lessonRepository, userLessonRepository, categoryRepository);
	}
}
