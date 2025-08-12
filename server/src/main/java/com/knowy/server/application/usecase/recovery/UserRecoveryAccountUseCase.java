package com.knowy.server.application.usecase.recovery;

import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.model.MailMessage;
import com.knowy.server.application.util.TokenUserPrivateTool;
import com.knowy.server.domain.Email;

/**
 * Use case responsible for initiating the account recovery process for deactivated accounts.
 * <p>
 * Generates a recovery token valid for 30 days and creates an email message containing a reactivation link that allows
 * the user to restore their account before permanent deletion.
 */
public class UserRecoveryAccountUseCase {

	private final TokenUserPrivateTool tokenUserPrivateTool;

	/**
	 * Constructs a new {@code UserRecoveryAccountUseCase} with the specified token tool.
	 *
	 * @param tokenUserPrivateTool Utility for generating and verifying user tokens.
	 */
	public UserRecoveryAccountUseCase(TokenUserPrivateTool tokenUserPrivateTool) {
		this.tokenUserPrivateTool = tokenUserPrivateTool;
	}

	/**
	 * Executes the account recovery process for a deactivated user.
	 * <p>
	 * Creates a recovery token valid for 30 days for the specified email and generates a {@link MailMessage} containing
	 * a reactivation link.
	 *
	 * @param email           The user's email to send the recovery link to.
	 * @param recoveryBaseUrl The base URL used to construct the recovery link.
	 * @return A {@link MailMessage} containing the recipient, subject, and body of the recovery email.
	 * @throws KnowyTokenException        If there is an error generating the recovery token.
	 * @throws KnowyUserNotFoundException If no user is found with the provided email.
	 */
	public MailMessage execute(Email email, String recoveryBaseUrl)
		throws KnowyTokenException, KnowyUserNotFoundException {

		final long THIRTY_DAYS_IN_MILLIS = 30L * 24 * 60 * 60 * 1000;

		String subject = "Tu enlace para recuperar la cuenta de Knowy está aquí";
		String token = tokenUserPrivateTool.createUserTokenByEmail(email, THIRTY_DAYS_IN_MILLIS);
		String body = reactivationTokenBody(token, recoveryBaseUrl);
		return new MailMessage(email.value(), subject, body);
	}

	private String reactivationTokenBody(String token, String appUrl) {
		String url = "%s?token=%s".formatted(appUrl, token);
		return """
			¡Hola, %%$@€#&%%$%%!
			
			Tu cuenta de KNOWY ha sido desactivada correctamente.
			
			Dispones de 30 días para recuperarla haciendo click en el siguiente enlace:
			
			%s
			
			Una vez transcurrido este tiempo, tu cuenta será eliminada definitivamente.
			
			¡Esperamos verte de vuelta!
			
			© 2025 KNOWY, Inc
			""".formatted(url);
	}
}
