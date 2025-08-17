package com.knowy.core.user;

import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.user.exception.KnowyImageNotFoundException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.user.exception.KnowyInvalidUserNicknameException;
import com.knowy.core.user.exception.KnowyNicknameAlreadyTakenException;
import com.knowy.core.user.exception.KnowyUnchangedImageException;
import com.knowy.core.user.exception.KnowyUnchangedNicknameException;
import com.knowy.core.port.CategoryRepository;
import com.knowy.core.user.port.ProfileImageRepository;
import com.knowy.core.user.port.UserRepository;
import com.knowy.core.user.usercase.update.categories.UserUpdateCategoriesUseCase;
import com.knowy.core.user.usercase.update.nickname.UserUpdateNicknameUseCase;
import com.knowy.core.user.usercase.update.profileimage.UserUpdateProfileImageUseCase;


/**
 * Application service providing operations to update user data, including nickname, profile image, and categories.
 */
public class UserService {

	private final UserUpdateNicknameUseCase userUpdateEmailUseCase;
	private final UserUpdateProfileImageUseCase userUpdateProfileImageUseCase;
	private final UserUpdateCategoriesUseCase userUpdateCategoriesUseCase;

	/**
	 * Creates a new {@code UserService} with the required repositories.
	 *
	 * @param userRepository         Repository for accessing and saving user data.
	 * @param categoryRepository     Repository for accessing and saving category data.
	 * @param profileImageRepository Repository for accessing profile images.
	 */
	public UserService(
		UserRepository userRepository,
		CategoryRepository categoryRepository,
		ProfileImageRepository profileImageRepository
	) {
		this.userUpdateEmailUseCase = new UserUpdateNicknameUseCase(userRepository);
		this.userUpdateProfileImageUseCase = new UserUpdateProfileImageUseCase(userRepository, profileImageRepository);
		this.userUpdateCategoriesUseCase = new UserUpdateCategoriesUseCase(userRepository, categoryRepository);
	}

	/**
	 * Updates the nickname of a user.
	 * <p>
	 * Validates that the new nickname is not blank, is different from the current one, and is not already in use by
	 * another user.
	 *
	 * @param newNickname The new nickname to assign.
	 * @param userId      The ID of the user whose nickname will be updated.
	 * @throws KnowyUserNotFoundException         If the user with the given ID does not exist.
	 * @throws KnowyUnchangedNicknameException    If the new nickname is the same as the current one.
	 * @throws KnowyNicknameAlreadyTakenException If the new nickname is already in use by another user.
	 * @throws KnowyInvalidUserNicknameException  If the nickname is blank or otherwise invalid.
	 */
	public void updateNickname(String newNickname, Integer userId)
		throws KnowyUserNotFoundException, KnowyUnchangedNicknameException, KnowyNicknameAlreadyTakenException,
		KnowyInvalidUserNicknameException {

		userUpdateEmailUseCase.execute(newNickname, userId);
	}

	/**
	 * Updates the profile image of a user.
	 * <p>
	 * Validates that the new profile image exists and is different from the current one.
	 *
	 * @param newProfileImageId The ID of the new profile image to assign.
	 * @param userId            The ID of the user whose profile image will be updated.
	 * @throws KnowyUserNotFoundException   If no user exists with the given ID.
	 * @throws KnowyImageNotFoundException  If no profile image exists with the given ID.
	 * @throws KnowyUnchangedImageException If the new image is the same as the current one.
	 */
	public void updateProfileImage(Integer newProfileImageId, Integer userId)
		throws KnowyUnchangedImageException, KnowyImageNotFoundException, KnowyUserNotFoundException {

		userUpdateProfileImageUseCase.execute(newProfileImageId, userId);
	}

	/**
	 * Updates the set of categories associated with a user.
	 * <p>
	 * Validates that all specified categories exist in the system before applying the update.
	 *
	 * @param userId     The ID of the user whose categories will be updated.
	 * @param categories An array of category names to assign to the user. Must not be {@code null} (use an empty array
	 *                   for no categories).
	 * @throws KnowyInconsistentDataException If one or more of the specified categories do not exist.
	 */
	public void updateCategories(Integer userId, String[] categories) throws KnowyInconsistentDataException {
		userUpdateCategoriesUseCase.execute(userId, categories);
	}
}
