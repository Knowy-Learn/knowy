package com.knowy.server.api.controller;

import com.knowy.core.domain.Pagination;
import com.knowy.core.port.NewsRepository;
import com.knowy.server.api.dto.NewsDto;
import com.knowy.server.api.dto.NewsGet200Response;
import com.knowy.server.api.usecase.news.GetLatestNewsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class NewsController implements NewsApi {

	private final GetLatestNewsUseCase getLatestNewsUseCase;

	public NewsController(NewsRepository newsRepository) {
		this.getLatestNewsUseCase = new GetLatestNewsUseCase(newsRepository);
	}

	@Override
	public ResponseEntity<NewsGet200Response> newsGet(String acceptLanguage, Integer page, Integer pageSize) {
		Pagination pagination = new Pagination(page, pageSize);
		List<NewsDto> items = getLatestNewsUseCase.execute(pagination);

		var response = new NewsGet200Response(page, pageSize, items);
		return ResponseEntity.ok(response);
	}
}
