package com.knowy.server.api.config;

import com.knowy.core.exception.KnowyRuntimeException;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.security.filter.JwtAuthenticationFilter;
import com.knowy.security.model.UserPrivateSecurityDetails;
import com.knowy.server.api.filter.OncePerRequestFilterWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(UserPrivateRepository userPrivateRepository) {
		return username -> {
			try {
				return userPrivateRepository.findByEmail(username)
					.map(UserPrivateSecurityDetails::new)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));
			} catch (KnowyDataAccessException e) {
				throw new KnowyRuntimeException(e);
			}
		};
	}

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(
		KnowyTokenTools knowyTokenTools,
		UserPrivateRepository userPrivateRepository
	) {
		return new JwtAuthenticationFilter(knowyTokenTools, userPrivateRepository);
	}

	@Bean
	public SecurityFilterChain filterChain(
		HttpSecurity httpSecurity,
		AuthenticationProvider authenticationProvider,
		JwtAuthenticationFilter jwtAuthenticationFilter
	) throws Exception {

		httpSecurity
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/**").permitAll()
				.anyRequest().authenticated()
			)
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(new OncePerRequestFilterWrapper(jwtAuthenticationFilter), UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}
}
