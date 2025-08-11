package com.knowy.server.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class UserPrivate extends User implements Serializable {
	private final Email email;
	private final Password password;
	private final boolean active;

	public UserPrivate(
		Integer id,
		String nickname,
		ProfileImage profileImage,
		Set<Category> categories,
		Email email,
		Password password,
		boolean active
	) {
		super(id, nickname, profileImage, categories);
		this.email = email;
		this.password = password;
		this.active = active;
	}

	public UserPrivate(User user, Email email, Password password, boolean active) {
		this(user.id(), user.nickname(), user.profileImage(), user.categories(), email, password, active);
	}

	public UserPrivate(User user, Email email, Password password) {
		this(user, email, password, true);
	}

	public Email email() {
		return email;
	}

	public Password password() {
		return password;
	}

	public boolean active() {
		return active;
	}

	public User cropToUser() {
		return new User(this.id(), this.nickname(), this.profileImage(), this.categories());
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass()) return false;
		if (!super.equals(object)) return false;
		UserPrivate that = (UserPrivate) object;
		return active == that.active && Objects.equals(email, that.email) && Objects.equals(password, that.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), email, password, active);
	}
}