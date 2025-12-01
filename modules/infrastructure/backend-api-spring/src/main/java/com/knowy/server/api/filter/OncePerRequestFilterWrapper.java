package com.knowy.server.api.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class OncePerRequestFilterWrapper extends OncePerRequestFilter {

	private final Filter delegate;

	public OncePerRequestFilterWrapper(Filter delegate) {
		this.delegate = delegate;
	}

	@Override
	protected void doFilterInternal(
		@Nonnull HttpServletRequest request,
		@Nonnull HttpServletResponse response,
		@Nonnull FilterChain filterChain
	) throws ServletException, IOException {
		delegate.doFilter(request, response, filterChain);
	}
}
