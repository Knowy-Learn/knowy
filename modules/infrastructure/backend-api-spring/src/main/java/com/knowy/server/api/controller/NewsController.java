package com.knowy.server.api.controller;

import com.knowy.core.NewsService;
import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;
import com.knowy.server.api.dto.NewsDto;
import com.knowy.server.api.dto.NewsGet200Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@Validated
public class NewsController implements NewsApi {

	private final NewsService newsService;

	public NewsController(NewsService newsService) {
		this.newsService = newsService;
	}

	@Override
	public ResponseEntity<NewsGet200Response> newsGet(String acceptLanguage, Integer page, Integer pageSize) {
		var pagination = new Pagination(page, pageSize);

		Iterable<News> newsIterable = newsService.findLastNews(pagination);

		List<NewsDto> newsDto = StreamSupport.stream(newsIterable.spliterator(), false)
			.map(newItem -> new NewsDto(newItem.id(), newItem.title(), newItem.content(), newItem.date()))
			.toList();
		return ResponseEntity.ok(new NewsGet200Response(
			page,
			pageSize,
			newsDto
		));
	}
}
