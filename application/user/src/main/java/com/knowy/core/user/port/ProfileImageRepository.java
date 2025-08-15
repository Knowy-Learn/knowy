package com.knowy.core.user.port;

import com.knowy.core.user.domain.ProfileImage;

import java.util.Optional;

public interface ProfileImageRepository {
	Optional<ProfileImage> findById(int id);
}
