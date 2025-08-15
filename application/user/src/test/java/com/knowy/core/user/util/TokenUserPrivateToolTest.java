package com.knowy.core.user.util;

import com.knowy.core.user.exception.KnowyTokenException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Password;
import com.knowy.core.user.domain.ProfileImage;
import com.knowy.core.user.domain.UserPrivate;
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
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class TokenUserPrivateToolTest {

	@Mock
	private KnowyTokenTools tokenTools;

	@Mock
	private UserPrivateRepository userPrivateRepository;

	@Spy
	@InjectMocks
	private TokenUserPrivateTool tokenUserPrivateTool;

	// method verificationToken
	@Test
	void given_validToken_when_validateToken_then_returnTrue() throws KnowyTokenException {
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

		assertTrue(tokenUserPrivateTool.isValidToken("valid-token"));
	}

	@Test
	void given_nullToken_when_validateToke_then_throwNullPointerException() {
		assertThrows(NullPointerException.class, () -> tokenUserPrivateTool.isValidToken(null));
	}


	@Test
	void given_invalidToken_when_validateToken_then_returnsFalse() throws KnowyTokenException, KnowyUserNotFoundException {
		Mockito.doThrow(new KnowyTokenException("Invalid Token"))
			.when(tokenUserPrivateTool)
			.verifyPasswordToken("invalid-token");

		boolean result = tokenUserPrivateTool.isValidToken("invalid-token");
		assertFalse(result);
	}

	@Test
	void given_invalidTokenByUserNotFound_when_validateToken_then_returnsFalse()
		throws KnowyTokenException, KnowyUserNotFoundException {
		Mockito.doThrow(new KnowyUserNotFoundException("Invalid Token"))
			.when(tokenUserPrivateTool)
			.verifyPasswordToken("invalid-token");

		boolean result = tokenUserPrivateTool.isValidToken("invalid-token");
		assertFalse(result);
	}

	// method createUserTokenByEmail
	@Test
	void given_email_when_createUserToken_then_returnToken() throws KnowyTokenException {
		Email userEmail = new Email("test@email.com");
		UserPrivate userPrivate = new UserPrivate(
			11,
			"TestNickname",
			new ProfileImage(1, "https://knowy/image.png"),
			new HashSet<>(),
			userEmail,
			new Password("ValidOldPass123@"),
			true
		);

		Mockito.when(userPrivateRepository.findByEmail(userEmail.value()))
			.thenReturn(Optional.of(userPrivate));

		PasswordResetInfo passwordResetInfo = new PasswordResetInfo(userPrivate.id(), userPrivate.email().value());

		assertDoesNotThrow(() -> tokenUserPrivateTool.createUserTokenByEmail(userEmail));
		Mockito.verify(tokenTools).encode(passwordResetInfo, userPrivate.password().value(), 600_000);
	}

	@Test
	void given_email_when_createUserToken_then_throwKnowyUserNotFound() {
		Email userEmail = new Email("test@email.com");

		Mockito.when(userPrivateRepository.findByEmail(userEmail.value()))
			.thenReturn(Optional.empty());

		assertThrows(
			KnowyUserNotFoundException.class,
			() -> tokenUserPrivateTool.createUserTokenByEmail(userEmail)
		);
	}

	@Test
	void given_email_when_createUserToken_then_throwKnowyTokenException() throws KnowyTokenException {
		Email userEmail = new Email("test@email.com");
		UserPrivate userPrivate = new UserPrivate(
			11,
			"TestNickname",
			new ProfileImage(1, "https://knowy/image.png"),
			new HashSet<>(),
			userEmail,
			new Password("ValidOldPass123@"),
			true
		);
		PasswordResetInfo passwordResetInfo = new PasswordResetInfo(userPrivate.id(), userPrivate.email().value());

		Mockito.when(userPrivateRepository.findByEmail(userEmail.value()))
			.thenReturn(Optional.of(userPrivate));
		Mockito.doThrow(KnowyTokenException.class)
			.when(tokenTools)
			.encode(Mockito.eq(passwordResetInfo), Mockito.eq(userPrivate.password().value()), Mockito.anyLong());

		assertThrows(
			KnowyTokenException.class,
			() -> tokenUserPrivateTool.createUserTokenByEmail(userEmail)
		);
		Mockito.verify(tokenTools).encode(passwordResetInfo, userPrivate.password().value(), 600_000);
	}
}
