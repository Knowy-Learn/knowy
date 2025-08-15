package com.knowy.server.application;

import com.knowy.server.application.exception.KnowyException;
import com.knowy.server.application.exception.KnowyMailDispatchException;
import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserNicknameException;
import com.knowy.server.application.exception.validation.user.KnowyUnchangedEmailException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.model.PasswordResetInfo;
import com.knowy.server.application.ports.*;
import com.knowy.server.application.usecase.manage.DeactivateAccountCommand;
import com.knowy.server.application.usecase.register.UserSingUpCommand;
import com.knowy.server.application.usecase.update.email.UserUpdateEmailCommand;
import com.knowy.server.application.usecase.update.password.UserUpdatePasswordCommand;
import com.knowy.server.application.util.TokenUserPrivateTool;
import com.knowy.server.domain.*;
import com.knowy.server.domain.exception.KnowyPasswordFormatException;
import com.knowy.server.domain.exception.KnowyUserEmailFormatException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserPrivateServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPrivateRepository userPrivateRepository;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @Mock
    private KnowyPasswordEncoder knowyPasswordEncoder;

    @Mock
    private KnowyTokenTools knowyTokenTools;

    @Mock
    private KnowyEmailClientTool knowyEmailClientTool;

    @Mock
    private TokenUserPrivateTool tokenUserPrivateTool;

    @InjectMocks
    private UserPrivateService userPrivateService;

    @Test
    void given_validDependencies_when_createUserPrivateService_then_doesNotThrow() {
        assertDoesNotThrow(() -> new UserPrivateService(
                userRepository,
                userPrivateRepository,
                profileImageRepository,
                knowyPasswordEncoder,
                knowyTokenTools,
                knowyEmailClientTool
        ));
    }

    @Nested
    class UserSingUpUseCaseTest {

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
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(userPrivateRepository.findByEmail(userSingUpCommand.email()))
                    .thenReturn(Optional.empty());
            Mockito.when(knowyPasswordEncoder.encode(userSingUpCommand.password()))
                    .thenReturn("Encoded.Password.123");
            Mockito.when(profileImageRepository.findById(1))
                    .thenReturn(Optional.of(new ProfileImage(1, "https://knowy/image.png")));
            Mockito.when(userPrivateRepository.save(any(UserPrivate.class)))
                    .thenReturn(userPrivateResult);

            UserPrivate newUserPrivate = userPrivateService.registerNewUser(userSingUpCommand);
            assertEquals(newUserPrivate, userPrivateResult);
        }

        @Test
        void given_blankNickname_when_executeSingUp_then_KnowyInvalidUserNicknameException() {
            UserSingUpCommand userSingUpCommand = new UserSingUpCommand(
                    "   ", "test@email.com", "ValidPass123@"
            );

            assertThrows(
                    KnowyInvalidUserNicknameException.class,
                    () -> userPrivateService.registerNewUser(userSingUpCommand)
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
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(userRepository.findByNickname(existNickname))
                    .thenReturn(Optional.of(userPrivateResult));

            assertThrows(
                    KnowyInvalidUserNicknameException.class,
                    () -> userPrivateService.registerNewUser(userSingUpCommand)
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
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(userPrivateRepository.findByEmail(existMail))
                    .thenReturn(Optional.of(userPrivate));

            assertThrows(
                    KnowyUserEmailFormatException.class,
                    () -> userPrivateService.registerNewUser(userSingUpCommand)
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
                    KnowyPasswordFormatException.class, () -> userPrivateService.registerNewUser(userSingUpCommand)
            );
        }
    }

    @Nested
    class UserUpdatePasswordUseCaseTest {
        @Test
        void given_validTokenAndMatchingPasswords_when_resetPassword_then_passwordResetSuccess() throws Exception {
            UserUpdatePasswordCommand userUpdatePasswordCommand = new UserUpdatePasswordCommand(
                    "valid-token",
                    "ValidNewPass123@",
                    "ValidNewPass123@"
            );

            User user = new User(
                    11,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>()
            );
            UserPrivate userPrivate = new UserPrivate(
                    user,
                    new Email("test@email.com"),
                    new Password("ValidOldPass123@"),
                    true
            );

            UserPrivate newUserPrivate = new UserPrivate(
                    user,
                    new Email("test@email.com"),
                    new Password("Encoded.New.Password.123"),
                    true
            );
            PasswordResetInfo passwordResetInfo = new PasswordResetInfo(11, "user@mail.com");

            Mockito.when(knowyTokenTools.decodeUnverified("valid-token", PasswordResetInfo.class))
                    .thenReturn(passwordResetInfo);
            Mockito.when(userPrivateRepository.findById(userPrivate.id()))
                    .thenReturn(Optional.of(userPrivate));
            Mockito.when(knowyTokenTools.decode(userPrivate.password().value(), "valid-token", PasswordResetInfo.class))
                    .thenReturn(passwordResetInfo);
            Mockito.when(knowyPasswordEncoder.encode("ValidNewPass123@"))
                    .thenReturn("Encoded.New.Password.123");
            Mockito.when(userPrivateRepository.save(newUserPrivate))
                    .thenReturn(newUserPrivate);

            assertEquals(newUserPrivate, userPrivateService.updatePassword(userUpdatePasswordCommand));
            Mockito.verify(userPrivateRepository).save(newUserPrivate);
        }

        @Test
        void given_invalidPasswordFormat_when_resetPassword_then_KnowyPasswordFormatException() {
            UserUpdatePasswordCommand userUpdatePasswordCommand = new UserUpdatePasswordCommand(
                    "some-token",
                    "invalidPassword",
                    "invalidPassword"
            );

            assertThrows(
                    KnowyPasswordFormatException.class,
                    () -> userPrivateService.updatePassword(userUpdatePasswordCommand)
            );
        }

        @Test
        void given_mismatchedPasswords_when_resetPassword_then_KnowyTokenException() {
            UserUpdatePasswordCommand userUpdatePasswordCommand = new UserUpdatePasswordCommand(
                    "some-token",
                    "VALID.pass1234",
                    "VALID.diffPass1234"
            );

            assertThrows(
                    KnowyWrongPasswordException.class,
                    () -> userPrivateService.updatePassword(userUpdatePasswordCommand)
            );
        }

        @Test
        void given_tokenWithNotExistUser_when_resetPassword_then_KnowyUserNotFoundException() throws Exception {
            String token = "invalid-token";
            UserUpdatePasswordCommand userUpdatePasswordCommand = new UserUpdatePasswordCommand(
                    token, "VALID.pass123.", "VALID.pass123."
            );
            PasswordResetInfo passwordResetInfo = new PasswordResetInfo(16, "x@x.com");

            Mockito.when(knowyTokenTools.decodeUnverified(token, PasswordResetInfo.class))
                    .thenReturn(passwordResetInfo);
            Mockito.when(userPrivateRepository.findById(passwordResetInfo.userId()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    KnowyUserNotFoundException.class,
                    () -> userPrivateService.updatePassword(userUpdatePasswordCommand)
            );
        }

        @Test
        void given_invalidToken_when_resetPassword_then_KnowyTokenException() throws Exception {
            String token = "invalid-token";
            UserUpdatePasswordCommand userUpdatePasswordCommand = new UserUpdatePasswordCommand(
                    token, "VALID.pass123.", "VALID.pass123."
            );
            PasswordResetInfo passwordResetInfo = new PasswordResetInfo(16, "x@x.com");
            UserPrivate userPrivate = new UserPrivate(
                    16,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("test@email.com"),
                    new Password("ValidPass123@"),
                    true
            );

            Mockito.when(knowyTokenTools.decodeUnverified(token, PasswordResetInfo.class))
                    .thenReturn(passwordResetInfo);
            Mockito.when(userPrivateRepository.findById(16))
                    .thenReturn(Optional.of(userPrivate));
            Mockito.doThrow(new KnowyTokenException("Invalid or expired token"))
                    .when(knowyTokenTools)
                    .decode(userPrivate.password().value(), token, PasswordResetInfo.class);

            assertThrows(
                    KnowyTokenException.class,
                    () -> userPrivateService.updatePassword(userUpdatePasswordCommand)
            );
        }
    }

    @Nested
    class UserUpdateEmailUseCaseTest {
        @Test
        void given_validEmailAndCorrectPassword_when_executeUpdateEmail_then_userEmailUpdated() throws Exception {
            UserUpdateEmailCommand userUpdateEmailCommand = new UserUpdateEmailCommand(
                    16,
                    "new@mail.com",
                    "RAW_PASS"
            );

            UserPrivate userPrivate = new UserPrivate(
                    16,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("old@email.com"),
                    new Password("Encoded.Password.123"),
                    true
            );
            UserPrivate newUserPrivate = new UserPrivate(
                    userPrivate.cropToUser(),
                    new Email("new@mail.com"),
                    userPrivate.password()
            );

            Mockito.when(userPrivateRepository.findById(16))
                    .thenReturn(Optional.of(userPrivate));
            Mockito.when(userPrivateRepository.findByEmail("new@mail.com"))
                    .thenReturn(Optional.empty());

            userPrivateService.updateEmail(userUpdateEmailCommand);
            Mockito.verify(userPrivateRepository, Mockito.times(1))
                    .save(newUserPrivate);
        }

        @Test
        void given_sameEmail_when_updateEmail_then_KnowyUnchangedEmailException() {
            int userId = 16;
            String sameEmail = "same@mail.com";
            UserUpdateEmailCommand userUpdateEmailCommand = new UserUpdateEmailCommand(
                    userId,
                    sameEmail,
                    "RAW_PASS"
            );

            UserPrivate userPrivateResult = new UserPrivate(
                    userId,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email(sameEmail),
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(userPrivateRepository.findById(userId))
                    .thenReturn(Optional.of(userPrivateResult));

            assertThrows(
                    KnowyUnchangedEmailException.class,
                    () -> userPrivateService.updateEmail(userUpdateEmailCommand)
            );
        }

        @Test
        void given_emailAlreadyExists_when_executeUpdateEmail_then_KnowyInvalidUserEmailException() {
            int userId = 16;
            String newMail = "new@mail.com";
            UserUpdateEmailCommand userUpdateEmailCommand = new UserUpdateEmailCommand(
                    userId,
                    newMail,
                    "RAW_PASS"
            );


            UserPrivate userPrivateResult = new UserPrivate(
                    userId,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("old@mail.com"),
                    new Password("Encoded.Password.123"),
                    true
            );

            UserPrivate otherUserPrivate = new UserPrivate(
                    24,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("other@email.com"),
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(userPrivateRepository.findById(userId))
                    .thenReturn(Optional.of(userPrivateResult));
            Mockito.when(userPrivateRepository.findByEmail(newMail))
                    .thenReturn(Optional.of(otherUserPrivate));

            assertThrows(
                    KnowyUserEmailFormatException.class,
                    () -> userPrivateService.updateEmail(userUpdateEmailCommand)
            );
        }

        @Test
        void given_wrongPassword_when_updateEmail_then_throwKnowyWrongPasswordException() throws KnowyWrongPasswordException {
            int userId = 16;
            String newMail = "new@mail.com";
            UserUpdateEmailCommand userUpdateEmailCommand = new UserUpdateEmailCommand(
                    userId,
                    newMail,
                    "RAW_PASS"
            );

            UserPrivate userPrivate = new UserPrivate(
                    userId,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("old@email.com"),
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(userPrivateRepository.findById(userId))
                    .thenReturn(Optional.of(userPrivate));
            Mockito.when(userPrivateRepository.findByEmail(newMail))
                    .thenReturn(Optional.empty());
            Mockito.doThrow(new KnowyWrongPasswordException("Invalid password"))
                    .when(knowyPasswordEncoder)
                    .assertHasPassword(userPrivate, "RAW_PASS");

            assertThrows(
                    KnowyWrongPasswordException.class,
                    () -> userPrivateService.updateEmail(userUpdateEmailCommand)
            );
        }
    }

    @Nested
    class UserPrivateTokenValidationTest {
        @Test
        void given_validToken_when_validateUserToken_then_returnTrue() {
            String token = "valid-token";

            Mockito.when(tokenUserPrivateTool.isValidToken(token))
                    .thenReturn(true);

            assertTrue(userPrivateService.isValidUserToken(token));
        }

        @Test
        void given_invalidToken_when_validateUserToken_then_returnFalse() {
            String token = "valid-token";

            Mockito.when(tokenUserPrivateTool.isValidToken(token))
                    .thenReturn(false);

            assertFalse(userPrivateService.isValidUserToken(token));
        }
    }

    @Nested
    class SendRecoveryPasswordUseCaseTest {
        @Test
        void given_validEmail_when_sendRecoveryPasswordEmail_then_sendRecoveryAccountEmail() throws Exception {
            Email email = new Email("user@mail.com");
            String recoveryBaseUrl = "https://app.url/recover";
            String expectedToken = "mocked-token";

            Mockito.when(tokenUserPrivateTool.createUserTokenByEmail(email))
                    .thenReturn(expectedToken);

            assertDoesNotThrow(() -> userPrivateService.sendRecoveryPasswordEmail(email, recoveryBaseUrl));
            Mockito.verify(knowyEmailClientTool).sendEmail(
                    Mockito.eq(email.value()),
                    Mockito.argThat(subject -> subject != null && !subject.isEmpty()),
                    Mockito.argThat(body -> body.contains(expectedToken) && body.contains(recoveryBaseUrl))
            );
        }

        @Test
        void given_nonExistentEmail_when_sendRecoveryPasswordMailMessage_then_throwKnowyUserNotFoundException()
                throws KnowyTokenException, KnowyUserNotFoundException {

            Email email = new Email("missing@mail.com");
            String recoveryBaseUrl = "https://app.url/recover";

            Mockito.doThrow(KnowyUserNotFoundException.class)
                    .when(tokenUserPrivateTool)
                    .createUserTokenByEmail(email);

            assertThrows(
                    KnowyUserNotFoundException.class,
                    () -> userPrivateService.sendRecoveryPasswordEmail(email, recoveryBaseUrl)
            );
        }

        @Test
        void given_token_when_sendRecoveryPasswordEmailEncodeFail_then_throwKnowyTokenException() throws KnowyTokenException, KnowyUserNotFoundException {
            Email email = new Email("test@mail.com");
            String recoveryBaseUrl = "https://app.url/recover";

            Mockito.doThrow(KnowyTokenException.class)
                    .when(tokenUserPrivateTool)
                    .createUserTokenByEmail(email);

            assertThrows(
                    KnowyTokenException.class,
                    () -> userPrivateService.sendRecoveryPasswordEmail(email, recoveryBaseUrl)
            );
        }

        @Test
        void given_validData_when_sendRecoveryPasswordEmail_then_throwKnowyMailDispatchException()
			throws KnowyTokenException, KnowyUserNotFoundException, KnowyMailDispatchException {

			Email email = new Email("user@mail.com");
            String recoveryBaseUrl = "https://app.url/recover";
            String expectedToken = "mocked-token";

            Mockito.when(tokenUserPrivateTool.createUserTokenByEmail(email))
                    .thenReturn(expectedToken);
            Mockito.doThrow(KnowyMailDispatchException.class)
                    .when(knowyEmailClientTool)
                    .sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

            assertThrows(
                    KnowyMailDispatchException.class,
                    () -> userPrivateService.sendRecoveryPasswordEmail(email, recoveryBaseUrl)
            );
        }
    }

    @Nested
    class DeactivateAccountUseCaseTest {
        @Test
        void given_validEmailAndCorrectPassword_when_desactivateUserAccount_then_deactivateAccountAndSave()
                throws KnowyWrongPasswordException, KnowyUserNotFoundException, KnowyTokenException, KnowyMailDispatchException {

            Email email = new Email("nonExistMail@mail.com");
            Password password = new Password("valid.Password123");
            String baseUrl = "http://app.url";

            DeactivateAccountCommand command = new DeactivateAccountCommand(
                    email, password, password, baseUrl
            );

            UserPrivate userPrivate = new UserPrivate(
                    11,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("user@mail.com"),
                    new Password("Encoded.Password.123"),
                    true
            );
            UserPrivate newUserPrivate = new UserPrivate(
                    userPrivate.cropToUser(),
                    userPrivate.email(),
                    userPrivate.password(),
                    false);
            Mockito.when(userPrivateRepository.findByEmail(email.value()))
                    .thenReturn(Optional.of(userPrivate));

            userPrivateService.desactivateUserAccount(command);
            Mockito.verify(userPrivateRepository, Mockito.times(1))
                    .save(newUserPrivate);
        }

        @Test
        void given_nonexistentUser_when_desactivateUserAccount_then_throwKnowyUserNotFoundException() {
            Email email = new Email("nonExistMail@mail.com");
            Password password = new Password("valid.Pasword123");
            String baseUrl = "http://app.url";

            DeactivateAccountCommand command = new DeactivateAccountCommand(
                    email, password, password, baseUrl
            );

            Mockito.when(userPrivateRepository.findByEmail(email.value()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    KnowyUserNotFoundException.class,
                    () -> userPrivateService.desactivateUserAccount(command)
            );
            Mockito.verify(userPrivateRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void given_validEmailButPasswordsDoNotMatch_when_desactivateUserAccount_then_throwKnowyWrongPasswordException() {
            Email email = new Email("user@mail.com");
            Password password = new Password("Valid123@");
            Password passwordConfirm = new Password("ValidDiff123@");
            String baseUrl = "http://app.url";

            DeactivateAccountCommand command = new DeactivateAccountCommand(
                    email, password, passwordConfirm, baseUrl
            );

            assertThrows(
                    KnowyWrongPasswordException.class,
                    () -> userPrivateService.desactivateUserAccount(command)
            );
            Mockito.verify(userPrivateRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void given_validEmailAndWrongPassword_when_desactivateUserAccount_then_throwKnowyWrongPasswordException()
                throws KnowyWrongPasswordException {

            Email email = new Email("user@mail.com");
            Password wrongPassword = new Password("wrong.Password.123");
            String baseUrl = "http://app.url";

            DeactivateAccountCommand command = new DeactivateAccountCommand(
                    email, wrongPassword, wrongPassword, baseUrl
            );

            UserPrivate userPrivate = new UserPrivate(
                    11,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    email,
                    new Password("Encoded.Password.123"),
                    true
            );
            Mockito.when(userPrivateRepository.findByEmail("user@mail.com"))
                    .thenReturn(Optional.of(userPrivate));
            Mockito.doThrow(KnowyWrongPasswordException.class)
                    .when(knowyPasswordEncoder)
                    .assertHasPassword(userPrivate, wrongPassword.value());

            assertThrows(
                    KnowyWrongPasswordException.class,
                    () -> userPrivateService.desactivateUserAccount(command)
            );
        }

        @Test
        void given_validEmail_when_createDeletedAccountEmail_then_throwsKnowyTokenException()
                throws KnowyTokenException, KnowyUserNotFoundException {

            Email email = new Email("nonExistMail@mail.com");
            Password password = new Password("valid.Pasword123");
            String baseUrl = "http://app.url";

            DeactivateAccountCommand command = new DeactivateAccountCommand(
                    email, password, password, baseUrl
            );

            UserPrivate userPrivate = new UserPrivate(
                    11,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("user@mail.com"),
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(userPrivateRepository.findByEmail(email.value()))
                    .thenReturn(Optional.of(userPrivate));
            Mockito.doThrow(KnowyTokenException.class)
                    .when(tokenUserPrivateTool)
                    .createUserTokenByEmail(Mockito.eq(email), Mockito.anyLong());

            assertThrows(
                    KnowyTokenException.class,
                    () -> userPrivateService.desactivateUserAccount(command)
            );
        }

        @Test
        void given_nonExistingEmail_when_createDeletedAccountEmail_then_throwsKnowyUserNotFoundException()
                throws KnowyTokenException, KnowyUserNotFoundException {

            Email email = new Email("nonExistMail@mail.com");
            Password password = new Password("valid.Pasword123");
            String baseUrl = "http://app.url";

            DeactivateAccountCommand command = new DeactivateAccountCommand(
                    email, password, password, baseUrl
            );

            UserPrivate userPrivate = new UserPrivate(
                    11,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("user@mail.com"),
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(userPrivateRepository.findByEmail(email.value()))
                    .thenReturn(Optional.of(userPrivate));
            Mockito.doThrow(KnowyUserNotFoundException.class)
                    .when(tokenUserPrivateTool)
                    .createUserTokenByEmail(Mockito.eq(email), Mockito.anyLong());

            assertThrows(
                    KnowyUserNotFoundException.class,
                    () -> userPrivateService.desactivateUserAccount(command)
            );
        }

        @Test
        void given_validEmailAndCorrectPassword_when_desactivateUserAccount_then_throw() throws KnowyMailDispatchException {

            Email email = new Email("nonExistMail@mail.com");
            Password password = new Password("valid.Pasword123");
            String baseUrl = "http://app.url";

            DeactivateAccountCommand command = new DeactivateAccountCommand(
                    email, password, password, baseUrl
            );

            UserPrivate userPrivate = new UserPrivate(
                    11,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("user@mail.com"),
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(userPrivateRepository.findByEmail(email.value()))
                    .thenReturn(Optional.of(userPrivate));
            Mockito.doThrow(KnowyMailDispatchException.class)
                    .when(knowyEmailClientTool)
                    .sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

            assertThrows(
                    KnowyMailDispatchException.class,
                    () -> userPrivateService.desactivateUserAccount(command)
            );
        }
    }

    @Nested
    class ReactivateAccountUseCaseTest {
        @Test
        void given_validTokenAndUserInactive_when_reactivateAccount_thenReactivateAccount() throws Exception {
            String token = "valid-token";

            UserPrivate userPrivate = new UserPrivate(
                    11,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("user@mail.com"),
                    new Password("Encoded.Password.123"),
                    false
            );
            UserPrivate newUserPrivate = new UserPrivate(
                    userPrivate.cropToUser(),
                    new Email("user@mail.com"), new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(tokenUserPrivateTool.verifyPasswordToken(token))
                    .thenReturn(userPrivate);

            userPrivateService.reactivateUserAccount(token);
            Mockito.verify(userPrivateRepository).save(newUserPrivate);
        }

        @Test
        void given_invalidToken_when_reactivateUserAccount_then_throwKnowyTokenException() throws KnowyTokenException, KnowyUserNotFoundException {
            String token = "invalid-token";

            Mockito.doThrow(new KnowyTokenException("Invalid token"))
                    .when(tokenUserPrivateTool)
                    .verifyPasswordToken(token);

            assertThrows(
                    KnowyTokenException.class,
                    () -> userPrivateService.reactivateUserAccount(token)
            );
        }

        @Test
        void given_validTokenButUserNotFound_when_reactivateUserAccount_then_throwKnowyUserNotFoundException() throws KnowyTokenException, KnowyUserNotFoundException {
            String token = "valid-token-with-no-registry";


            Mockito.doThrow(KnowyUserNotFoundException.class)
                    .when(tokenUserPrivateTool)
                    .verifyPasswordToken(token);

            assertThrows(
                    KnowyUserNotFoundException.class,
                    () -> userPrivateService.reactivateUserAccount(token)
            );
        }

        @Test
        void given_validTokenAndUserAlreadyActive_when_reactivateUserAccount_then_doNothing() throws Exception {
            String token = "valid-token-with-active-user";

            UserPrivate userPrivate = new UserPrivate(
                    11,
                    "TestNickname",
                    new ProfileImage(1, "https://knowy/image.png"),
                    new HashSet<>(),
                    new Email("user@mail.com"),
                    new Password("Encoded.Password.123"),
                    true
            );

            Mockito.when(tokenUserPrivateTool.verifyPasswordToken(token))
                    .thenReturn(userPrivate);

            userPrivateService.reactivateUserAccount(token);
            Mockito.verify(userPrivateRepository, Mockito.never()).save(Mockito.any());
        }
    }
}
