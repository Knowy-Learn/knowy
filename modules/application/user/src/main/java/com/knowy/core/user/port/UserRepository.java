package com.knowy.core.user.port;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.user.domain.User;

import java.util.Optional;

public interface UserRepository {
	Optional<User> findById(Integer id) throws KnowyDataAccessException;

	void updateNickname(String nickname, int id) throws KnowyDataAccessException;

	User save(User user) throws KnowyDataAccessException;

	Optional<User> findByNickname(String nickname) throws KnowyDataAccessException;

	boolean existsByNickname(String nickname) throws KnowyDataAccessException;
}
