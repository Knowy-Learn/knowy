package com.knowy.server.infrastructure.adapters.security;

import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.ports.KnowyPasswordEncoder;
import com.knowy.server.domain.UserPrivate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PasswordEncoderAdapter implements KnowyPasswordEncoder {

	private final PasswordEncoder passwordEncoder;

	public PasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String encode(String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	public void assertHasPassword(UserPrivate user, String password) throws KnowyWrongPasswordException {
		if (!hasPassword(user, password)) {
			throw new KnowyWrongPasswordException("Wrong password for user with id: " + user.id());
		}
	}

	@Override
	public boolean hasPassword(UserPrivate user, String password) {
		Objects.requireNonNull(user, "Can't check user password of null user");
		return passwordEncoder.matches(password, user.password().value());
	}
}
