package com.knowy.server.application;

import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserEmailException;
import com.knowy.server.application.exception.validation.user.KnowyPasswordFormatException;
import com.knowy.server.application.exception.validation.user.KnowyUnchangedEmailException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.model.MailMessage;
import com.knowy.server.application.model.PasswordResetInfo;
import com.knowy.server.application.ports.KnowyPasswordEncoder;
import com.knowy.server.application.ports.KnowyTokenTools;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.application.util.TokenUserPrivateTool;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.Password;
import com.knowy.server.domain.UserPrivate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class UserPrivateService {

    private final UserPrivateRepository privateUserRepository;
	private final TokenUserPrivateTool tokenUserPrivateTool;
    private final KnowyPasswordEncoder passwordEncoder;
    private final KnowyTokenTools tokenTools;

    /**
     * The constructor
     *
     * @param privateUserRepository the privateUserRepository
     * @param passwordEncoder       the passwordEncoder
     * @param tokenTools            the tokenTools
     */
    public UserPrivateService(
		UserPrivateRepository privateUserRepository,
		KnowyPasswordEncoder passwordEncoder,
		KnowyTokenTools tokenTools, TokenUserPrivateTool tokenUserPrivateTool
	) {
        this.privateUserRepository = privateUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenTools = tokenTools;
		this.tokenUserPrivateTool = tokenUserPrivateTool;
	}

    /**
     * Persists the given private user entity in the database.
     *
     * <p>If the user already exists, it will be updated; otherwise, a new record will be created.</p>
     *
     * @param user the {@code PrivateUserEntity} to persist
     * @return the saved entity, potentially with an updated ID or other persisted fields
     */
    public UserPrivate save(UserPrivate user) {
        return privateUserRepository.save(user);
    }

    /**
     * Retrieves a private user entity by its ID if it exists.
     *
     * @param id the unique identifier of the private user
     * @return an {@code Optional} containing the user if found, or empty if not
     */
    public Optional<UserPrivate> findPrivateUserById(Integer id) {
        return privateUserRepository.findById(id);
    }

    /**
     * Retrieves a private user entity by its ID or throws an exception if not found.
     *
     * <p>This is a convenience method that wraps {@link #findPrivateUserById(Integer)}
     * and ensures the result is non-null.</p>
     *
     * @param id the unique identifier of the private user
     * @return the {@code PrivateUserEntity} if found
     * @throws KnowyUserNotFoundException if no user exists with the given ID
     */
    public UserPrivate getPrivateUserById(Integer id) throws KnowyUserNotFoundException {
        return findPrivateUserById(id)
                .orElseThrow(() -> new KnowyUserNotFoundException("User not found with id: " + id));
    }

    /**
     * Retrieves a private user entity by email if it exists.
     *
     * @param email the email address of the private user
     * @return an {@code Optional} containing the user if found, or empty if not
     */
    public Optional<UserPrivate> findPrivateUserByEmail(String email) {
        return privateUserRepository.findByEmail(email);
    }

    /**
     * Retrieves a private user entity by email or throws an exception if not found.
     *
     * <p>This method guarantees a non-null result and is typically used in flows
     * where the presence of a user is required.</p>
     *
     * @param email the email address of the private user
     * @return the {@code PrivateUserEntity} associated with the given email
     * @throws KnowyUserNotFoundException if no user exists with the specified email
     */
    public UserPrivate getUserPrivateByEmail(String email) throws KnowyUserNotFoundException {
        return findPrivateUserByEmail(email)
                .orElseThrow(() -> new KnowyUserNotFoundException("User not found"));
    }

    /**
     * Creates a recovery password email for the given user email.
     *
     * <p>Generates a JWT token associated with the user, builds the email content using the provided base URL,
     * and returns a {@code MailMessage} object containing the recipient, subject, and body.</p>
     *
     * @param email           the email address of the user requesting password recovery
     * @param recoveryBaseUrl the base URL to be used for building the recovery link
     * @return a {@code MailMessage} ready to be sent
     * @throws KnowyUserNotFoundException if no user exists with the given email
     * @throws KnowyTokenException        if token generation fails
     */
    public MailMessage createRecoveryPasswordEmail(String email, String recoveryBaseUrl)
            throws KnowyUserNotFoundException, KnowyTokenException {

        String subject = "Tu enlace para recuperar la cuenta de Knowy está aquí";
        String token = createUserTokenByEmail(email);
        String body = tokenBody(token, recoveryBaseUrl);
        return new MailMessage(email, subject, body);
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

    /**
     * Creates an email message with a recovery link for a deleted user account. The recovery link contains a token that
     * expires after 30 days.
     *
     * @param email           the email address of the user whose account was deleted
     * @param recoveryBaseUrl the base URL used to generate the account recovery link
     * @return a {@link MailMessage} containing the recovery email details (receiver, subject, and body)
     * @throws KnowyTokenException        if an error occurs while encoding the JWT token
     * @throws KnowyUserNotFoundException if no user is found for the given email
     */
    public MailMessage createDeletedAccountEmail(String email, String recoveryBaseUrl)
            throws KnowyTokenException, KnowyUserNotFoundException {

        final long THIRTY_DAYS_IN_MILLIS = 30L * 24 * 60 * 60 * 1000;

        String subject = "Tu enlace para recuperar la cuenta de Knowy está aquí";
        String token = createUserTokenByEmail(email, THIRTY_DAYS_IN_MILLIS);
        String body = reactivationTokenBody(token, recoveryBaseUrl);
        return new MailMessage(email, subject, body);
    }

    private String createUserTokenByEmail(String email, long tokenExpirationTime)
            throws KnowyUserNotFoundException, KnowyTokenException {
        UserPrivate userPrivate = findPrivateUserByEmail(email)
                .orElseThrow(() -> new KnowyUserNotFoundException(String.format("The user with email %s was not found", email)));

        PasswordResetInfo passwordResetInfo = new PasswordResetInfo(userPrivate.id(), userPrivate.email().value());
        return tokenTools.encode(passwordResetInfo, userPrivate.password().value(), tokenExpirationTime);
    }

    private String createUserTokenByEmail(String email) throws KnowyUserNotFoundException, KnowyTokenException {
        return createUserTokenByEmail(email, 600_000);
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

    /**
     * Deactivates a user account by verifying the provided password and confirmation password.
     *
     * @param email           the email address of the user whose account will be deactivated
     * @param password        the password entered by the user for verification
     * @param confirmPassword the confirmation password to ensure correctness
     * @throws KnowyWrongPasswordException if the passwords do not match or the password is incorrect
     * @throws KnowyUserNotFoundException  if no user is found for the given email
     */
    public void desactivateUserAccount(
            String email,
            String password,
            String confirmPassword
    ) throws KnowyWrongPasswordException, KnowyUserNotFoundException {
        if (!password.equals(confirmPassword)) {
            throw new KnowyWrongPasswordException("Passwords do not match");
        }

        UserPrivate userPrivate = findPrivateUserByEmail(email)
                .orElseThrow(() -> new KnowyUserNotFoundException("User not found"));

        passwordEncoder.assertHasPassword(userPrivate, password);

        UserPrivate newUserPrivate = new UserPrivate(
                userPrivate.cropToUser(),
                userPrivate.email(),
                userPrivate.password(),
                false);
        privateUserRepository.save(newUserPrivate);
    }

    /**
     * Reactivates a user account based on a valid token.
     *
     * @param token the JWT token used to verify the reactivation request
     * @throws KnowyTokenException        if the token is invalid or cannot be processed
     * @throws KnowyUserNotFoundException if no user is associated with the token
     */
    public void reactivateUserAccount(String token) throws KnowyTokenException, KnowyUserNotFoundException {
        UserPrivate userPrivate = tokenUserPrivateTool.verifyPasswordToken(token);

        if (!userPrivate.active()) {
            UserPrivate newUserPrivate = new UserPrivate(
                    userPrivate.cropToUser(),
                    userPrivate.email(),
                    userPrivate.password(),
                    true
            );
            privateUserRepository.save(newUserPrivate);
        }
    }
}
