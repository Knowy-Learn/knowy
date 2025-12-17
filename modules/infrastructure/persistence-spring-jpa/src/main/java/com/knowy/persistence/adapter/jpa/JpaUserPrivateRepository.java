package com.knowy.persistence.adapter.jpa;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.dao.JpaGenderDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserPrivateDao;
import com.knowy.persistence.adapter.jpa.entity.PrivateUserEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserPrivateMapper;

import java.util.Optional;

public class JpaUserPrivateRepository implements UserPrivateRepository {

	private final JpaUserPrivateDao jpaUserPrivateDao;
	private final JpaCategoryDao jpaCategoryDao;
	private final JpaGenderDao jpaGenderDao;

	public JpaUserPrivateRepository(JpaUserPrivateDao jpaUserPrivateDao, JpaCategoryDao jpaCategoryDao, JpaGenderDao jpaGenderDao) {
		this.jpaUserPrivateDao = jpaUserPrivateDao;
		this.jpaCategoryDao = jpaCategoryDao;
		this.jpaGenderDao = jpaGenderDao;
	}

	@Override
	public Optional<UserPrivate> findById(int id) throws KnowyDataAccessException {
		JpaUserPrivateMapper jpaUserPrivateMapper = getJpaUserPrivateMapper();

		try {
			Optional<PrivateUserEntity> privateUserEntity = jpaUserPrivateDao.findById(id);

			if (privateUserEntity.isEmpty()) {
				return Optional.empty();
			}

			UserPrivate userPrivate = jpaUserPrivateMapper.toDomain(privateUserEntity.get());
			return Optional.of(userPrivate);

		} catch (KnowyInvalidUserGenderException e) {
			throw new KnowyDataAccessException(e);
		}
	}

	@Override
	public Optional<UserPrivate> findByEmail(String email) throws KnowyDataAccessException {
		JpaUserPrivateMapper jpaUserPrivateMapper = getJpaUserPrivateMapper();

		try {
			Optional<PrivateUserEntity> privateUserEntity = jpaUserPrivateDao.findByEmail(email);

			if (privateUserEntity.isEmpty()) {
				return Optional.empty();
			}

			UserPrivate userPrivate = jpaUserPrivateMapper.toDomain(privateUserEntity.get());
			return Optional.of(userPrivate);
		} catch (KnowyInvalidUserGenderException e) {
			throw new KnowyDataAccessException(e);
		}
	}

	@Override
	public UserPrivate save(UserPrivate user) throws KnowyDataAccessException {
		JpaUserPrivateMapper jpaUserPrivateMapper = getJpaUserPrivateMapper();

		try {
			PrivateUserEntity privateUserEntity = jpaUserPrivateDao.save(jpaUserPrivateMapper.toEntity(user));
			return jpaUserPrivateMapper.toDomain(privateUserEntity);

		} catch (KnowyInvalidUserGenderException e) {
			throw new KnowyDataAccessException(e);
		}
	}

	private JpaUserPrivateMapper getJpaUserPrivateMapper() {
		return new JpaUserPrivateMapper(jpaCategoryDao, jpaGenderDao);
	}
}
