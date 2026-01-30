package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.core.domain.CourseIdentifiedInfo;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonIdEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaUserLessonDao extends JpaRepository<PublicUserLessonEntity, PublicUserLessonIdEntity> {

	boolean existsByUserIdAndLessonId(Integer userId, Integer lessonId);

	@Query("""
		SELECT DISTINCT l.course.id
		FROM PublicUserLessonEntity pul
		JOIN LessonEntity l ON pul.lessonId= l.id
		WHERE pul.userId = :userId
		""")
	List<Integer> findCourseIdsByUserId(@Param("userId") Integer userId);

	boolean existsById(@NonNull PublicUserLessonIdEntity id);

	@NonNull
	<S extends PublicUserLessonEntity> S save(@NonNull S entity);

	@Query("""
		SELECT COUNT(pul)
		FROM PublicUserLessonEntity pul
		    JOIN LessonEntity l
		        ON pul.lessonId = l.id
		WHERE pul.userId = :userId AND l.course.id = :courseId AND pul.status = :status
		""")
	int countByUserIdAndCourseIdAndStatus(
		@Param("userId") Integer userId,
		@Param("courseId") Integer courseId,
		@Param("status") String status
	);

	@NonNull
	Optional<PublicUserLessonEntity> findById(@NonNull PublicUserLessonIdEntity publicUserLessonIdEntity);

	Set<PublicUserLessonEntity> findByUserId(int userId);

	@Query("""
		SELECT pul
		FROM PublicUserLessonEntity pul
		    JOIN pul.lessonEntity l
		WHERE pul.userId = :userId
		""")
	Page<PublicUserLessonEntity> findAllByUserId(@Param("userId") int userId, Pageable pageable);

	@Query("""
		SELECT pul
		FROM PublicUserLessonEntity pul
		    JOIN pul.lessonEntity l
		WHERE pul.userId = :userId
		    AND l.course.id = :courseId
		""")
	List<PublicUserLessonEntity> findAllByUserIdAndCourseId(@Param("userId") int userId, @Param("courseId") int courseId);

	@Query("""
		SELECT pul
		FROM PublicUserLessonEntity pul
		    JOIN pul.lessonEntity l
		WHERE pul.userId = :userId
		""")
	List<PublicUserLessonEntity> findAllWhereUserIsSubscribed(@Param("userId") int userId);

	@Query("""
		SELECT
		    new com.knowy.persistence.adapter.jpa.dao.JpaUserLessonDao$UserLessonCourseInfo(c,pul)
		FROM CourseEntity c
		    LEFT JOIN c.lessons l
		    LEFT JOIN PublicUserLessonEntity pul
		        ON pul.lessonEntity.id = l.id
		    LEFT JOIN FETCH c.languages
		WHERE c.id IN :coursesId
		""")
	List<UserLessonCourseInfo> findAllWithCourseInfoByCoursesId(@Param("coursesId") List<Integer> coursesId);

	record UserLessonCourseInfo(CourseEntity courseEntity, PublicUserLessonEntity userLessonEntity) {
	}
}
