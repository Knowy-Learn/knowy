package com.knowy.server.api.usecase.news;

import com.knowy.core.NewsService;
import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;
import com.knowy.core.port.NewsRepository;
import com.knowy.core.user.usercase.KnowyUseCase;
import com.knowy.server.api.dto.NewsDto;

import java.util.List;
import java.util.stream.StreamSupport;

public class GetLatestNewsUseCase implements KnowyUseCase<Pagination, List<NewsDto>> {

	private final NewsService newsService;

	public GetLatestNewsUseCase(NewsRepository newsRepository) {
		this.newsService = new NewsService(newsRepository);
	}

	@Override
	public List<NewsDto> execute(Pagination pagination) {
		Iterable<News> newsIterable = newsService.findLastNews(pagination);

		return StreamSupport.stream(newsIterable.spliterator(), false)
			.map(this::toDto)
			.toList();
	}

	private NewsDto toDto(News news) {
		return new NewsDto(news.id(), news.title(), news.content(), news.date()
		);
	}
}
