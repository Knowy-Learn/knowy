package com.knowy.server.application.usecase.recovery;

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
public class UserRecoveryAccountUseCaseTest {

	@Mock
	private TokenUserPrivateTool tokenUserPrivateTool;

	@InjectMocks
	private UserRecoveryAccountUseCase userRecoveryPasswordUseCase;

	@Test
	void given_validEmail_when_createDeletedAccountEmail_thenReturnMailMessage() throws Exception {
		Email email = new Email("email@example.com");

		Mockito.when(tokenUserPrivateTool.createUserTokenByEmail(Mockito.eq(email), Mockito.anyLong()))
			.thenReturn("mocked-token");

		MailMessage mailMessage = userRecoveryPasswordUseCase.execute(email, "http://app.url");

		assertEquals(email.value(), mailMessage.to());
		assertNotNull(mailMessage.subject());
		assertFalse(mailMessage.subject().isEmpty());
		assertTrue(mailMessage.body().contains("mocked-token"));
		assertTrue(mailMessage.body().contains("http://app.url"));
	}

	@Test
	void given_nonExistingEmail_when_createDeletedAccountEmail_then_throwsKnowyUserNotFoundException()
		throws KnowyTokenException, KnowyUserNotFoundException {

		Email email = new Email("notfound@example.com");
		Mockito.doThrow(KnowyUserNotFoundException.class)
			.when(tokenUserPrivateTool)
			.createUserTokenByEmail(Mockito.eq(email), Mockito.anyLong());

		assertThrows(
			KnowyUserNotFoundException.class,
			() -> userRecoveryPasswordUseCase.execute(email, "http://app.url")
		);
	}

	@Test
	void given_validEmail_when_createDeletedAccountEmail_then_throwsKnowyTokenException()
		throws KnowyTokenException, KnowyUserNotFoundException {

		Email email = new Email("email@example.com");
		Mockito.doThrow(KnowyTokenException.class)
			.when(tokenUserPrivateTool)
			.createUserTokenByEmail(Mockito.eq(email), Mockito.anyLong());

		assertThrows(
			KnowyTokenException.class,
			() -> userRecoveryPasswordUseCase.execute(email, "http://app.url")
		);
	}
}
