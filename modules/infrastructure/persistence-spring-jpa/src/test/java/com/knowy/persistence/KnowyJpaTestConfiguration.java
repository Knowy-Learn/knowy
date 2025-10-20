package com.knowy.persistence;

import com.knowy.core.port.CourseRepository;
import com.knowy.persistence.adapter.jpa.JpaCourseRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaCourseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserLessonDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaCourseMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("com.knowy.persistence.adapter.jpa.mapper")
@EntityScan("com.knowy.persistence.adapter.jpa.entity")
@EnableJpaRepositories("com.knowy.persistence.adapter.jpa.dao")
public class KnowyJpaTestConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public CourseRepository jpaCourseRepository(
		JpaCourseDao jpaCourseDao, JpaCourseMapper jpaCourseMapper, JpaUserLessonDao jpaUserLessonDao
	) {
		return new JpaCourseRepository(jpaCourseDao, jpaUserLessonDao, jpaCourseMapper);
	}
}
