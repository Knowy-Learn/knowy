package com.knowy.server.api.config;

import com.knowy.core.NewsService;
import com.knowy.core.port.NewsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public NewsService newsService(NewsRepository newsRepository) {
		return new NewsService(newsRepository);
	}
}
