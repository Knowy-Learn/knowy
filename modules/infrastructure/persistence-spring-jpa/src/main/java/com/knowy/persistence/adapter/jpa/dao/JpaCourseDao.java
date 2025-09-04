package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.core.domain.Course;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
		SELECT c
		FROM CourseEntity c
		    JOIN c.languages lang
		WHERE lang.id IN (:categoriesIds)
		ORDER BY function('RANDOM')
		""")
	Stream<CourseEntity> findByCategoryIdsInRandomOrder(@Param("categoriesIds") List<Integer> categoriesIds);

	@Query("SELECT l.course FROM LessonEntity l WHERE l.id = :lessonId")
	CourseEntity findCourseIdByLessonId(@Param("lessonId") int lessonId);
}

