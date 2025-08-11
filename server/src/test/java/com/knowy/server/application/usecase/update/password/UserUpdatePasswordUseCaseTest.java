package com.knowy.server.application.usecase.update.password;

import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyPasswordFormatException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.model.PasswordResetInfo;
import com.knowy.server.application.ports.KnowyTokenTools;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.domain.*;
import com.knowy.server.infrastructure.adapters.security.PasswordEncoderAdapter;
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

@ExtendWith(MockitoExtension.class)
class UserUpdatePasswordUseCaseTest {

	@Mock
	private UserPrivateRepository userPrivateRepository;

	@Mock
	private PasswordEncoderAdapter passwordEncoderAdapter;

	@Mock
	private KnowyTokenTools knowyTokenTools;

	@InjectMocks
	private UserUpdatePasswordUseCase userUpdatePasswordUseCase;

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
			new Password("ENCODED_NEW_PASSWORD"),
			true
		);
		PasswordResetInfo passwordResetInfo = new PasswordResetInfo(11, "user@mail.com");

		Mockito.when(knowyTokenTools.decodeUnverified("valid-token", PasswordResetInfo.class))
			.thenReturn(passwordResetInfo);
		Mockito.when(userPrivateRepository.findById(userPrivate.id()))
			.thenReturn(Optional.of(userPrivate));
		Mockito.when(knowyTokenTools.decode(userPrivate.password().value(), "valid-token", PasswordResetInfo.class))
			.thenReturn(passwordResetInfo);
		Mockito.when(passwordEncoderAdapter.encode("ValidNewPass123@"))
			.thenReturn("ENCODED_NEW_PASSWORD");
		Mockito.when(userPrivateRepository.save(newUserPrivate))
			.thenReturn(newUserPrivate);

		assertEquals(newUserPrivate, userUpdatePasswordUseCase.execute(userUpdatePasswordCommand));
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
			() -> userUpdatePasswordUseCase.execute(userUpdatePasswordCommand)
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
			() -> userUpdatePasswordUseCase.execute(userUpdatePasswordCommand)
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
			() -> userUpdatePasswordUseCase.execute(userUpdatePasswordCommand)
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
			() -> userUpdatePasswordUseCase.execute(userUpdatePasswordCommand)
		);
	}
}
