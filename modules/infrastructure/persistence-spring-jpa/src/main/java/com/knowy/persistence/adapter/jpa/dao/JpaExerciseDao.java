package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.ExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaExerciseDao extends JpaRepository<ExerciseEntity, Integer> {
	Optional<ExerciseEntity> findById(int id);
}
