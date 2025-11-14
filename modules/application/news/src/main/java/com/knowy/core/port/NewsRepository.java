package com.knowy.core.port;

import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;

/**
 * Repository interface for accessing news data.
 */
public interface NewsRepository {

	/**
	 * Retrieves the latest news items based on the given pagination.
	 *
	 * @param pagination the pagination information specifying which subset of news to retrieve
	 * @return an iterable containing the latest news items
	 */
	Iterable<News> findLastNews(Pagination pagination);
}
