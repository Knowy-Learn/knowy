package com.knowy.server.api.usecase.auth;

import com.knowy.core.user.exception.KnowyTokenException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.usercase.KnowyUseCase;
import com.knowy.server.api.dto.AuthLoginPost200Response;
import com.knowy.server.api.dto.AuthLoginPostRequest;
import com.knowy.security.model.UserPrivateSecurityDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class LoginUserUseCase implements KnowyUseCase<AuthLoginPostRequest, AuthLoginPost200Response> {

	private final AuthenticationManager authenticationManager;
	private final KnowyTokenTools knowyTokenTools;

	public LoginUserUseCase(AuthenticationManager authenticationManager, KnowyTokenTools knowyTokenTools) {
		this.authenticationManager = authenticationManager;
		this.knowyTokenTools = knowyTokenTools;
	}

	@Override
	public AuthLoginPost200Response execute(AuthLoginPostRequest authLoginPostRequest) throws KnowyTokenException {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
			authLoginPostRequest.getEmail(),
			authLoginPostRequest.getPassword()
		));

		UserPrivateSecurityDetails userDetails = (UserPrivateSecurityDetails) authentication.getPrincipal();
		String token = knowyTokenTools.encode(userDetails.getUser(), userDetails.getPassword());

		return new AuthLoginPost200Response().accessToken(token);
	}
}
