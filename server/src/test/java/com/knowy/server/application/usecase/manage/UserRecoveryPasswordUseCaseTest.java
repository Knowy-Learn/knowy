package com.knowy.server.application.usecase.manage;

import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.model.MailMessage;
import com.knowy.server.application.util.TokenUserPrivateTool;
import com.knowy.server.domain.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserRecoveryPasswordUseCaseTest {

	@Mock
	private TokenUserPrivateTool tokenUserPrivateTool;

	@InjectMocks
	private UserRecoveryPasswordUseCase userRecoveryPasswordUseCase;

	@Test
	void given_validEmail_when_createRecoveryPasswordEmail_then_returnMailMessage() throws Exception {
		Email email = new Email("user@mail.com");
		String recoveryBaseUrl = "https://app.url/recover";
		String expectedToken = "mocked-token";

		Mockito.when(tokenUserPrivateTool.createUserTokenByEmail(email))
			.thenReturn(expectedToken);

		MailMessage mailMessage = userRecoveryPasswordUseCase.execute(email, recoveryBaseUrl);
		assertEquals(email.value(), mailMessage.to());
		assertNotNull(mailMessage.subject());
		assertFalse(mailMessage.subject().isEmpty());
		assertTrue(mailMessage.body().contains(expectedToken));
		assertTrue(mailMessage.body().contains(recoveryBaseUrl));
	}


	@Test
	void given_nonExistentEmail_when_createRecoveryPasswordMailMessage_then_throwKnowyUserNotFoundException()
		throws KnowyTokenException, KnowyUserNotFoundException {

		Email email = new Email("missing@mail.com");
		String recoveryBaseUrl = "https://app.url/recover";

		Mockito.doThrow(KnowyUserNotFoundException.class)
			.when(tokenUserPrivateTool)
			.createUserTokenByEmail(email);

		assertThrows(
			KnowyUserNotFoundException.class,
			() -> userRecoveryPasswordUseCase.execute(email, recoveryBaseUrl)
		);
	}

	@Test
	void given_token_when_createRecoveryPasswordEmailEncodeFail_then_throwKnowyTokenException() throws KnowyTokenException, KnowyUserNotFoundException {
		Email email = new Email("test@mail.com");
		String recoveryBaseUrl = "https://app.url/recover";

		Mockito.doThrow(KnowyTokenException.class)
			.when(tokenUserPrivateTool)
			.createUserTokenByEmail(email);

		assertThrows(
			KnowyTokenException.class,
			() -> userRecoveryPasswordUseCase.execute(email, recoveryBaseUrl)
		);
	}
}
