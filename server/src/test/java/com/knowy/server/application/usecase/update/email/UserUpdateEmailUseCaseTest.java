package com.knowy.server.application.usecase.update.email;

import com.knowy.server.application.exception.validation.user.KnowyInvalidUserEmailException;
import com.knowy.server.application.exception.validation.user.KnowyUnchangedEmailException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.Password;
import com.knowy.server.domain.ProfileImage;
import com.knowy.server.domain.UserPrivate;
import com.knowy.server.infrastructure.adapters.security.PasswordEncoderAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserUpdateEmailUseCaseTest {

	@Mock
	private UserPrivateRepository userPrivateRepository;

	@Mock
	private PasswordEncoderAdapter passwordEncoderAdapter;

	@InjectMocks
	private UserUpdateEmailUseCase userUpdateEmailUseCase;

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
			new Password("ENCODED_PASS"),
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

		userUpdateEmailUseCase.execute(userUpdateEmailCommand);
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
			new Password("ENCODED_PASS"),
			true
		);

		Mockito.when(userPrivateRepository.findById(userId))
			.thenReturn(Optional.of(userPrivateResult));

		assertThrows(
			KnowyUnchangedEmailException.class,
			() -> userUpdateEmailUseCase.execute(userUpdateEmailCommand)
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
			new Password("ENCODED_PASS"),
			true
		);

		UserPrivate otherUserPrivate = new UserPrivate(
			24,
			"TestNickname",
			new ProfileImage(1, "https://knowy/image.png"),
			new HashSet<>(),
			new Email("other@email.com"),
			new Password("ENCODED_PASS"),
			true
		);

		Mockito.when(userPrivateRepository.findById(userId))
			.thenReturn(Optional.of(userPrivateResult));
		Mockito.when(userPrivateRepository.findByEmail(newMail))
			.thenReturn(Optional.of(otherUserPrivate));

		assertThrows(
			KnowyInvalidUserEmailException.class,
			() -> userUpdateEmailUseCase.execute(userUpdateEmailCommand)
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
                new Password("ENCODED_PASS"),
                true
        );

        Mockito.when(userPrivateRepository.findById(userId))
                .thenReturn(Optional.of(userPrivate));
        Mockito.when(userPrivateRepository.findByEmail(newMail))
                .thenReturn(Optional.empty());
        Mockito.doThrow(new KnowyWrongPasswordException("Invalid password"))
                .when(passwordEncoderAdapter)
                .assertHasPassword(userPrivate, "RAW_PASS");

        assertThrows(
                KnowyWrongPasswordException.class,
                () -> userUpdateEmailUseCase.execute(userUpdateEmailCommand)
        );
    }
}
