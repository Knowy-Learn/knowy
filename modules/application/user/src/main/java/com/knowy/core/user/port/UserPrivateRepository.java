package com.knowy.core.user.port;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.user.domain.UserPrivate;

import java.util.Optional;

public interface UserPrivateRepository {
	Optional<UserPrivate> findByEmail(String email) throws KnowyDataAccessException;

	Optional<UserPrivate> findById(int id) throws KnowyDataAccessException;

	UserPrivate save(UserPrivate user) throws KnowyDataAccessException;
}
