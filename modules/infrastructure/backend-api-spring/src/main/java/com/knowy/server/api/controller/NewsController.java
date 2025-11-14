package com.knowy.server.api.controller;

import com.knowy.core.NewsService;
import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("news")
public class NewsController {

	private final NewsService newsService;

	public NewsController(NewsService newsService) {
		this.newsService = newsService;
	}

	/**
	 * Retrieves the latest news items.
	 * <p>
	 * Uses a fixed pagination of the first 3 items.
	 *
	 * @return a list containing the latest news items
	 */
	@GetMapping
	public List<News> getNews() {
		Pagination pagination = new Pagination(0, 3);

		Iterable<News> newsIterable = newsService.findLastNews(pagination);

		return StreamSupport.stream(newsIterable.spliterator(), false)
			.toList();
	}
}