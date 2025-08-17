package com.knowy.core.user.port;

import com.knowy.core.user.domain.UserPrivate;

import java.util.Optional;

public interface UserPrivateRepository {
	Optional<UserPrivate> findByEmail(String email);

	Optional<UserPrivate> findById(int id);

	UserPrivate save(UserPrivate user);
}
