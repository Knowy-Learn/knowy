package com.knowy.server.application;

import com.knowy.server.application.exception.KnowyMailDispatchException;
import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.KnowyInconsistentDataException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyImageNotFoundException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.*;
import com.knowy.server.application.model.MailMessage;
import com.knowy.server.application.ports.KnowyEmailClientTool;
import com.knowy.server.application.usecase.register.UserSignUpUseCase;
import com.knowy.server.application.usecase.register.UserSingUpCommand;
import com.knowy.server.application.usecase.update.email.UserUpdateEmailCommand;
import com.knowy.server.application.usecase.update.email.UserUpdateEmailUseCase;
import com.knowy.server.domain.UserPrivate;

public class UserFacadeService {

    private final KnowyEmailClientTool knowyEmailClientTool;
    private final UserPrivateService userPrivateService;
    private final UserService userService;
    private final UserSignUpUseCase userSignUpUseCase;
    private final UserUpdateEmailUseCase userUpdateEmailUseCase;

    /**
     * The constructor
     *
     * @param knowyEmailClientTool the emailClientService
     * @param userPrivateService   the privateUserService
     * @param publicUserService    the publicUserService
     */
    public UserFacadeService(
            KnowyEmailClientTool knowyEmailClientTool,
            UserPrivateService userPrivateService,
            UserService publicUserService, UserSignUpUseCase userSignUpUseCase, UserUpdateEmailUseCase userUpdateEmailUseCase
    ) {
        this.knowyEmailClientTool = knowyEmailClientTool;
        this.userPrivateService = userPrivateService;
        this.userService = publicUserService;
        this.userSignUpUseCase = userSignUpUseCase;
        this.userUpdateEmailUseCase = userUpdateEmailUseCase;
    }

    public UserPrivate registerNewUser(String nickname, String email, String password)
            throws KnowyInvalidUserException, KnowyImageNotFoundException, KnowyPasswordFormatException {
        return userSignUpUseCase.execute(new UserSingUpCommand(nickname, email, password));
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
            throws KnowyInconsistentDataException, KnowyUserNotFoundException {
        userService.updateCategories(userId, languages);
    }

    /**
     * Updates a user's password using a password recovery token.
     *
     * <p>Validates the token, checks the password format and that both provided passwords match.
     * If all checks pass, the user's password is updated.</p>
     *
     * @param token           the JWT token used to identify and authorize the password reset
     * @param password        the new password
     * @param confirmPassword confirmation of the new password
     * @throws KnowyTokenException          if the token is invalid or expired
     * @throws KnowyPasswordFormatException if the password format is invalid or passwords do not match
     */
    public void updatePassword(String token, String password, String confirmPassword)
            throws KnowyTokenException, KnowyPasswordFormatException, KnowyWrongPasswordException, KnowyUserNotFoundException {
        userPrivateService.resetPassword(token, password, confirmPassword);
    }

    public void updateEmail(String email, int userId, String password)
            throws KnowyUnchangedEmailException, KnowyWrongPasswordException, KnowyInvalidUserEmailException, KnowyUserNotFoundException {
        userUpdateEmailUseCase.execute(new UserUpdateEmailCommand(userId, email, password));
    }

    /**
     * Verifies whether the given password recovery token is valid.
     *
     * <p>Delegates the validation to the {@code PrivateUserService}, which checks
     * the integrity, expiration, and user association of the token.</p>
     *
     * @param token the JWT token to validate
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    public boolean isValidToken(String token) {
        return userPrivateService.isValidToken(token);
    }

    /**
     * Sends a password recovery email to the user associated with the given email address.
     *
     * <p>Generates a recovery link using the provided base URL and user-specific token,
     * composes an email, and sends it using the configured email client.</p>
     *
     * @param email           the email address of the user requesting password recovery
     * @param recoveryBaseUrl the base URL to be used in the recovery link (e.g., frontend reset page)
     * @throws KnowyTokenException        if there is a problem generating the recovery token
     * @throws KnowyMailDispatchException if the email could not be sent
     */
    public void sendRecoveryPasswordEmail(String email, String recoveryBaseUrl)
            throws KnowyTokenException, KnowyMailDispatchException, KnowyUserNotFoundException {
        MailMessage mailMessage = userPrivateService.createRecoveryPasswordEmail(email, recoveryBaseUrl);
        knowyEmailClientTool.sendEmail(mailMessage.to(), mailMessage.subject(), mailMessage.body());
    }

    /**
     * Deactivates the user account after validating the password and confirmation. Sends an email with the account
     * recovery link using the provided recovery base URL.
     *
     * @param password        the user's current password for validation
     * @param confirmPassword the confirmation of the password
     * @param email           the email of the user whose account will be deactivated
     * @param recoveryBaseUrl the base URL to be used for account reactivation link
     * @throws KnowyUserNotFoundException  if the user with the given email does not exist
     * @throws KnowyTokenException         if there is an error generating the JWT token
     * @throws KnowyMailDispatchException  if sending the recovery email fails
     * @throws KnowyWrongPasswordException if the password and confirmation do not match or are incorrect
     */
    public void desactivateUserAccount(
            String password,
            String confirmPassword,
            String email,
            String recoveryBaseUrl
    ) throws KnowyTokenException, KnowyMailDispatchException, KnowyWrongPasswordException, KnowyUserNotFoundException {
        userPrivateService.desactivateUserAccount(email, password, confirmPassword);
        MailMessage mailMessage = userPrivateService.createDeletedAccountEmail(email, recoveryBaseUrl);
        knowyEmailClientTool.sendEmail(mailMessage.to(), mailMessage.subject(), mailMessage.body());
    }

    /**
     * Reactivates a previously deactivated user account using a valid reactivation token.
     *
     * @param token the JWT token used to verify and reactivate the user account
     * @throws KnowyUserNotFoundException if the user associated with the token does not exist
     * @throws KnowyTokenException        if the token is invalid or expired
     */
    public void reactivateUserAccount(String token) throws KnowyTokenException, KnowyUserNotFoundException {
        userPrivateService.reactivateUserAccount(token);
    }
}
