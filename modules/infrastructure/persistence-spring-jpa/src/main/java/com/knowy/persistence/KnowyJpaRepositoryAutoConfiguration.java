package com.knowy.persistence;

import com.knowy.core.port.*;
import com.knowy.core.user.port.ProfileImageRepository;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.port.UserRepository;
import com.knowy.persistence.adapter.jpa.*;
import com.knowy.persistence.adapter.jpa.dao.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@EntityScan("com.knowy.persistence.adapter.jpa.entity")
@EnableJpaRepositories("com.knowy.persistence.adapter.jpa.dao")
public class KnowyJpaRepositoryAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public BannedWordsRepository jpaBannedWordsRepository(JpaBannedWordsDao jpaBannedWordsDao) {
		return new JpaBannedWordsRepository(jpaBannedWordsDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public CategoryRepository jpaCategoryRepository(JpaCategoryDao jpaCategoryDao) {
		return new JpaCategoryRepository(jpaCategoryDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public CourseRepository jpaCourseRepository(
		JpaCourseDao jpaCourseDao,
		JpaUserLessonDao jpaUserLessonDao,
		JpaCategoryDao jpaCategoryDao,
		JpaLessonDao jpaLessonDao,
		JpaExerciseDao jpaExerciseDao
	) {
		return new JpaCourseRepository(jpaCourseDao, jpaUserLessonDao, jpaCategoryDao, jpaLessonDao, jpaExerciseDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public ExerciseRepository jpaExerciseRepository(JpaExerciseDao jpaExerciseDao, JpaLessonDao jpaLessonDao) {
		return new JpaExerciseRepository(jpaExerciseDao, jpaLessonDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public LessonRepository jpaLessonRepository(
		JpaLessonDao jpaLessonDao, JpaUserLessonDao jpaUserLessonDao, JpaExerciseDao jpaExerciseDao, JpaCourseDao jpaCourseDao
	) {
		return new JpaLessonRepository(jpaLessonDao, jpaUserLessonDao, jpaExerciseDao, jpaCourseDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public LessonBaseRepository jpaLessonBaseRepository(
		JpaLessonDao jpaLessonDao
	) {
		return new JpaLessonBaseRepository(jpaLessonDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public ProfileImageRepository jpaProfileImageRepository(JpaProfileImageDao jpaProfileImageDao) {
		return new JpaProfileImageRepository(jpaProfileImageDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public UserExerciseRepository jpaUserExerciseRepository(
		JpaUserExerciseDao jpaUserExerciseDao,
		JpaUserDao jpaUserDao,
		JpaExerciseDao jpaExerciseDao,
		JpaLessonDao jpaLessonDao
	) {
		return new JpaUserExerciseRepository(jpaUserExerciseDao, jpaUserDao, jpaExerciseDao, jpaLessonDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public UserLessonRepository jpaUserLessonRepository(
		JpaUserLessonDao jpaUserLessonDao,
		JpaLessonDao jpaLessonDao,
		JpaExerciseDao jpaExerciseDao,
		JpaCourseDao jpaCourseDao, JpaUserDao jpaUserDao
	) {
		return new JpaUserLessonRepository(jpaUserLessonDao, jpaLessonDao, jpaExerciseDao, jpaCourseDao, jpaUserDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public UserPrivateRepository jpaUserPrivateRepository(
		JpaUserPrivateDao jpaUserPrivateDao, JpaCategoryDao jpaCategoryDao
	) {
		return new JpaUserPrivateRepository(jpaUserPrivateDao, jpaCategoryDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public UserRepository jpaUserRepository(JpaUserDao jpaUserDao, JpaCategoryDao jpaCategoryDao) {
		return new JpaUserRepository(jpaUserDao, jpaCategoryDao);
	}
}

