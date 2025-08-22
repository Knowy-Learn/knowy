package com.knowy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowy.core.user.port.KnowyPasswordEncoder;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.security.adapter.jwt.JwtTools;
import com.knowy.security.adapter.jwt.PasswordEncoderAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoConfiguration
public class KnowyJwtAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public KnowyTokenTools jwtTools(
		@Value("${spring.jwt.key}") String secretKey,
		ObjectMapper objectMapper
	) {
		return new JwtTools(secretKey, objectMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public KnowyPasswordEncoder passwordEncoderAdapter(PasswordEncoder passwordEncoder) {
		return new PasswordEncoderAdapter(passwordEncoder);
	}

}
