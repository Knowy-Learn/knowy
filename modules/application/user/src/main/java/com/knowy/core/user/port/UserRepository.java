package com.knowy.core.user.port;

import com.knowy.core.user.domain.User;

import java.util.Optional;

public interface UserRepository {
	Optional<User> findById(Integer id);

	void updateNickname(String nickname, int id);

	User save(User user);

	Optional<User> findByNickname(String nickname);

	boolean existsByNickname(String nickname);
}
