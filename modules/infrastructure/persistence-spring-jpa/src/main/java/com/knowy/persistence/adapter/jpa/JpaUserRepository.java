package com.knowy.persistence.adapter.jpa;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.user.domain.User;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;
import com.knowy.core.user.port.UserRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.dao.JpaGenderDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserMapper;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

public class JpaUserRepository implements UserRepository {

	private final JpaUserDao jpaUserDao;
	private final JpaCategoryDao jpaCategoryDao;
	private final JpaGenderDao jpaGenderDao;

	public JpaUserRepository(JpaUserDao jpaUserDao, JpaCategoryDao jpaCategoryDao, JpaGenderDao jpaGenderDao) {
		this.jpaUserDao = jpaUserDao;
		this.jpaCategoryDao = jpaCategoryDao;
		this.jpaGenderDao = jpaGenderDao;
	}

	@Override
	public Optional<User> findById(Integer id) throws KnowyDataAccessException {
		JpaUserMapper jpaUserMapper = new JpaUserMapper(jpaCategoryDao, jpaGenderDao);

		try {
			Optional<PublicUserEntity> publicUser = jpaUserDao.findById(id);
			if (publicUser.isEmpty()) {
				return Optional.empty();
			}

			User user = jpaUserMapper.toDomain(publicUser.get());
			return Optional.of(user);

		} catch (KnowyInvalidUserGenderException e) {
			throw new KnowyInconsistentDataException("Error mapping PublicUserEntity to User", e);

		} catch (DataAccessException e) {
			throw new KnowyDataAccessException("Error accessing data from database", e);

		}
	}


	@Override
	public void updateNickname(String nickname, int id) throws KnowyDataAccessException {
		try {
			jpaUserDao.updateNickname(nickname, id);
		} catch (DataAccessException e) {
			throw new KnowyDataAccessException("Error accessing data from database", e);
		}
	}

	@Override
	public User save(User user) throws KnowyDataAccessException {
		JpaUserMapper jpaUserMapper = new JpaUserMapper(jpaCategoryDao, jpaGenderDao);

		try {
			jpaUserDao.save(jpaUserMapper.toEntity(user));
			return user;
		} catch (DataAccessException e) {
			throw new KnowyDataAccessException("Error accessing data from database", e);
		}
	}

	@Override
	public Optional<User> findByNickname(String nickname) throws KnowyDataAccessException {
		JpaUserMapper jpaUserMapper = new JpaUserMapper(jpaCategoryDao, jpaGenderDao);

		try {
			Optional<PublicUserEntity> publicUser = jpaUserDao.findByNickname(nickname);

			if (publicUser.isEmpty()) {
				return Optional.empty();
			}

			User user = jpaUserMapper.toDomain(publicUser.get());
			return Optional.of(user);

		} catch (KnowyInvalidUserGenderException e) {
			throw new KnowyInconsistentDataException("Error mapping PublicUserEntity to User", e);

		} catch (DataAccessException e) {
			throw new KnowyDataAccessException("Error accessing data from database", e);
		}
	}

	@Override
	public boolean existsByNickname(String nickname) throws KnowyDataAccessException {
		try {
			return jpaUserDao.existsByNickname(nickname);
		} catch (DataAccessException e) {
			throw new KnowyDataAccessException("Error accessing data from database", e);
		}
	}
}
