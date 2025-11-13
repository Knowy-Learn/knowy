package com.knowy.server.infrastructure.config;

import com.knowy.XmlDataLoader;
import com.knowy.core.port.DataLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoaderConfiguration {

	@Bean
	public DataLoader dataLoader() {
		return new XmlDataLoader();
	}
}
