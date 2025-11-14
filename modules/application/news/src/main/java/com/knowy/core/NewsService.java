package com.knowy.core;

import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;
import com.knowy.core.port.NewsRepository;
import com.knowy.core.usecase.FindLastNewsUseCase;

import java.util.List;

/**
 * Service class responsible for news-related operations.
 */
public class NewsService {

	private final FindLastNewsUseCase findLastNewsUseCase;

	/**
	 * Constructs a NewsService.
	 *
	 * @param newsRepository the repository used to access news data
	 */
	public NewsService(NewsRepository newsRepository) {
		this.findLastNewsUseCase = new FindLastNewsUseCase(newsRepository);
	}

	/**
	 * Retrieves the most recent news items according to the given pagination.
	 *
	 * @param pagination the pagination information specifying which subset of news to retrieve
	 * @return a list of the latest news items
	 */
	public List<News> findLastNews(Pagination pagination) {
		return findLastNewsUseCase.execute(pagination);
	}
}
