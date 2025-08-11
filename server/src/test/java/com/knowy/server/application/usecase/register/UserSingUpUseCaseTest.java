package com.knowy.server.application.usecase.register;

import com.knowy.server.application.exception.KnowyException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserEmailException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserNicknameException;
import com.knowy.server.application.exception.validation.user.KnowyPasswordFormatException;
import com.knowy.server.application.ports.KnowyPasswordEncoder;
import com.knowy.server.application.ports.ProfileImageRepository;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.application.ports.UserRepository;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.Password;
import com.knowy.server.domain.ProfileImage;
import com.knowy.server.domain.UserPrivate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserSingUpUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPrivateRepository userPrivateRepository;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @Mock
    private KnowyPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserSignUpUseCase userSignUpUseCase;

    @Test
    void given_userSignUpCommand_when_executeSignUp_then_registerNewUser() throws KnowyException {
        UserSingUpCommand userSingUpCommand = new UserSingUpCommand(
                "TestNickname", "test@email.com", "ValidPass123@"
        );
        UserPrivate userPrivateResult = new UserPrivate(
                1,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("test@email.com"),
                new Password("ENCODED_PASS"),
                true
        );

        Mockito.when(userPrivateRepository.findByEmail(userSingUpCommand.email()))
                .thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(userSingUpCommand.password()))
                .thenReturn("ENCODED_PASS");
        Mockito.when(profileImageRepository.findById(1))
                .thenReturn(Optional.of(new ProfileImage(1, "https://knowy/image.png")));
        Mockito.when(userPrivateRepository.save(any(UserPrivate.class)))
                .thenReturn(userPrivateResult);

        UserPrivate newUserPrivate = userSignUpUseCase.execute(userSingUpCommand);
        assertEquals(newUserPrivate, userPrivateResult);
    }

    @Test
    void given_blankNickname_when_executeSingUp_then_KnowyInvalidUserNicknameException() {
        UserSingUpCommand userSingUpCommand = new UserSingUpCommand(
                "   ", "test@email.com", "ValidPass123@"
        );

        assertThrows(
                KnowyInvalidUserNicknameException.class,
                () -> userSignUpUseCase.execute(userSingUpCommand)
        );
    }

    @Test
    void given_existNickname_when_executeSingUp_then_KnowyInvalidUserNicknameException() {
        String existNickname = "existNickname";
        UserSingUpCommand userSingUpCommand = new UserSingUpCommand(
                existNickname, "test@email.com", "ValidPass123@"
        );
        UserPrivate userPrivateResult = new UserPrivate(
                1,
                existNickname,
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("test@email.com"),
                new Password("ENCODED_PASS"),
                true
        );

        Mockito.when(userRepository.findByNickname(existNickname))
                .thenReturn(Optional.of(userPrivateResult));

        assertThrows(
                KnowyInvalidUserNicknameException.class,
                () -> userSignUpUseCase.execute(userSingUpCommand)
        );
    }

    @Test
    void given_existingEmail_when_executeSingUp_then_throwKnowyInvalidUserEmailException() {
        String existMail = "existmail@mail.com";

        UserSingUpCommand userSingUpCommand = new UserSingUpCommand(
                "TestNickname", existMail, "ValidPass123@"
        );
        UserPrivate userPrivate = new UserPrivate(
                1,
                "OtherTestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email(existMail),
                new Password("ValidPass123"),
                true
        );

        Mockito.when(userPrivateRepository.findByEmail(existMail))
                .thenReturn(Optional.of(userPrivate));

        assertThrows(
                KnowyInvalidUserEmailException.class,
                () -> userSignUpUseCase.execute(userSingUpCommand)
        );
    }

    @Test
    void given_invalidPassword_when_executeSingUp_then_throwKnowyInvalidUserPasswordFormatException() {
        UserSingUpCommand userSingUpCommand = new UserSingUpCommand(
                "TestNickname", "test@mail.com", "invalidPassword"
        );

        Mockito.when(userPrivateRepository.findByEmail(userSingUpCommand.email()))
                .thenReturn(Optional.empty());

        assertThrows(
                KnowyPasswordFormatException.class, () ->
                        userSignUpUseCase.execute(userSingUpCommand)
        );
    }
}
