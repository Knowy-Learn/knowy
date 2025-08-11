package com.knowy.server.infrastructure.config;

import com.knowy.server.application.*;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.ports.*;
import com.knowy.server.application.usecase.register.UserSignUpUseCase;
import com.knowy.server.application.usecase.register.UserSingUpCommand;
import com.knowy.server.domain.UserPrivate;
import com.knowy.server.infrastructure.adapters.security.PasswordEncoderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public KnowyPasswordEncoder knowyPasswordEncoder(PasswordEncoder passwordEncoder) {
		return new PasswordEncoderAdapter(passwordEncoder);
	}

	@Bean
	public UserPrivateService privateUserService(
		UserPrivateRepository privateUserRepository,
		KnowyPasswordEncoder knowyPasswordEncoder,
		KnowyTokenTools knowyTokenTools
	) {
		return new UserPrivateService(
			privateUserRepository, knowyPasswordEncoder, knowyTokenTools
		);
	}

	@Bean
	public UserFacadeService userFacadeService(
		KnowyEmailClientTool knowyEmailClientTool,
		UserPrivateService userPrivateService,
		UserService userService,
		UserSignUpUseCase userSignUpUseCase
	) {
		return new UserFacadeService(
			knowyEmailClientTool,
			userPrivateService,
			userService,
			userSignUpUseCase
		);
	}

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
	public UserExerciseService exerciseService(
		UserExerciseRepository userExerciseRepository,
		UserRepository userRepository,
		ExerciseRepository exerciseRepository
	) {
		return new UserExerciseService(userExerciseRepository, userRepository, exerciseRepository);
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
