package com.knowy.server.application;

import com.knowy.server.application.exception.KnowyMailDispatchException;
import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyImageNotFoundException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.*;
import com.knowy.server.application.ports.*;
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
import com.knowy.server.domain.UserPrivate;

/**
 * Service layer for handling user account private operations.
 * <p>
 * Provides a high-level API for registering new users, updating credentials, validating tokens, sending recovery
 * emails, deactivating accounts, and reactivating accounts.
 * <p>
 * Internally delegates execution to dedicated use case classes to enforce business logic.
 */
public class UserPrivateService {

    private final UserSignUpUseCase userSignUpUseCase;
    private final UserUpdatePasswordUseCase userUpdatePasswordUseCase;
    private final UserUpdateEmailUseCase userUpdateEmailUseCase;
    private final TokenUserPrivateTool tokenUserPrivateTool;
    private final SendRecoveryPasswordUseCase sendRecoveryPasswordUseCase;
    private final DeactivateAccountUseCase deactivateAccountUseCase;
    private final ReactivateAccountUseCase reactivateAccountUseCase;

    /**
     * Constructs a new {@code UserPrivateService} with the provided dependencies.
     *
     * @param userRepository         Repository for public user data access.
     * @param userPrivateRepository  Repository for private user data access.
     * @param profileImageRepository Repository for retrieving profile images.
     * @param knowyPasswordEncoder   Password encoder for hashing user passwords.
     * @param knowyTokenTools        Utility for generating and validating security tokens.
     * @param knowyEmailClientTool   Client for sending system emails.
     */
    public UserPrivateService(
            UserRepository userRepository,
            UserPrivateRepository userPrivateRepository,
            ProfileImageRepository profileImageRepository,
            KnowyPasswordEncoder knowyPasswordEncoder,
            KnowyTokenTools knowyTokenTools,
            KnowyEmailClientTool knowyEmailClientTool
    ) {
        this(
                userRepository,
                userPrivateRepository,
                profileImageRepository,
                knowyPasswordEncoder,
                knowyTokenTools,
                knowyEmailClientTool,
                new TokenUserPrivateTool(knowyTokenTools, userPrivateRepository)
        );
    }

    UserPrivateService(
            UserRepository userRepository,
            UserPrivateRepository userPrivateRepository,
            ProfileImageRepository profileImageRepository,
            KnowyPasswordEncoder knowyPasswordEncoder,
            KnowyTokenTools knowyTokenTools,
            KnowyEmailClientTool knowyEmailClientTool,
            TokenUserPrivateTool tokenUserPrivateTool
    ) {
        this.userSignUpUseCase = new UserSignUpUseCase(
                userRepository, userPrivateRepository, knowyPasswordEncoder, profileImageRepository
        );
        this.userUpdatePasswordUseCase = new UserUpdatePasswordUseCase(
                userPrivateRepository, knowyPasswordEncoder, knowyTokenTools
        );
        this.userUpdateEmailUseCase = new UserUpdateEmailUseCase(userPrivateRepository, knowyPasswordEncoder);
        this.tokenUserPrivateTool = tokenUserPrivateTool;
        this.sendRecoveryPasswordUseCase = new SendRecoveryPasswordUseCase(tokenUserPrivateTool, knowyEmailClientTool);
        this.deactivateAccountUseCase = new DeactivateAccountUseCase(
                tokenUserPrivateTool, knowyEmailClientTool, knowyPasswordEncoder, userPrivateRepository
        );
        this.reactivateAccountUseCase = new ReactivateAccountUseCase(tokenUserPrivateTool, userPrivateRepository);
    }

    /**
     * Registers a new user with the given credentials.
     *
     * @param command The {@link UserSingUpCommand} containing the user's registration details, including nickname,
     *                email, and password.
     * @return The created {@link UserPrivate} entity.
     * @throws KnowyInvalidUserException    If the nickname or email is invalid or already in use.
     * @throws KnowyImageNotFoundException  If the default profile image cannot be found.
     * @throws KnowyPasswordFormatException If the password format is invalid.
     */
    public UserPrivate registerNewUser(UserSingUpCommand command)
            throws KnowyInvalidUserException, KnowyImageNotFoundException, KnowyPasswordFormatException {
        return userSignUpUseCase.execute(command);
    }

    /**
     * Updates the user's password using a recovery token.
     *
     * @param command The {@link UserUpdatePasswordCommand} containing the recovery token, the new password, and the
     *                confirmation of the new password.
     * @throws KnowyTokenException          If the token is invalid or expired.
     * @throws KnowyPasswordFormatException If the password format is invalid.
     * @throws KnowyWrongPasswordException  If the password and confirmation do not match.
     * @throws KnowyUserNotFoundException   If the user associated with the token does not exist.
     */
    public UserPrivate updatePassword(UserUpdatePasswordCommand command)
            throws KnowyTokenException, KnowyPasswordFormatException, KnowyWrongPasswordException, KnowyUserNotFoundException {
        return userUpdatePasswordUseCase.execute(command);
    }

    /**
     * Updates the user's email address.
     *
     * @param command The {@link UserUpdateEmailCommand} containing the user's ID, the new email address, and the
     *                current password for verification.
     * @throws KnowyUnchangedEmailException   If the new email is the same as the current one.
     * @throws KnowyWrongPasswordException    If the provided password is incorrect.
     * @throws KnowyInvalidUserEmailException If the email is invalid or already in use.
     * @throws KnowyUserNotFoundException     If no user is found with the given ID.
     */
    public void updateEmail(UserUpdateEmailCommand command)
            throws KnowyUnchangedEmailException, KnowyWrongPasswordException, KnowyInvalidUserEmailException, KnowyUserNotFoundException {
        userUpdateEmailUseCase.execute(command);
    }

    /**
     * Checks if the provided token is valid for a user.
     *
     * @param token The token to validate.
     * @return {@code true} if the token is valid; {@code false} otherwise.
     */
    public boolean isValidUserToken(String token) {
        return tokenUserPrivateTool.isValidToken(token);
    }

    /**
     * Sends a password recovery email to the given address.
     *
     * @param email           The user's email.
     * @param recoveryBaseUrl The base URL for building the recovery link.
     * @throws KnowyTokenException        If an error occurs while generating the recovery token.
     * @throws KnowyMailDispatchException If the email cannot be sent.
     * @throws KnowyUserNotFoundException If no user exists with the provided email.
     */
    public void sendRecoveryPasswordEmail(Email email, String recoveryBaseUrl)
            throws KnowyTokenException, KnowyMailDispatchException, KnowyUserNotFoundException {
        sendRecoveryPasswordUseCase.execute(email, recoveryBaseUrl);
    }

    /**
     * Deactivates a user's account and sends them a recovery email with a reactivation link.
     *
     * @param command The {@link DeactivateAccountCommand} containing the user's email, current password, confirmation
     *                password, and the base URL for the recovery link.
     * @throws KnowyTokenException         If an error occurs while generating the recovery token.
     * @throws KnowyMailDispatchException  If the recovery email cannot be sent.
     * @throws KnowyWrongPasswordException If the provided password is incorrect or does not match the confirmation.
     * @throws KnowyUserNotFoundException  If no user exists with the provided email.
     */
    public void desactivateUserAccount(DeactivateAccountCommand command)
            throws KnowyTokenException, KnowyMailDispatchException, KnowyWrongPasswordException, KnowyUserNotFoundException {
        deactivateAccountUseCase.execute(command);
    }

    /**
     * Reactivates a user's account using the provided token.
     *
     * @param token The reactivation token.
     * @throws KnowyTokenException        If the token is invalid or expired.
     * @throws KnowyUserNotFoundException If no user exists for the given token.
     */
    public void reactivateUserAccount(String token) throws KnowyTokenException, KnowyUserNotFoundException {
        reactivateAccountUseCase.execute(token);
    }
}
