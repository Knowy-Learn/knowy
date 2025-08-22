package com.knowy.server.infrastructure.config;

import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.server.infrastructure.security.UserSecurityDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final UserPrivateRepository userPrivateRepository;
	private static final String LOGIN_URL = "/login";

	public SecurityConfiguration(UserPrivateRepository userPrivateRepository) {
		this.userPrivateRepository = userPrivateRepository;
	}

	/**
	 * Provides a {@link UserDetailsService} bean for Spring Security authentication.
	 *
	 * <p>
	 * This implementation loads user details using an email address, which serves as the username. It queries the
	 * database through {@link UserPrivateRepository} to find a matching user, and wraps the result in a
	 * {@link UserSecurityDetails} object required by Spring Security.
	 * </p>
	 *
	 * @return a lambda-based {@link UserDetailsService} that resolves users by email
	 * @throws UsernameNotFoundException if no user is found with the given email
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		return email -> {
			UserPrivate userPrivate = userPrivateRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
			return new UserSecurityDetails(userPrivate);
		};
	}

	/**
	 * Defines a {@link PasswordEncoder} bean that uses BCrypt hashing algorithm.
	 * <p>
	 * This encoder is used by Spring Security to hash passwords securely before storing them and to verify password
	 * matches during authentication.
	 * </p>
	 *
	 * @return a {@link BCryptPasswordEncoder} instance for password encoding
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Configures the HTTP security settings for the application.
	 * <p>
	 * This includes authorization rules, form login, logout, and session management.
	 * </p>
	 *
	 * @param http the {@link HttpSecurity} object to configure
	 * @return the configured {@link SecurityFilterChain}
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(request -> request
			.requestMatchers("/fonts/**", "/scripts/**", "/styles/**", "/images/**", "/error/**", "/favicon.ico").permitAll()
			.requestMatchers("/", LOGIN_URL, "/register", "/password-change/email", "/password-change",
				"/actuator/health", "/actuator/info", "/reactivate-account").permitAll()
			.anyRequest().authenticated()
		);

		http.formLogin(form -> form
			.loginPage(LOGIN_URL)
			.usernameParameter("email")
			.passwordParameter("password")
			.loginProcessingUrl(LOGIN_URL)
			.failureUrl(LOGIN_URL + "?error")
			.defaultSuccessUrl("/home")
			.permitAll());

		http.logout(logout -> logout
			.logoutUrl("/logout")
			.logoutSuccessUrl("/")
			.deleteCookies("JSESSIONID")
			.permitAll());

		http.sessionManagement(session -> session
			.maximumSessions(1)
		);
		return http.build();
	}

}
