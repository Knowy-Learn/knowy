package com.knowy.server.application.usecase.manage;

import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.application.util.TokenUserPrivateTool;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ReactivateAccountUseCaseTest {

	@Mock
	private TokenUserPrivateTool tokenUserPrivateTool;

	@Mock
	private UserPrivateRepository userPrivateRepository;

	@InjectMocks
	private ReactivateAccountUseCase reactivateAccountUseCase;

	@Test
	void given_validTokenAndUserInactive_when_reactivateAccount_thenReactivateAccount() throws Exception {
		String token = "valid-token";

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

		Mockito.when(tokenUserPrivateTool.verifyPasswordToken(token))
			.thenReturn(userPrivate);

		reactivateAccountUseCase.execute(token);
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
			() -> reactivateAccountUseCase.execute(token)
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
			() -> reactivateAccountUseCase.execute(token)
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
			new Password("ENCODED_PASSWORD"),
			true
		);

		Mockito.when(tokenUserPrivateTool.verifyPasswordToken(token))
			.thenReturn(userPrivate);

		reactivateAccountUseCase.execute(token);
		Mockito.verify(userPrivateRepository, Mockito.never()).save(Mockito.any());
	}
}
