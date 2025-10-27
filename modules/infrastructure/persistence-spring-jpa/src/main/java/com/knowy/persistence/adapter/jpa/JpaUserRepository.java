package com.knowy.persistence.adapter.jpa;

import com.knowy.core.user.domain.User;
import com.knowy.core.user.port.UserRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserMapper;

import java.util.Optional;

public class JpaUserRepository implements UserRepository {

	private final JpaUserDao jpaUserDao;
	private final JpaCategoryDao jpaCategoryDao;

	public JpaUserRepository(JpaUserDao jpaUserDao, JpaCategoryDao jpaCategoryDao) {
		this.jpaUserDao = jpaUserDao;
		this.jpaCategoryDao = jpaCategoryDao;
	}

	@Override
	public Optional<User> findById(Integer id) {
		JpaUserMapper jpaUserMapper = new JpaUserMapper(jpaCategoryDao);

		return jpaUserDao.findById(id).map(jpaUserMapper::toDomain);
	}

	@Override
	public void updateNickname(String nickname, int id) {
		jpaUserDao.updateNickname(nickname, id);
	}

	@Override
	public User save(User user) {
		JpaUserMapper jpaUserMapper = new JpaUserMapper(jpaCategoryDao);

		jpaUserDao.save(jpaUserMapper.toEntity(user));
		return user;
	}

	@Override
	public Optional<User> findByNickname(String nickname) {
		JpaUserMapper jpaUserMapper = new JpaUserMapper(jpaCategoryDao);

		return jpaUserDao.findByNickname(nickname).map(jpaUserMapper::toDomain);
	}

	@Override
	public boolean existsByNickname(String nickname) {
		return jpaUserDao.existsByNickname(nickname);
	}
}
