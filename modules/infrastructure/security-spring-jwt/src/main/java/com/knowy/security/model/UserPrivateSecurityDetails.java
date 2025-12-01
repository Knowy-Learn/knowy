package com.knowy.security.model;

import com.knowy.core.user.domain.UserPrivate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record UserPrivateSecurityDetails(UserPrivate userPrivate) implements UserDetails {

	public UserSecurityDto getUser() {
		return new UserSecurityDto(userPrivate.cropToUser());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return userPrivate.password().value();
	}

	@Override
	public String getUsername() {
		return userPrivate.email().value();
	}

	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return userPrivate.active();
	}
}
