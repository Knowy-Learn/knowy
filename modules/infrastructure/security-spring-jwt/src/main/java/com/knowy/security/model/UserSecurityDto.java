package com.knowy.security.model;

import com.knowy.core.domain.Category;
import com.knowy.core.user.domain.ProfileImage;
import com.knowy.core.user.domain.User;

import java.util.Set;

public record UserSecurityDto(Integer id, String nickname, ProfileImage profileImage, Set<Category> categories) {
	public UserSecurityDto(User user) {
		this(user.id(), user.nickname(), user.profileImage(), user.categories());
	}
}