package com.knowy.server.api.usecase.news;

import com.knowy.core.NewsService;
import com.knowy.core.domain.News;
import com.knowy.core.domain.PagedResult;
import com.knowy.core.domain.Pagination;
import com.knowy.core.port.NewsRepository;
import com.knowy.core.util.KnowyUseCase;
import com.knowy.server.api.controller.exception.KnowyBadRequestRuntimeException;
import com.knowy.server.api.dto.NewsDto;
import com.knowy.server.api.dto.NewsGet200Response;
import com.knowy.server.api.dto.PaginationMetadata;

public class GetLatestNewsUseCase implements KnowyUseCase<Pagination, NewsGet200Response> {

	private final NewsService newsService;

	public GetLatestNewsUseCase(NewsRepository newsRepository) {
		this.newsService = new NewsService(newsRepository);
	}

	@Override
	public NewsGet200Response execute(Pagination pagination) {
		PagedResult<News> pagedResult = newsService.findLastNews(pagination);

		if(pagedResult.collection().isEmpty()) {
			throw new KnowyBadRequestRuntimeException("No news found");
		}

		return new NewsGet200Response(
			toMetadata(pagedResult),
			pagedResult.collection().stream()
				.map(this::toDto)
				.toList()
		);
	}

	private NewsDto toDto(News news) {
		return new NewsDto(news.id(), news.title(), news.content(), news.date());
	}

	private PaginationMetadata toMetadata(PagedResult<News> pagedResult) {
		return new PaginationMetadata()
			.page(pagedResult.page().number())
			.size(pagedResult.page().size())
			.pages(pagedResult.pages())
			.total(pagedResult.totalItems());
	}
}
