package com.knowy.server.application.usecase.recovery;

import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.model.MailMessage;
import com.knowy.server.application.util.TokenUserPrivateTool;
import com.knowy.server.domain.Email;

/**
 * Use case responsible for initiating the password recovery process.
 * <p>
 * Generates a password recovery token for a user identified by their email, constructs a recovery email message
 * containing the token link.
 */
public class UserRecoveryPasswordUseCase {

	private final TokenUserPrivateTool tokenUserPrivateTool;

	/**
	 * Constructs a new {@code RecoveryPasswordUseCase} with the specified token tool.
	 *
	 * @param tokenUserPrivateTool Utility for generating and verifying user tokens.
	 */
	public UserRecoveryPasswordUseCase(TokenUserPrivateTool tokenUserPrivateTool) {
		this.tokenUserPrivateTool = tokenUserPrivateTool;
	}

	/**
	 * Executes the password recovery process for the given email.
	 * <p>
	 * Creates a token for the user associated with the email and generates a recovery email message containing a link
	 * to reset the password.
	 *
	 * @param email           The user's email to send the recovery link to.
	 * @param recoveryBaseUrl The base URL used to construct the recovery link.
	 * @return A {@link MailMessage} containing the recipient, subject, and body of the recovery email.
	 * @throws KnowyUserNotFoundException If no user is found with the provided email.
	 * @throws KnowyTokenException        If there is an error generating the recovery token.
	 */
	public MailMessage execute(Email email, String recoveryBaseUrl)
		throws KnowyUserNotFoundException, KnowyTokenException {

		String subject = "Tu enlace para recuperar la cuenta de Knowy está aquí";
		String token = tokenUserPrivateTool.createUserTokenByEmail(email);
		String body = tokenBody(token, recoveryBaseUrl);
		return new MailMessage(email.value(), subject, body);
	}

	private String tokenBody(String token, String appUrl) {
		String url = "%s?token=%s".formatted(appUrl, token);
		return """
			¡Hola, talentoso %%$@€#&%%$%%! 👋
			
			Sabemos que tu camino como $%%$@%%&€#@&$ es importante, por eso te ayudamos a recuperar tu acceso. \s
			Haz clic en el siguiente enlace para restablecer tu contraseña:
			
			%s
			
			Este enlace es válido solo por un tiempo limitado.
			Si no fuiste tú quien pidió este cambio, no te preocupes, simplemente ignora este correo.
			
			¡Sigue aprendiendo y conquistando tus metas con Knowy! 💪
			
			---
			© 2025 KNOWY, Inc
			""".formatted(url);
	}
}
