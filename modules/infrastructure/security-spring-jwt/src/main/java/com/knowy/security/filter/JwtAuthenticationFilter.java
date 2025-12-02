package com.knowy.security.filter;

import com.knowy.core.user.domain.User;
import com.knowy.core.user.exception.security.KnowyTokenException;
import com.knowy.core.user.exception.resource.KnowyUserNotFoundException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.port.UserPrivateRepository;

import com.knowy.security.service.TokenValidationService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter implements Filter {

    private final TokenValidationService tokenValidationService;

    public JwtAuthenticationFilter(KnowyTokenTools knowyTokenTools, UserPrivateRepository userPrivateRepository) {
        this.tokenValidationService = new TokenValidationService(knowyTokenTools, userPrivateRepository);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String authHeader = request.getHeader("Authorization");

        if (!isBearerToken(authHeader)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            authenticateUser(jwt);
        } catch (KnowyTokenException | KnowyUserNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isBearerToken(String header) {
        return header != null && header.startsWith("Bearer ");
    }

	private void authenticateUser(String jwt) throws KnowyTokenException, KnowyUserNotFoundException {
		User user = tokenValidationService.validateUserToken(jwt);

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
			user, null, Collections.emptyList()
		);

		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
