package com.knowy.core.usecase;

import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;
import com.knowy.core.port.NewsRepository;

import java.util.Collection;
import java.util.List;

/**
 * Use case class responsible for retrieving the latest news from the repository.
 */
public class FindLastNewsUseCase {

	private final NewsRepository newsRepository;


	/**
	 * Constructs a FindLastNewsUseCase.
	 *
	 * @param newsRepository the repository used to access news data
	 */
	public FindLastNewsUseCase(NewsRepository newsRepository) {
		this.newsRepository = newsRepository;
	}

	/**
	 * Executes the use case to retrieve the latest news items based on the given pagination.
	 *
	 * @param pagination the pagination information specifying which subset of news to retrieve
	 * @return a list containing the latest news items
	 */
	public List<News> execute(Pagination pagination) {
		Iterable<News> iterable = newsRepository.findLastNews(pagination);
		return List.copyOf((Collection<News>) iterable);
	}
}
