package com.knowy.server.application.usecase.manage;

import com.knowy.server.application.exception.KnowyMailDispatchException;
import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.ports.KnowyEmailClientTool;
import com.knowy.server.application.util.TokenUserPrivateTool;
import com.knowy.server.domain.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SendRecoveryPasswordUseCaseTest {

	@Mock
	private KnowyEmailClientTool knowyEmailClientTool;

	@Mock
	private TokenUserPrivateTool tokenUserPrivateTool;

	@InjectMocks
	private SendRecoveryPasswordUseCase sendRecoveryPasswordUseCase;

	@Test
	void given_validEmail_when_createRecoveryPasswordEmail_then_sendRecoveryAccountEmail() throws Exception {
		Email email = new Email("user@mail.com");
		String recoveryBaseUrl = "https://app.url/recover";
		String expectedToken = "mocked-token";

		Mockito.when(tokenUserPrivateTool.createUserTokenByEmail(email))
			.thenReturn(expectedToken);

		assertDoesNotThrow(() -> sendRecoveryPasswordUseCase.execute(email, recoveryBaseUrl));
		Mockito.verify(knowyEmailClientTool).sendEmail(
			Mockito.eq(email.value()),
			Mockito.argThat(subject -> subject != null && !subject.isEmpty()),
			Mockito.argThat(body -> body.contains(expectedToken) && body.contains(recoveryBaseUrl))
		);
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
			() -> sendRecoveryPasswordUseCase.execute(email, recoveryBaseUrl)
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
			() -> sendRecoveryPasswordUseCase.execute(email, recoveryBaseUrl)
		);
	}

	@Test
	void given_x_when_createRecoveryPasswordEmail_then_throw() throws Exception {
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
			() -> sendRecoveryPasswordUseCase.execute(email, recoveryBaseUrl)
		);
	}
}
