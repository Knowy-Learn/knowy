package com.knowy.server.application.usecase.manage;

import com.knowy.server.application.exception.KnowyMailDispatchException;
import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.ports.KnowyEmailClientTool;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.application.util.TokenUserPrivateTool;
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
class DeactivateAccountUseCaseTest {

	@Mock
	PasswordEncoderAdapter passwordEncoderAdapter;

	@Mock
	UserPrivateRepository userPrivateRepository;

	@Mock
	private KnowyEmailClientTool knowyEmailClientTool;

	@Mock
	private TokenUserPrivateTool tokenUserPrivateTool;

	@InjectMocks
	private DeactivateAccountUseCase deactivateAccountUseCase;

	@Test
	void given_validEmailAndCorrectPassword_when_desactivateUserAccount_then_deactivateAccountAndSave()
		throws KnowyWrongPasswordException, KnowyUserNotFoundException, KnowyTokenException, KnowyMailDispatchException {

		Email email = new Email("nonExistMail@mail.com");
		Password password = new Password("validPasword");
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
			new Password("ENCODED_PASSWORD"),
			true
		);
		UserPrivate newUserPrivate = new UserPrivate(
			userPrivate.cropToUser(),
			userPrivate.email(),
			userPrivate.password(),
			false);
		Mockito.when(userPrivateRepository.findByEmail(email.value()))
			.thenReturn(Optional.of(userPrivate));

		deactivateAccountUseCase.execute(command);

		Mockito.verify(userPrivateRepository, Mockito.times(1))
			.save(newUserPrivate);
	}

	@Test
	void given_nonexistentUser_when_desactivateUserAccount_then_throwKnowyUserNotFoundException() {
		Email email = new Email("nonExistMail@mail.com");
		Password password = new Password("validPasword");
		String baseUrl = "http://app.url";

		DeactivateAccountCommand command = new DeactivateAccountCommand(
			email, password, password, baseUrl
		);

		Mockito.when(userPrivateRepository.findByEmail(email.value()))
			.thenReturn(Optional.empty());

		assertThrows(
			KnowyUserNotFoundException.class,
			() -> deactivateAccountUseCase.execute(command)
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
			() -> deactivateAccountUseCase.execute(command)
		);
		Mockito.verify(userPrivateRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	void given_validEmailAndWrongPassword_when_desactivateUserAccount_then_throwKnowyWrongPasswordException()
		throws KnowyWrongPasswordException {

		Email email = new Email("user@mail.com");
		Password wrongPassword = new Password("wrongPassword");
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
			new Password("ENCODED_PASSWORD"),
			true
		);
		Mockito.when(userPrivateRepository.findByEmail("user@mail.com"))
			.thenReturn(Optional.of(userPrivate));
		Mockito.doThrow(KnowyWrongPasswordException.class)
			.when(passwordEncoderAdapter)
			.assertHasPassword(userPrivate, wrongPassword.value());

		assertThrows(
			KnowyWrongPasswordException.class,
			() -> deactivateAccountUseCase.execute(command)
		);
	}

	@Test
	void given_validEmail_when_createDeletedAccountEmail_then_throwsKnowyTokenException()
		throws KnowyTokenException, KnowyUserNotFoundException {

		Email email = new Email("nonExistMail@mail.com");
		Password password = new Password("validPasword");
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
			new Password("ENCODED_PASSWORD"),
			true
		);

		Mockito.when(userPrivateRepository.findByEmail(email.value()))
			.thenReturn(Optional.of(userPrivate));
		Mockito.doThrow(KnowyTokenException.class)
			.when(tokenUserPrivateTool)
			.createUserTokenByEmail(Mockito.eq(email), Mockito.anyLong());

		assertThrows(
			KnowyTokenException.class,
			() -> deactivateAccountUseCase.execute(command)
		);
	}

	@Test
	void given_nonExistingEmail_when_createDeletedAccountEmail_then_throwsKnowyUserNotFoundException()
		throws KnowyTokenException, KnowyUserNotFoundException {

		Email email = new Email("nonExistMail@mail.com");
		Password password = new Password("validPasword");
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
			new Password("ENCODED_PASSWORD"),
			true
		);

		Mockito.when(userPrivateRepository.findByEmail(email.value()))
			.thenReturn(Optional.of(userPrivate));
		Mockito.doThrow(KnowyUserNotFoundException.class)
			.when(tokenUserPrivateTool)
			.createUserTokenByEmail(Mockito.eq(email), Mockito.anyLong());

		assertThrows(
			KnowyUserNotFoundException.class,
			() -> deactivateAccountUseCase.execute(command)
		);
	}

	@Test
	void given_validEmailAndCorrectPassword_when_desactivateUserAccount_then_throw() throws KnowyMailDispatchException {

		Email email = new Email("nonExistMail@mail.com");
		Password password = new Password("validPasword");
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
			new Password("ENCODED_PASSWORD"),
			true
		);

		Mockito.when(userPrivateRepository.findByEmail(email.value()))
			.thenReturn(Optional.of(userPrivate));
		Mockito.doThrow(KnowyMailDispatchException.class)
			.when(knowyEmailClientTool)
			.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		assertThrows(
			KnowyMailDispatchException.class,
			() -> deactivateAccountUseCase.execute(command)
		);
	}
}
