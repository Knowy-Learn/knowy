package com.knowy.server.api.controller;

import com.knowy.core.user.exception.KnowyTokenException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.server.api.dto.*;
import com.knowy.server.api.exception.KnowyInternalServerErrorException;
import com.knowy.server.api.usecase.auth.LoginUserUseCase;
import com.knowy.server.api.usecase.auth.ValidateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController implements AuthApi {

	private final LoginUserUseCase loginUserUseCase;
	private final ValidateUserUseCase validateUserUseCase;

	public AuthenticationController(
		AuthenticationManager authenticationManager,
		KnowyTokenTools knowyTokenTools,
		UserPrivateRepository userPrivateRepository
	) {
		this.loginUserUseCase = new LoginUserUseCase(authenticationManager, knowyTokenTools);
		this.validateUserUseCase = new ValidateUserUseCase(knowyTokenTools, userPrivateRepository);
	}

	@Override
	public ResponseEntity<AuthLoginPost200Response> authLoginPost(AuthLoginPostRequest authLoginPostRequest) {
		try {
			return ResponseEntity.ok(loginUserUseCase.execute(authLoginPostRequest));
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (KnowyTokenException e) {
			throw new KnowyInternalServerErrorException("An unexpected error occurred while processing the token.", e);
		}
	}

	@Override
	public ResponseEntity<AuthRefreshTokenPost200Response> authRefreshTokenPost(AuthRefreshTokenPostRequest authRefreshTokenPostRequest) {
		return null;
	}

	@Override
	public ResponseEntity<AuthRegisterPost201Response> authRegisterPost(AuthRegisterPostRequest authRegisterPostRequest) {
		return null;
	}

	@Override
	public ResponseEntity<Void> authValidatePost(AuthValidatePostRequest authValidatePostRequest) {
		try {
			validateUserUseCase.execute(authValidatePostRequest);
			return ResponseEntity.ok().build();
		} catch (KnowyTokenException | KnowyUserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}