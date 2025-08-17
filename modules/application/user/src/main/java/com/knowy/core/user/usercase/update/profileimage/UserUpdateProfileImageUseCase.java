package com.knowy.core.user.usercase.update.profileimage;

import com.knowy.core.user.exception.KnowyImageNotFoundException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.user.exception.KnowyUnchangedImageException;
import com.knowy.core.user.port.ProfileImageRepository;
import com.knowy.core.user.port.UserRepository;
import com.knowy.core.user.domain.ProfileImage;
import com.knowy.core.user.domain.User;

/**
 * Use case responsible for updating a user's profile image.
 * <p>
 * Validates that the new profile image exists and is different from the user's current image, then updates the user's
 * profile image in the repository.
 */
public class UserUpdateProfileImageUseCase {

	private final UserRepository userRepository;
	private final ProfileImageRepository profileImageRepository;


	/**
	 * Constructs a new {@code UserUpdateProfileImageUseCase} with the given repositories.
	 *
	 * @param userRepository         Repository for accessing and saving user data.
	 * @param profileImageRepository Repository for accessing profile images.
	 */
	public UserUpdateProfileImageUseCase(UserRepository userRepository, ProfileImageRepository profileImageRepository) {
		this.userRepository = userRepository;
		this.profileImageRepository = profileImageRepository;
	}


	/**
	 * Executes the profile image update process for the specified user.
	 *
	 * @param newProfileImageId The ID of the new profile image to assign to the user.
	 * @param userId            The ID of the user whose profile image will be updated.
	 * @throws KnowyUnchangedImageException If the new image is the same as the current one.
	 * @throws KnowyImageNotFoundException  If no profile image exists with the given ID.
	 * @throws KnowyUserNotFoundException   If no user exists with the given ID.
	 */
	public void execute(Integer newProfileImageId, Integer userId) throws KnowyUnchangedImageException,
		KnowyImageNotFoundException, KnowyUserNotFoundException {

		User user = findUserByIdOrThrow(userId);
		ProfileImage img = findProfileImageByIdOrThrow(newProfileImageId);
		ensureProfileImageIsDifferent(user, img);

		User newUser = new User(user.id(), user.nickname(), img, user.categories());
		userRepository.save(newUser);
	}

	private User findUserByIdOrThrow(int userId) throws KnowyUserNotFoundException {
		return userRepository.findById(userId)
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found with id: " + userId));
	}

	private ProfileImage findProfileImageByIdOrThrow(int profileImageId) throws KnowyImageNotFoundException {
		return profileImageRepository.findById(profileImageId)
			.orElseThrow(() -> new KnowyImageNotFoundException("Profile image with this id not found"));
	}

	private void ensureProfileImageIsDifferent(User user, ProfileImage profileImage) throws KnowyUnchangedImageException {
		if (user.profileImage().id().equals(profileImage.id())) {
			throw new KnowyUnchangedImageException("Image must be different from the current one.");
		}
	}
}
