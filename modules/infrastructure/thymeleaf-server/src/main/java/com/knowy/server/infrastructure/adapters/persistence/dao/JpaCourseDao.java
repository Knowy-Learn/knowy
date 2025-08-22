package com.knowy.server.infrastructure.adapters.persistence.dao;

import com.knowy.core.domain.Course;
import com.knowy.server.infrastructure.adapters.persistence.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface JpaCourseDao extends JpaRepository<CourseEntity, Integer> {
	@NonNull
	List<CourseEntity> findAll();

	@Query("SELECT c FROM CourseEntity c ORDER BY function('RANDOM')")
	Stream<CourseEntity> findAllRandom();

	@Query("""
		SELECT DISTINCT c
		FROM CourseEntity c
		    JOIN c.languages lang
		WHERE lang.id IN (:categoriesIds)
		ORDER BY function('RANDOM')
		""")
	Stream<Course> findByCategoryIdsInRandomOrder(List<Integer> categoriesIds);
}

