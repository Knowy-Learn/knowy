package com.knowy.persistence;

import com.knowy.core.port.*;
import com.knowy.core.user.port.ProfileImageRepository;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.port.UserRepository;
import com.knowy.persistence.adapter.jpa.*;
import com.knowy.persistence.adapter.jpa.dao.*;
import com.knowy.persistence.adapter.jpa.mapper.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@ComponentScan("com.knowy.persistence.adapter.jpa.mapper")
@EntityScan("com.knowy.persistence.adapter.jpa.entity")
@EnableJpaRepositories("com.knowy.persistence.adapter.jpa.dao")
public class KnowyJpaAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public BannedWordsRepository jpaBannedWordsRepository(JpaBannedWordsDao jpaBannedWordsDao) {
		return new JpaBannedWordsRepository(jpaBannedWordsDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public CategoryRepository jpaCategoryRepository(JpaCategoryDao jpaCategoryDao,
													JpaCategoryMapper jpaCategoryMapper) {
		return new JpaCategoryRepository(jpaCategoryDao, jpaCategoryMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public CourseRepository jpaCourseRepository(
		JpaCourseDao jpaCourseDao, JpaUserLessonDao jpaUserLessonDao, JpaCourseMapper jpaCourseMapper
	) {
		return new JpaCourseRepository(jpaCourseDao, jpaUserLessonDao, jpaCourseMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public ExerciseRepository jpaExerciseRepository(JpaExerciseDao jpaExerciseDao,
													JpaExerciseMapper jpaExerciseMapper) {
		return new JpaExerciseRepository(jpaExerciseDao, jpaExerciseMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public LessonRepository jpaLessonRepository(
		JpaLessonDao jpaLessonDao, JpaUserLessonDao jpaUserLessonDao, JpaLessonMapper jpaLessonMapper
	) {
		return new JpaLessonRepository(jpaLessonDao, jpaUserLessonDao, jpaLessonMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public ProfileImageRepository jpaProfileImageRepository(
		JpaProfileImageDao jpaProfileImageDao, JpaProfileImageMapper jpaProfileImageMapper
	) {
		return new JpaProfileImageRepository(jpaProfileImageDao, jpaProfileImageMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public UserExerciseRepository jpaUserExerciseRepository(JpaUserExerciseDao jpaUserExerciseDao,
															JpaUserExerciseMapper jpaUserExerciseMapper) {
		return new JpaUserExerciseRepository(jpaUserExerciseDao, jpaUserExerciseMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public UserLessonRepository jpaUserLessonRepository(
		JpaUserLessonDao jpaUserLessonDao, JpaUserLessonMapper jpaUserLessonMapper
	) {
		return new JpaUserLessonRepository(jpaUserLessonDao, jpaUserLessonMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public UserPrivateRepository jpaUserPrivateRepository(
		JpaUserPrivateDao jpaUserPrivateDao, JpaUserPrivateMapper jpaUserPrivateMapper
	) {
		return new JpaUserPrivateRepository(jpaUserPrivateDao, jpaUserPrivateMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public UserRepository jpaUserRepository(JpaUserDao jpaUserDao, JpaUserMapper jpaUserMapper) {
		return new JpaUserRepository(jpaUserDao, jpaUserMapper);
	}

}

