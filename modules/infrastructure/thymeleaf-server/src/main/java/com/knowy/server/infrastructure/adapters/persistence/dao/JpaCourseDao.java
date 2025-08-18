package com.knowy.server.infrastructure.adapters.persistence.dao;

import com.knowy.server.infrastructure.adapters.persistence.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaCourseDao extends JpaRepository<CourseEntity, Integer> {
	@NonNull
	List<CourseEntity> findAll();

	@Query("SELECT c FROM CourseEntity c ORDER BY function('RANDOM')")
	List<CourseEntity> findAllRandom();

	@Query("""
		SELECT distinct c
		FROM CourseEntity c
		    LEFT JOIN c.lessons l
		    LEFT JOIN l.publicUserLessons pul with pul.userId = :userId
		WHERE pul.id IS null
		ORDER by c.id
		""")
	List<CourseEntity> findAllRandomUserIsNotSubscribed(@Param("userId") int userId);
}

