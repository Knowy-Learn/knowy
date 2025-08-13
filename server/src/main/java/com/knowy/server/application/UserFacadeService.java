package com.knowy.server.application;

import com.knowy.server.application.exception.KnowyMailDispatchException;
import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.KnowyInconsistentDataException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyImageNotFoundException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.*;
import com.knowy.server.application.usecase.manage.DeactivateAccountCommand;
import com.knowy.server.application.usecase.manage.DeactivateAccountUseCase;
import com.knowy.server.application.usecase.manage.ReactivateAccountUseCase;
import com.knowy.server.application.usecase.manage.SendRecoveryPasswordUseCase;
import com.knowy.server.application.usecase.register.UserSignUpUseCase;
import com.knowy.server.application.usecase.register.UserSingUpCommand;
import com.knowy.server.application.usecase.update.email.UserUpdateEmailCommand;
import com.knowy.server.application.usecase.update.email.UserUpdateEmailUseCase;
import com.knowy.server.application.usecase.update.password.UserUpdatePasswordCommand;
import com.knowy.server.application.usecase.update.password.UserUpdatePasswordUseCase;
import com.knowy.server.application.util.TokenUserPrivateTool;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.Password;
import com.knowy.server.domain.UserPrivate;

public class UserFacadeService {

	private final UserService userService;
	private final TokenUserPrivateTool tokenUserPrivateTool;
	private final UserSignUpUseCase userSignUpUseCase;
	private final UserUpdateEmailUseCase userUpdateEmailUseCase;
	private final UserUpdatePasswordUseCase userUpdatePasswordUseCase;
	private final DeactivateAccountUseCase deactivateAccountUseCase;
	private final SendRecoveryPasswordUseCase sendRecoveryPasswordUseCase;
	private final ReactivateAccountUseCase reactivateAccountUseCase;

	/**
	 * The constructor
	 *
	 * @param publicUserService the publicUserService
	 */
	public UserFacadeService(
		UserService publicUserService,
		TokenUserPrivateTool tokenUserPrivateTool,
		UserSignUpUseCase userSignUpUseCase,
		UserUpdateEmailUseCase userUpdateEmailUseCase,
		UserUpdatePasswordUseCase userUpdatePasswordUseCase,
		DeactivateAccountUseCase deactivateAccountUseCase, SendRecoveryPasswordUseCase sendRecoveryPasswordUseCase, ReactivateAccountUseCase reactivateAccountUseCase
	) {
		this.userService = publicUserService;
		this.tokenUserPrivateTool = tokenUserPrivateTool;
		this.userSignUpUseCase = userSignUpUseCase;
		this.userUpdateEmailUseCase = userUpdateEmailUseCase;
		this.userUpdatePasswordUseCase = userUpdatePasswordUseCase;
		this.deactivateAccountUseCase = deactivateAccountUseCase;
		this.sendRecoveryPasswordUseCase = sendRecoveryPasswordUseCase;
		this.reactivateAccountUseCase = reactivateAccountUseCase;
	}

	/**
	 * Updates the nickname of a public user.
	 *
	 * <p>Delegates the update to the public user service. If the new nickname
	 * is already taken or is identical to the current one, an exception is thrown.</p>
	 *
	 * @param nickname the new nickname to assign
	 * @param userId   the ID of the user whose nickname is to be updated
	 * @throws KnowyNicknameAlreadyTakenException if the desired nickname is already in use
	 * @throws KnowyUnchangedNicknameException    if the new nickname is the same as the current one
	 */
	public void updateNickname(String nickname, int userId)
		throws KnowyNicknameAlreadyTakenException, KnowyUnchangedNicknameException, KnowyInvalidUserNicknameException, KnowyUserNotFoundException {
		userService.updateNickname(nickname, userId);
	}

	/**
	 * Updates the profile image of a public user.
	 *
	 * <p>Attempts to update the user's profile image using the provided image ID.
	 * If the new image is the same as the current one, or if the image is not found, exceptions are thrown.</p>
	 *
	 * @param profilePictureId the ID of the new profile image
	 * @param userId           the ID of the user whose profile image is to be updated
	 * @throws KnowyUnchangedImageException if the new image is the same as the current image
	 * @throws KnowyImageNotFoundException  if the image with the given ID cannot be found
	 */
	public void updateProfileImage(int profilePictureId, int userId)
		throws KnowyUnchangedImageException, KnowyImageNotFoundException, KnowyUserNotFoundException {
		userService.updateProfileImage(profilePictureId, userId);
	}

	/**
	 * Updates the list of favorite categories for the specified public user.
	 *
	 * <p>Delegates the update operation to the {@code PublicUserService}.
	 * Replaces the user's current language list with the provided one.</p>
	 *
	 * @param userId    the ID of the user whose categories are to be updated
	 * @param languages an array of language representing the user's spoken categories
	 * @throws KnowyUserNotFoundException if the user does not exist
	 */
	public void updateLanguages(int userId, String[] languages)
		throws KnowyInconsistentDataException {
		userService.updateCategories(userId, languages);
	}
}
