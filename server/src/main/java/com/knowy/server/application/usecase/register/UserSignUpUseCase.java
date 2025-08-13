package com.knowy.server.application.usecase.register;

import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyImageNotFoundException;
import com.knowy.server.domain.exception.KnowyUserEmailFormatException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserNicknameException;
import com.knowy.server.domain.exception.KnowyPasswordFormatException;
import com.knowy.server.application.ports.KnowyPasswordEncoder;
import com.knowy.server.application.ports.ProfileImageRepository;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.application.ports.UserRepository;
import com.knowy.server.application.usecase.KnowyUseCase;
import com.knowy.server.application.util.StringUtils;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.Password;
import com.knowy.server.domain.UserPrivate;

import java.util.HashSet;

/**
 * Use case class responsible for handling the user sign-up process.
 */
public class UserSignUpUseCase  implements KnowyUseCase<UserSingUpCommand, UserPrivate> {

    private final UserRepository userRepository;
    private final UserPrivateRepository userPrivateRepository;
    private final ProfileImageRepository profileImageRepository;
    private final KnowyPasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserSignUpUseCase with the required dependencies.
     *
     * @param userRepository         Repository for user public data access.
     * @param userPrivateRepository  Repository for user private data access.
     * @param passwordEncoder        Password encoder for encrypting user passwords.
     * @param profileImageRepository Repository for profile image access.
     */
    public UserSignUpUseCase(
            UserRepository userRepository,
            UserPrivateRepository userPrivateRepository,
            KnowyPasswordEncoder passwordEncoder,
            ProfileImageRepository profileImageRepository
    ) {
        this.userRepository = userRepository;
        this.userPrivateRepository = userPrivateRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileImageRepository = profileImageRepository;
    }

    /**
     * Executes the user sign-up process with the given sign-up command. Validates user data, encodes the password, and
     * creates a new user record in the persistence.
     *
     * @param userSingUpCommand Command containing user registration data.
     * @return The newly created UserPrivate entity.
     * @throws KnowyInvalidUserException    If user validation fails.
     * @throws KnowyImageNotFoundException  If the default profile image is not found.
     * @throws KnowyPasswordFormatException If the password format is invalid.
     */
    public UserPrivate execute(UserSingUpCommand userSingUpCommand)
            throws KnowyInvalidUserException, KnowyImageNotFoundException, KnowyPasswordFormatException {

        assertUserNickname(userSingUpCommand.nickname());
        validateEmail(userSingUpCommand.email());
        Password.assertPasswordFormatIsRight(userSingUpCommand.password());

        String encodedPassword = passwordEncoder.encode(userSingUpCommand.password());
        UserPrivate userPrivate = new UserPrivate(
                null,
                userSingUpCommand.nickname(),
                profileImageRepository.findById(1)
                        .orElseThrow(() -> new KnowyImageNotFoundException("Not found profile image")),
                new HashSet<>(),
                new Email(userSingUpCommand.email()),
                new Password(encodedPassword),
                true
        );
        return userPrivateRepository.save(userPrivate);
    }

    private void assertUserNickname(String nickname) throws KnowyInvalidUserException {
        if (StringUtils.isBlank(nickname)) {
            throw new KnowyInvalidUserNicknameException("Invalid nickname");
        }

        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new KnowyInvalidUserNicknameException("Nickname already exists");
        }
    }

    private void validateEmail(String email) throws KnowyUserEmailFormatException {
        Email.assertValid(email);
        if (userPrivateRepository.findByEmail(email).isPresent()) {
            throw new KnowyUserEmailFormatException("Email already exists");
        }
    }
}
