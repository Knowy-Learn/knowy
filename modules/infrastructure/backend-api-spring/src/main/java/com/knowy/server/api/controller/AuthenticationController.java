package com.knowy.server.api.controller;

import com.knowy.core.user.exception.conflict.KnowyEmailAlreadyTakenException;
import com.knowy.core.user.exception.conflict.KnowyNicknameAlreadyTakenException;
import com.knowy.core.user.exception.resource.KnowyImageNotFoundException;
import com.knowy.core.user.exception.resource.KnowyUserNotFoundException;
import com.knowy.core.user.exception.security.KnowyTokenException;
import com.knowy.core.user.exception.validation.KnowyInvalidUserException;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;
import com.knowy.core.user.exception.validation.KnowyPasswordFormatException;
import com.knowy.core.user.exception.validation.KnowyUserEmailFormatException;
import com.knowy.core.user.port.*;
import com.knowy.security.usecase.ValidateUserUseCase;
import com.knowy.server.api.controller.exception.KnowyBadRequestRuntimeException;
import com.knowy.server.api.controller.exception.KnowyConflictRuntimeException;
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorException;
import com.knowy.server.api.controller.exception.KnowyUnauthorizedException;
import com.knowy.server.api.dto.*;
import com.knowy.server.api.usecase.auth.LoginUserUseCase;
import com.knowy.server.api.usecase.auth.RegisterUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for user authentication operations, including login, registration, and token validation.
 */
@RestController
public class AuthenticationController implements AuthApi {

	private final LoginUserUseCase loginUserUseCase;
	private final RegisterUserUseCase registerUserUseCase;
	private final ValidateUserUseCase validateUserUseCase;

	/**
	 * Constructor that initializes use cases for login, registration, and user validation.
	 *
	 * @param authenticationManager  the Spring authentication manager
	 * @param knowyTokenTools        tools for token generation and validation
	 * @param userPrivateRepository  repository for private user data
	 * @param userRepository         repository for public user data
	 * @param profileImageRepository repository for user profile images
	 * @param knowyPasswordEncoder   password encoder utility
	 */
	public AuthenticationController(
		AuthenticationManager authenticationManager,
		KnowyTokenTools knowyTokenTools,
		UserPrivateRepository userPrivateRepository,
		UserRepository userRepository,
		ProfileImageRepository profileImageRepository,
		KnowyPasswordEncoder knowyPasswordEncoder
	) {
		this.loginUserUseCase = new LoginUserUseCase(
			authenticationManager,
			knowyTokenTools
		);

		this.validateUserUseCase = new ValidateUserUseCase(
			knowyTokenTools,
			userPrivateRepository
		);

		this.registerUserUseCase = new RegisterUserUseCase(
			userRepository,
			userPrivateRepository,
			profileImageRepository,
			knowyPasswordEncoder,
			knowyTokenTools
		);
	}

	/**
	 * Handles user login requests.
	 *
	 * @param authLoginPostRequest the login request containing user credentials
	 * @return 200 OK with login response if successful
	 * @throws KnowyUnauthorizedException        if the credentials are invalid
	 * @throws KnowyInternalServerErrorException if a token processing error occurs
	 */
	@Override
	public ResponseEntity<AuthLoginPost200Response> authLoginPost(AuthLoginPostRequest authLoginPostRequest) {
		try {
			return ResponseEntity.ok(loginUserUseCase.execute(authLoginPostRequest));
		} catch (BadCredentialsException ex) {
			throw new KnowyUnauthorizedException("Invalid credentials.", ex);
		} catch (KnowyTokenException ex) {
			throw new KnowyInternalServerErrorException("An unexpected error occurred while processing the token.", ex);
		}
	}

	/**
	 * Handles user registration requests.
	 *
	 * @param authRegisterPostRequest the registration request with user details
	 * @return 201 Created with registration response if successful
	 * @throws KnowyBadRequestRuntimeException   if the user data is invalid
	 * @throws KnowyConflictRuntimeException     if the email or nickname is already taken
	 * @throws KnowyInternalServerErrorException if an unexpected error occurs (token or image processing)
	 */
	@Override
	public ResponseEntity<AuthRegisterPost201Response> authRegisterPost(AuthRegisterPostRequest authRegisterPostRequest) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED)
				.body(registerUserUseCase.execute(authRegisterPostRequest));

		} catch (KnowyUserEmailFormatException e) {
			throw new KnowyBadRequestRuntimeException("The email format is invalid.", e);

		} catch (KnowyPasswordFormatException e) {
			throw new KnowyBadRequestRuntimeException("The password does not meet the required format.", e);

		} catch (KnowyInvalidUserException e) {
			throw new KnowyBadRequestRuntimeException("The user data provided is incomplete or invalid.", e);

		} catch (KnowyInvalidUserGenderException e) {
			throw new KnowyInternalServerErrorException("An error occurred while saving user gender", e);

		}catch (KnowyEmailAlreadyTakenException e) {
			throw new KnowyConflictRuntimeException("The email is already associated with an existing account.", e);

		} catch (KnowyNicknameAlreadyTakenException e) {
			throw new KnowyConflictRuntimeException("The nickname is already taken. Please choose another.", e);

		} catch (KnowyTokenException e) {
			throw new KnowyInternalServerErrorException("An error occurred while generating the authentication token.", e);

		} catch (KnowyImageNotFoundException e) {
			throw new KnowyInternalServerErrorException("An unexpected error occurred while processing the profile image.", e);
		}
	}

	/**
	 * Validates a user's access token.
	 *
	 * @param authValidatePostRequest the request containing the access token
	 * @return 200 OK if the token is valid, 401 Unauthorized if the token is invalid or the user does not exist
	 */
	@Override
	public ResponseEntity<Void> authValidatePost(AuthValidatePostRequest authValidatePostRequest) {
		try {
			validateUserUseCase.execute(authValidatePostRequest.getAccessToken());
			return ResponseEntity.ok().build();
		} catch (KnowyTokenException | KnowyUserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}