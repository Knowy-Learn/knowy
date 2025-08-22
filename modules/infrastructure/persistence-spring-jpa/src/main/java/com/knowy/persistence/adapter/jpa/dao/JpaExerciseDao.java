package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.ExerciseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ConditionalOnMissingBean(
	value = JpaRepository.class,
	parameterizedContainer = {ExerciseEntity.class, Integer.class}
)
public interface JpaExerciseDao extends JpaRepository<ExerciseEntity, Integer> {
	Optional<ExerciseEntity> findById(int id);
}
