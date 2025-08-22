package com.knowy.persistence.adapter.jpa;

import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaUserPrivateDao;
import com.knowy.persistence.adapter.jpa.entity.PrivateUserEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserPrivateMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserPrivateRepository implements UserPrivateRepository {

	private final JpaUserPrivateDao jpaUserPrivateDao;
	private final JpaUserPrivateMapper jpaUserPrivateMapper;

	public JpaUserPrivateRepository(JpaUserPrivateDao jpaUserPrivateDao, JpaUserPrivateMapper jpaUserPrivateMapper) {
		this.jpaUserPrivateDao = jpaUserPrivateDao;
		this.jpaUserPrivateMapper = jpaUserPrivateMapper;
	}

	@Override
	public Optional<UserPrivate> findById(int id) {
		return jpaUserPrivateDao.findById(id).map(jpaUserPrivateMapper::toDomain);
	}

	@Override
	public Optional<UserPrivate> findByEmail(String email) {
		return jpaUserPrivateDao.findByEmail(email).map(jpaUserPrivateMapper::toDomain);
	}

	@Override
	public UserPrivate save(UserPrivate user) {
		PrivateUserEntity privateUserEntity = jpaUserPrivateDao.save(jpaUserPrivateMapper.toEntity(user));
		return jpaUserPrivateMapper.toDomain(privateUserEntity);
	}

}
