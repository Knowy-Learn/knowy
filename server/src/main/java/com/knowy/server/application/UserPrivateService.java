package com.knowy.server.application;

import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserEmailException;
import com.knowy.server.application.exception.validation.user.KnowyPasswordFormatException;
import com.knowy.server.application.exception.validation.user.KnowyUnchangedEmailException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.model.MailMessage;
import com.knowy.server.application.model.PasswordResetInfo;
import com.knowy.server.application.ports.KnowyPasswordEncoder;
import com.knowy.server.application.ports.KnowyTokenTools;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.application.util.TokenUserPrivateTool;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.Password;
import com.knowy.server.domain.UserPrivate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class UserPrivateService {

    private final UserPrivateRepository privateUserRepository;
	private final TokenUserPrivateTool tokenUserPrivateTool;

    /**
     * The constructor
     *
     * @param privateUserRepository the privateUserRepository
     * @param passwordEncoder       the passwordEncoder
     * @param tokenTools            the tokenTools
     */
    public UserPrivateService(
		UserPrivateRepository privateUserRepository,
		KnowyPasswordEncoder passwordEncoder,
		KnowyTokenTools tokenTools, TokenUserPrivateTool tokenUserPrivateTool
	) {
        this.privateUserRepository = privateUserRepository;
		this.tokenUserPrivateTool = tokenUserPrivateTool;
	}

    /**
     * Persists the given private user entity in the database.
     *
     * <p>If the user already exists, it will be updated; otherwise, a new record will be created.</p>
     *
     * @param user the {@code PrivateUserEntity} to persist
     * @return the saved entity, potentially with an updated ID or other persisted fields
     */
    public UserPrivate save(UserPrivate user) {
        return privateUserRepository.save(user);
    }

    /**
     * Retrieves a private user entity by email if it exists.
     *
     * @param email the email address of the private user
     * @return an {@code Optional} containing the user if found, or empty if not
     */
    public Optional<UserPrivate> findPrivateUserByEmail(String email) {
        return privateUserRepository.findByEmail(email);
    }

    /**
     * Retrieves a private user entity by email or throws an exception if not found.
     *
     * <p>This method guarantees a non-null result and is typically used in flows
     * where the presence of a user is required.</p>
     *
     * @param email the email address of the private user
     * @return the {@code PrivateUserEntity} associated with the given email
     * @throws KnowyUserNotFoundException if no user exists with the specified email
     */
    public UserPrivate getUserPrivateByEmail(String email) throws KnowyUserNotFoundException {
        return findPrivateUserByEmail(email)
                .orElseThrow(() -> new KnowyUserNotFoundException("User not found"));
    }


    /**
     * Reactivates a user account based on a valid token.
     *
     * @param token the JWT token used to verify the reactivation request
     * @throws KnowyTokenException        if the token is invalid or cannot be processed
     * @throws KnowyUserNotFoundException if no user is associated with the token
     */
    public void reactivateUserAccount(String token) throws KnowyTokenException, KnowyUserNotFoundException {
        UserPrivate userPrivate = tokenUserPrivateTool.verifyPasswordToken(token);

        if (!userPrivate.active()) {
            UserPrivate newUserPrivate = new UserPrivate(
                    userPrivate.cropToUser(),
                    userPrivate.email(),
                    userPrivate.password(),
                    true
            );
            privateUserRepository.save(newUserPrivate);
        }
    }
}
