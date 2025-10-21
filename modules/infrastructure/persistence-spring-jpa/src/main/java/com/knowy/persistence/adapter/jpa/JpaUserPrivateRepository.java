package com.knowy.persistence.adapter.jpa;

import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserPrivateDao;
import com.knowy.persistence.adapter.jpa.entity.PrivateUserEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaCategoryMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaProfileImageMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserPrivateMapper;

import java.util.Optional;

public class JpaUserPrivateRepository implements UserPrivateRepository {

	private final JpaUserPrivateDao jpaUserPrivateDao;
	private final JpaCategoryDao jpaCategoryDao;

	public JpaUserPrivateRepository(JpaUserPrivateDao jpaUserPrivateDao, JpaCategoryDao jpaCategoryDao) {
		this.jpaUserPrivateDao = jpaUserPrivateDao;
		this.jpaCategoryDao = jpaCategoryDao;
	}

	@Override
	public Optional<UserPrivate> findById(int id) {
		JpaUserPrivateMapper jpaUserPrivateMapper = getJpaUserPrivateMapper();

		return jpaUserPrivateDao.findById(id).map(jpaUserPrivateMapper::toDomain);
	}

	@Override
	public Optional<UserPrivate> findByEmail(String email) {
		JpaUserPrivateMapper jpaUserPrivateMapper = getJpaUserPrivateMapper();

		return jpaUserPrivateDao.findByEmail(email).map(jpaUserPrivateMapper::toDomain);
	}

	@Override
	public UserPrivate save(UserPrivate user) {
		JpaUserPrivateMapper jpaUserPrivateMapper = getJpaUserPrivateMapper();

		PrivateUserEntity privateUserEntity = jpaUserPrivateDao.save(jpaUserPrivateMapper.toEntity(user));
		return jpaUserPrivateMapper.toDomain(privateUserEntity);
	}

	private JpaUserPrivateMapper getJpaUserPrivateMapper() {
		return new JpaUserPrivateMapper(
			new JpaUserMapper(
				new JpaCategoryMapper(jpaCategoryDao),
				new JpaProfileImageMapper()
			)
		);
	}
}
