package com.knowy.core.user.usercase.manage;

import com.knowy.core.exception.KnowyMailDispatchException;
import com.knowy.core.user.exception.KnowyTokenException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.port.ExternalNotificationDispatcher;
import com.knowy.core.port.ExternalNotificationDispatcher.ExternalNotification;
import com.knowy.core.user.util.TokenUserPrivateTool;
import com.knowy.core.user.domain.Email;

/**
 * Use case responsible for initiating the password recovery process.
 * <p>
 * Generates a password recovery token for a user identified by their email, constructs a recovery email message
 * containing a secure link to reset the password, and sends it via the configured email client.
 */
public class SendRecoveryPasswordUseCase {

	private final TokenUserPrivateTool tokenUserPrivateTool;
	private final ExternalNotificationDispatcher externalNotificationDispatcher;

	/**
	 * Constructs a new {@code SendRecoveryPasswordUseCase} with the specified dependencies.
	 *
	 * @param tokenUserPrivateTool Utility for generating and verifying user tokens.
	 * @param externalNotificationDispatcher Email client used to send recovery messages.
	 */
	public SendRecoveryPasswordUseCase(TokenUserPrivateTool tokenUserPrivateTool, ExternalNotificationDispatcher externalNotificationDispatcher) {
		this.tokenUserPrivateTool = tokenUserPrivateTool;
		this.externalNotificationDispatcher = externalNotificationDispatcher;
	}

	/**
	 * Executes the password recovery process for the given email address.
	 * <p>
	 * Creates a recovery token for the user associated with the specified email, generates an email message containing
	 * the reset password link, and sends the email.
	 *
	 * @param email           The user's email address to send the recovery link to.
	 * @param recoveryBaseUrl The base URL used to build the recovery link.
	 * @throws KnowyUserNotFoundException If no user exists with the provided email.
	 * @throws KnowyTokenException        If an error occurs while generating the recovery token.
	 * @throws KnowyMailDispatchException If the recovery email fails to send.
	 */
	public void execute(Email email, String recoveryBaseUrl)
		throws KnowyUserNotFoundException, KnowyTokenException, KnowyMailDispatchException {

		String subject = "Tu enlace para recuperar la cuenta de Knowy está aquí";
		String token = tokenUserPrivateTool.createUserTokenByEmail(email);
		String body = tokenBody(token, recoveryBaseUrl);

		externalNotificationDispatcher.dispatch(new ExternalNotification(email.value(), subject, body));
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
