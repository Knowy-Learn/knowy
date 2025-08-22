package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.PrivateUserEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ConditionalOnMissingBean(
	value = JpaRepository.class,
	parameterizedContainer = {PrivateUserEntity.class, Integer.class}
)
public interface JpaUserPrivateDao extends JpaRepository<PrivateUserEntity, Integer> {
	Optional<PrivateUserEntity> findByEmail(String email);

	@NonNull
	<S extends PrivateUserEntity> S save(@NonNull S user);

	Optional<PrivateUserEntity> findById(int id);
}