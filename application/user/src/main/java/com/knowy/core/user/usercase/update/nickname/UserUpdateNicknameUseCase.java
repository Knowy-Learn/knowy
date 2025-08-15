package com.knowy.core.user.usercase.update.nickname;

import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.user.exception.KnowyInvalidUserNicknameException;
import com.knowy.core.user.exception.KnowyNicknameAlreadyTakenException;
import com.knowy.core.user.exception.KnowyUnchangedNicknameException;
import com.knowy.core.user.port.UserRepository;
import com.knowy.core.util.StringUtils;
import com.knowy.core.user.domain.User;

/**
 * Use case responsible for updating a user's nickname.
 */
public class UserUpdateNicknameUseCase {

	private final UserRepository userRepository;

	/**
	 * Constructs a new {@code UserUpdateNicknameUseCase} with the given repository.
	 *
	 * @param userRepository Repository for accessing and updating user data.
	 */
	public UserUpdateNicknameUseCase(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Executes the nickname update process for the specified user.
	 *
	 * @param newNickname The new nickname to assign to the user.
	 * @param userId      The ID of the user whose nickname will be updated.
	 * @throws KnowyUnchangedNicknameException    If the new nickname is the same as the current one.
	 * @throws KnowyNicknameAlreadyTakenException If the new nickname is already in use by another user.
	 * @throws KnowyInvalidUserNicknameException  If the new nickname is blank or invalid.
	 * @throws KnowyUserNotFoundException         If no user exists with the given ID.
	 */
	public void execute(String newNickname, Integer userId)
		throws KnowyUnchangedNicknameException, KnowyNicknameAlreadyTakenException, KnowyInvalidUserNicknameException,
		KnowyUserNotFoundException {

		assertNotBlankNickname(newNickname);
		User user = findByIdOrThrow(userId);
		ensureNicknameIsDifferent(user, newNickname);
		ensureNicknameIsAvailable(newNickname);

		userRepository.updateNickname(newNickname, userId);
	}

	private User findByIdOrThrow(int userId) throws KnowyUserNotFoundException {
		return userRepository.findById(userId)
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found"));
	}

	private void ensureNicknameIsDifferent(User user, String newNickname) throws KnowyUnchangedNicknameException {
		if (user.nickname().equals(newNickname)) {
			throw new KnowyUnchangedNicknameException("Nickname must be different from the current one.");
		}
	}

	private void ensureNicknameIsAvailable(String newNickname) throws KnowyNicknameAlreadyTakenException {
		if (userRepository.existsByNickname(newNickname)) {
			throw new KnowyNicknameAlreadyTakenException("Nickname is already in use.");
		}
	}

	private void assertNotBlankNickname(String nickname) throws KnowyInvalidUserNicknameException {
		if (StringUtils.isBlank(nickname)) {
			throw new KnowyInvalidUserNicknameException("Blank nicknames are not allowed");
		}
	}
}
