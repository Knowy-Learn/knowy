package com.knowy.server.api.usecase.auth;

import com.knowy.core.exception.KnowyException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.usercase.KnowyUseCase;

public class RefreshUserTokenUseCase implements KnowyUseCase<String, String> {

	private final KnowyTokenTools knowyTokenTools;

	public RefreshUserTokenUseCase(KnowyTokenTools knowyTokenTools) {
		this.knowyTokenTools = knowyTokenTools;
	}

	@Override
	public String execute(String token) {
		return "";
	}
}
