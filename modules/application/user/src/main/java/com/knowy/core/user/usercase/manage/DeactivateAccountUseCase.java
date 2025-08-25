package com.knowy.core.user.usercase.manage;

import com.knowy.core.exception.KnowyMailDispatchException;
import com.knowy.core.port.ExternalNotificationDispatcher.ExternalNotification;
import com.knowy.core.user.exception.KnowyTokenException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.user.exception.KnowyWrongPasswordException;
import com.knowy.core.port.ExternalNotificationDispatcher;
import com.knowy.core.user.port.KnowyPasswordEncoder;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.util.TokenUserPrivateTool;
import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Password;
import com.knowy.core.user.domain.UserPrivate;

/**
 * Use case responsible for deactivating a user's account.
 * <p>
 * This process verifies the provided password, deactivates the account, and sends an email containing a reactivation
 * link that is valid for 30 days. If the user does not reactivate the account within this period, it will be
 * permanently deleted.
 */
public class DeactivateAccountUseCase {

	private final TokenUserPrivateTool tokenUserPrivateTool;
	private final ExternalNotificationDispatcher externalNotificationDispatcher;
	private final KnowyPasswordEncoder knowyPasswordEncoder;
	private final UserPrivateRepository userPrivateRepository;

	/**
	 * Constructs a new {@code DeactivateAccountUseCase} with the required dependencies.
	 *
	 * @param tokenUserPrivateTool  Utility for generating and verifying user tokens.
	 * @param externalNotificationDispatcher  Email client for sending recovery messages.
	 * @param knowyPasswordEncoder  Adapter for verifying user passwords.
	 * @param userPrivateRepository Repository for accessing and persisting private user data.
	 */
	public DeactivateAccountUseCase(
		TokenUserPrivateTool tokenUserPrivateTool,
		ExternalNotificationDispatcher externalNotificationDispatcher,
		KnowyPasswordEncoder knowyPasswordEncoder,
		UserPrivateRepository userPrivateRepository
	) {
		this.tokenUserPrivateTool = tokenUserPrivateTool;
		this.externalNotificationDispatcher = externalNotificationDispatcher;
		this.knowyPasswordEncoder = knowyPasswordEncoder;
		this.userPrivateRepository = userPrivateRepository;
	}

	/**
	 * Executes the account deactivation process.
	 * <p>
	 * Verifies that the provided password and confirmation password match, deactivates the user's account, generates a
	 * recovery token valid for 30 days, and sends an email containing the reactivation link to the user.
	 *
	 * @param command Command containing the email, password, confirmation password, and recovery base URL.
	 * @throws KnowyTokenException         If there is an error generating the recovery token.
	 * @throws KnowyUserNotFoundException  If no user exists with the provided email.
	 * @throws KnowyMailDispatchException  If the recovery email could not be sent.
	 * @throws KnowyWrongPasswordException If the provided password is incorrect or does not match the confirmation
	 *                                     password.
	 */
	public void execute(DeactivateAccountCommand command)
		throws KnowyTokenException, KnowyUserNotFoundException, KnowyMailDispatchException, KnowyWrongPasswordException {

		desactivateUserAccount(command.email(), command.password(), command.confirmPassword());
		ExternalNotification externalNotification = createAccountRecoveryNotification(command.email(), command.recoveryBaseUrl());
		externalNotificationDispatcher.dispatch(externalNotification);
	}

	private void desactivateUserAccount(Email email, Password password, Password confirmPassword)
		throws KnowyUserNotFoundException, KnowyWrongPasswordException {

		if (!password.equals(confirmPassword)) {
			throw new KnowyWrongPasswordException("Passwords do not match");
		}
		UserPrivate userPrivate = userPrivateRepository.findByEmail(email.value())
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found"));
		knowyPasswordEncoder.assertHasPassword(userPrivate, password.value());

		UserPrivate newUserPrivate = new UserPrivate(
			userPrivate.cropToUser(),
			userPrivate.email(),
			userPrivate.password(),
			false);
		userPrivateRepository.save(newUserPrivate);
	}

	private ExternalNotification createAccountRecoveryNotification(Email email, String recoveryBaseUrl)
		throws KnowyTokenException, KnowyUserNotFoundException {

		final long THIRTY_DAYS_IN_MILLIS = 30L * 24 * 60 * 60 * 1000;

		String subject = "Tu enlace para recuperar la cuenta de Knowy está aquí";
		String token = tokenUserPrivateTool.createUserTokenByEmail(email, THIRTY_DAYS_IN_MILLIS);
		String body = reactivationTokenBody(token, recoveryBaseUrl);
		return new ExternalNotification(email.value(), subject, body);
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
