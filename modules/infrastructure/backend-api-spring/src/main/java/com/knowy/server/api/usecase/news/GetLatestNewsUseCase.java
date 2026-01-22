package com.knowy.server.api.usecase.news;

import com.knowy.core.NewsService;
import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;
import com.knowy.core.port.NewsRepository;
import com.knowy.core.util.KnowyUseCase;
import com.knowy.server.api.dto.NewsDto;

import java.util.List;

public class GetLatestNewsUseCase implements KnowyUseCase<Pagination, List<NewsDto>> {

	private final NewsService newsService;

	public GetLatestNewsUseCase(NewsRepository newsRepository) {
		this.newsService = new NewsService(newsRepository);
	}

	@Override
	public List<NewsDto> execute(Pagination pagination) {
		return newsService.findLastNews(pagination).collection().stream()
			.map(this::toDto)
			.toList();
	}

	private NewsDto toDto(News news) {
		return new NewsDto(news.id(), news.title(), news.content(), news.date()
		);
	}
}
