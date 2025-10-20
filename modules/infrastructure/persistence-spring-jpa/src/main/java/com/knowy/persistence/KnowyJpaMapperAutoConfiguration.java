package com.knowy.persistence;

import com.knowy.persistence.adapter.jpa.dao.*;
import com.knowy.persistence.adapter.jpa.mapper.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class KnowyJpaMapperAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public JpaCategoryMapper jpaCategoryMapper(JpaCategoryDao jpaCategoryDao) {
		return new JpaCategoryMapper(jpaCategoryDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaCourseMapper jpaCourseMapper(JpaCategoryMapper jpaCategoryMapper, JpaLessonMapper jpaLessonMapper) {
		return new JpaCourseMapper(jpaCategoryMapper, jpaLessonMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaDocumentationMapper jpaDocumentationMapper(JpaLessonDao jpaLessonDao) {
		return new JpaDocumentationMapper(jpaLessonDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaExerciseMapper jpaExerciseMapper(JpaOptionMapper jpaOptionMapper, JpaLessonDao jpaLessonDao) {
		return new JpaExerciseMapper(jpaOptionMapper, jpaLessonDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaLessonBaseMapper jpaLessonBaseMapper() {
		return new JpaLessonBaseMapper();
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaLessonMapper jpaLessonMapper(
		JpaDocumentationMapper jpaDocumentationMapper,
		JpaExerciseMapper jpaExerciseMapper,
		JpaCourseDao jpaCourseDao,
		JpaLessonDao jpaLessonDao
	) {
		return new JpaLessonMapper(jpaDocumentationMapper, jpaExerciseMapper, jpaCourseDao, jpaLessonDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaOptionMapper jpaOptionMapper(JpaExerciseDao jpaExerciseDao) {
		return new JpaOptionMapper(jpaExerciseDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaProfileImageMapper jpaProfileImageMapper() {
		return new JpaProfileImageMapper();
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaUserExerciseMapper jpaUserExerciseMapper(
		JpaUserDao jpaUserDao, JpaExerciseDao jpaExerciseDao, JpaExerciseMapper jpaExerciseMapper
	) {
		return new JpaUserExerciseMapper(jpaUserDao, jpaExerciseDao, jpaExerciseMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaUserLessonMapper jpaUserLessonMapper(
		JpaLessonMapper jpaLessonMapper, JpaUserDao jpaUserDao, JpaLessonDao jpaLessonDao) {
		return new JpaUserLessonMapper(jpaLessonMapper, jpaUserDao, jpaLessonDao);
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaUserMapper jpaUserMapper(JpaCategoryMapper jpaCategoryMapper, JpaProfileImageMapper jpaProfileImageMapper) {
		return new JpaUserMapper(jpaCategoryMapper, jpaProfileImageMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaUserPrivateMapper jpaUserPrivateMapper(JpaUserMapper jpaUserMapper) {
		return new JpaUserPrivateMapper(jpaUserMapper);
	}
}
