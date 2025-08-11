package com.knowy.server.application;


import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserEmailException;
import com.knowy.server.application.exception.validation.user.KnowyUnchangedEmailException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.model.MailMessage;
import com.knowy.server.application.model.PasswordResetInfo;
import com.knowy.server.application.ports.KnowyPasswordEncoder;
import com.knowy.server.application.ports.KnowyTokenTools;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.Password;
import com.knowy.server.domain.ProfileImage;
import com.knowy.server.domain.UserPrivate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserPrivateServiceTest {

    @Mock
    private UserPrivateRepository userPrivateRepository;

    @Mock
    private KnowyPasswordEncoder passwordEncoder;

    @Mock
    private KnowyTokenTools tokenTools;

    @Spy
    @InjectMocks
    private UserPrivateService userPrivateService;


    // method isValidToken
    @Test
    void given_nullToken_when_validateToke_then_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> userPrivateService.isValidToken(null));
    }

    @Test
    void givenValidTokenExpectTrue() throws KnowyTokenException {
        PasswordResetInfo passwordResetInfo = new PasswordResetInfo(11, "user@mail.com");
        UserPrivate userPrivate = new UserPrivate(
                11,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("test@email.com"),
                new Password("ValidOldPass123@"),
                true
        );

        Mockito.when(tokenTools.decodeUnverified("valid-token", PasswordResetInfo.class))
                .thenReturn(passwordResetInfo);
        Mockito.when(userPrivateRepository.findById(userPrivate.id()))
                .thenReturn(Optional.of(userPrivate));
        Mockito.when(tokenTools.decode(userPrivate.password().value(), "valid-token", PasswordResetInfo.class))
                .thenReturn(passwordResetInfo);

        assertTrue(userPrivateService.isValidToken("valid-token"));
    }

    @Test
    void given_invalidToken_when_validateToken_then_returnsFalse() throws KnowyTokenException, KnowyUserNotFoundException {
        Mockito.doThrow(new KnowyTokenException("Invalid Token"))
                .when(userPrivateService)
                .verifyPasswordToken("invalid-token");

        boolean result = userPrivateService.isValidToken("invalid-token");
        assertFalse(result);
    }

    @Test
    void given_invalidTokenByUserNotFound_when_validateToken_then_returnsFalse()
            throws KnowyTokenException, KnowyUserNotFoundException {
        Mockito.doThrow(new KnowyUserNotFoundException("Invalid Token"))
                .when(userPrivateService)
                .verifyPasswordToken("invalid-token");

        boolean result = userPrivateService.isValidToken("invalid-token");
        assertFalse(result);
    }

    // mehotd getUserPrivateByEmail
    @Test
    void given_existingEmail_when_getUserPrivateByEmail_then_returnUserPrivate() {
        UserPrivate userPrivate = new UserPrivate(
                11,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("test@email.com"),
                new Password("ValidOldPass123@"),
                true
        );

        Mockito.when(userPrivateRepository.findByEmail("exist@gmail.com"))
                .thenReturn(Optional.of(userPrivate));

        UserPrivate result = assertDoesNotThrow(() -> userPrivateService.getUserPrivateByEmail("exist@gmail.com"));
        assertEquals(userPrivate, result);
    }

    @Test
    void given_nonExistingEmail_when_getUserPrivateByEmail_then_throwKnowyUserNotFoundException() {
        Mockito.when(userPrivateRepository.findByEmail("nonExistg@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                KnowyUserNotFoundException.class,
                () -> userPrivateService.getUserPrivateByEmail("nonExistg@mail.com")
        );
    }

    // method createRecoveryPasswordEmail
    @Test
    void givenNonExistentEmailExpectUserNotFoundException() {
        String email = "missing@mail.com";
        String recoveryBaseUrl = "https://app.url/recover";

        Mockito.when(userPrivateRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                KnowyUserNotFoundException.class,
                () -> userPrivateService.createRecoveryPasswordEmail(email, recoveryBaseUrl)
        );
    }

    @Test
    void given_token_when_createRecoveryPasswordEmailencodeFail_then_throwKnowyTokenException() throws Exception {
        UserPrivate userPrivate = new UserPrivate(
                11,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("user@mail.com"),
                new Password("ENCODED_PASSWORD"),
                true
        );
        Mockito.when(userPrivateRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(userPrivate));
        Mockito.when(tokenTools.encode(Mockito.any(), Mockito.eq(userPrivate.password().value()), Mockito.anyLong()))
                .thenThrow(new KnowyTokenException("Token encoding failed"));

        assertThrows(
                KnowyTokenException.class,
                () -> userPrivateService.createRecoveryPasswordEmail("user@mail.com", "https://app.url/recover")
        );
    }

    @Test
    void given_validEmail_when_createRecoveryPasswordEmail_then_returnMailMessage() throws Exception {
        String email = "user@mail.com";
        String recoveryBaseUrl = "https://app.url/recover";
        String expectedToken = "mocked-token";

        UserPrivate userPrivate = new UserPrivate(
                11,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("user@mail.com"),
                new Password("ENCODED_PASSWORD"),
                true
        );

        Mockito.when(userPrivateRepository.findByEmail(email))
                .thenReturn(Optional.of(userPrivate));
        Mockito.when(tokenTools.encode(Mockito.any(), Mockito.eq(userPrivate.password().value()), Mockito.anyLong()))
                .thenReturn(expectedToken);

        MailMessage mailMessage = userPrivateService.createRecoveryPasswordEmail(email, recoveryBaseUrl);
        assertEquals(email, mailMessage.to());
        assertNotNull(mailMessage.subject());
        assertFalse(mailMessage.subject().isEmpty());
        assertTrue(mailMessage.body().contains(expectedToken));
        assertTrue(mailMessage.body().contains(recoveryBaseUrl));
    }

    // method createDeletedAccountEmail
    @Test
    void given_nonExistingEmail_when_createDeletedAccountEmail_then_throwsKnowyUserNotFoundException() {
        Mockito.when(userPrivateRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                KnowyUserNotFoundException.class,
                () -> userPrivateService.createDeletedAccountEmail("notfound@example.com", "http://app.url")
        );
    }

    @Test
    void given_validEmail_when_createDeletedAccountEmail_thenReturnMailMessage() throws Exception {
        UserPrivate userPrivate = new UserPrivate(
                11,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("user@mail.com"),
                new Password("ENCODED_PASSWORD"),
                true
        );

        Mockito.when(userPrivateRepository.findByEmail("email@example.com"))
                .thenReturn(Optional.of(userPrivate));
        Mockito.when(tokenTools.encode(
                        Mockito.any(PasswordResetInfo.class),
                        Mockito.eq("ENCODED_PASSWORD"),
                        Mockito.anyLong())
                )
                .thenReturn("mocked-token");

        MailMessage mailMessage = userPrivateService.createDeletedAccountEmail("email@example.com", "http://app.url");

        assertEquals("email@example.com", mailMessage.to());
        assertNotNull(mailMessage.subject());
        assertFalse(mailMessage.subject().isEmpty());
        assertTrue(mailMessage.body().contains("mocked-token"));
        assertTrue(mailMessage.body().contains("http://app.url"));
        Mockito.verify(tokenTools, Mockito.times(1))
                .encode(Mockito.any(PasswordResetInfo.class), Mockito.eq("ENCODED_PASSWORD"), Mockito.anyLong());
    }

    // method desactivateUserAccount
    @Test
    void given_validEmailAndCorrectPassword_when_desactivateUserAccount_then_deactivateAccountAndSave()
            throws KnowyWrongPasswordException, KnowyUserNotFoundException {

        String email = "user@mail.com";
        String password = "correctPass";
        String confirmPassword = "correctPass";

        UserPrivate userPrivate = new UserPrivate(
                11,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("user@mail.com"),
                new Password("ENCODED_PASSWORD"),
                true
        );
        UserPrivate newUserPrivate = new UserPrivate(
                userPrivate.cropToUser(),
                userPrivate.email(),
                userPrivate.password(),
                false);
        Mockito.when(userPrivateRepository.findByEmail(email))
                .thenReturn(Optional.of(userPrivate));

        userPrivateService.desactivateUserAccount(email, password, confirmPassword);

        Mockito.verify(userPrivateRepository, Mockito.times(1))
                .save(newUserPrivate);
        assertFalse(newUserPrivate.active());
    }

    @Test
    void given_nonexistentUser_when_desactivateUserAccount_then_throwKnowyUserNotFoundException() {
        String email = "nonexistent@mail.com";
        String password = "pass";
        String confirmPassword = "pass";

        Mockito.when(userPrivateRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                KnowyUserNotFoundException.class,
                () -> userPrivateService.desactivateUserAccount(email, password, confirmPassword)
        );
        Mockito.verify(userPrivateRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void given_validEmailButPasswordsDoNotMatch_when_desactivateUserAccount_then_throwKnowyWrongPasswordException() {
        assertThrows(
                KnowyWrongPasswordException.class,
                () -> userPrivateService.desactivateUserAccount("test@mail.com", "VALID.pass1", "VALID.pass2")
        );
        Mockito.verify(userPrivateRepository, Mockito.never()).save(Mockito.any());
    }

/*    @Test
    void given_validEmailAndWrongPassword_when_desactivateUserAccount_then_throwKnowyWrongPasswordException()
            throws KnowyWrongPasswordException {

        UserPrivate userPrivate = new UserPrivate(
                11,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("user@mail.com"),
                "ENCODED_PASSWORD",
                true
        );
        Mockito.when(userPrivateRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(userPrivate));
        Mockito.doThrow(KnowyWrongPasswordException.class)
                .when(passwordChecker)
                .assertHasPassword(userPrivate, "wrongPass");

        assertThrows(
                KnowyWrongPasswordException.class,
                () -> userPrivateService.desactivateUserAccount("user@mail.com", "wrongPass", "wrongPass")
        );
    }*/

    // method reactivateUserAccount
    @Test
    void givenValidTokenAndUserInactiveExpectActivateAndSave() throws Exception {
        String token = "valid-token";

        PasswordResetInfo passwordResetInfo = new PasswordResetInfo(11, "user@mail.com");
        UserPrivate userPrivate = new UserPrivate(
                11,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("user@mail.com"),
                new Password("ENCODED_PASSWORD"),
                false
        );
        UserPrivate newUserPrivate = new UserPrivate(
                userPrivate.cropToUser(),
                new Email("user@mail.com"), new Password("ENCODED_PASSWORD"),
                true
        );
        Mockito.when(tokenTools.decodeUnverified(token, PasswordResetInfo.class))
                .thenReturn(passwordResetInfo);
        Mockito.when(userPrivateRepository.findById(11))
                .thenReturn(Optional.of(userPrivate));
        Mockito.when(tokenTools.decode(Mockito.anyString(), Mockito.eq(token), Mockito.eq(PasswordResetInfo.class)))
                .thenReturn(passwordResetInfo);

        userPrivateService.reactivateUserAccount(token);
        Mockito.verify(userPrivateRepository).save(newUserPrivate);
    }

    @Test
    void given_invalidToken_when_reactivateUserAccount_then_throwKnowyTokenException() throws KnowyTokenException {
        String token = "invalid-token";

        Mockito.doThrow(new KnowyTokenException("Invalid token"))
                .when(tokenTools)
                .decodeUnverified(Mockito.eq(token), Mockito.any());

        assertThrows(
                KnowyTokenException.class,
                () -> userPrivateService.reactivateUserAccount(token)
        );
    }

    @Test
    void given_validTokenButUserNotFound_when_reactivateUserAccount_then_throwKnowyUserNotFoundException() throws KnowyTokenException {
        PasswordResetInfo passwordResetInfo = new PasswordResetInfo(99, "email@example.com");
        Mockito.when(tokenTools.decodeUnverified("valid-token", PasswordResetInfo.class))
                .thenReturn(passwordResetInfo);
        Mockito.when(userPrivateRepository.findById(99))
                .thenReturn(Optional.empty());

        assertThrows(
                KnowyUserNotFoundException.class,
                () -> userPrivateService.reactivateUserAccount("valid-token")
        );
    }

    @Test
    void given_validTokenAndUserAlreadyActive_when_reactivateUserAccount_then_doNothing() throws Exception {
        String token = "valid-token";

        PasswordResetInfo passwordResetInfo = new PasswordResetInfo(11, "email@example.com");
        UserPrivate userPrivate = new UserPrivate(
                11,
                "TestNickname",
                new ProfileImage(1, "https://knowy/image.png"),
                new HashSet<>(),
                new Email("user@mail.com"),
                new Password("ENCODED_PASSWORD"),
                true
        );
        Mockito.when(tokenTools.decodeUnverified(token, PasswordResetInfo.class))
                .thenReturn(passwordResetInfo);
        Mockito.when(userPrivateRepository.findById(11))
                .thenReturn(Optional.of(userPrivate));
        Mockito.when(tokenTools.decode(Mockito.anyString(), Mockito.eq(token), Mockito.eq(PasswordResetInfo.class)))
                .thenReturn(passwordResetInfo);

        userPrivateService.reactivateUserAccount(token);
        Mockito.verify(userPrivateRepository, Mockito.never()).save(Mockito.any());
    }
}
