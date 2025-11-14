package com.knowy.core;

import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;
import com.knowy.core.port.NewsRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class NewsServiceTest {

	@Mock
	private NewsRepository newsRepository;

	@InjectMocks
	private NewsService newsService;

	@Nested
	class FindLastNewsUseCaseTest {

		@Test
		void given_newsExists_when_findingLastNewsByPagination_then_returnsNews() {
			Pagination mockPagination = new Pagination(0, 3);
			News news1 = new News(1, "Breaking News", "Something happened today.", LocalDate.of(2025, 11, 13));
			News news2 = new News(2, "Tech Update", "New framework released.", LocalDate.of(2025, 11, 12));
			News news3 = new News(3, "Sports Result", "Team A won against Team B.", LocalDate.of(2025, 11, 11));

			Mockito.when(newsRepository.findLastNews(mockPagination))
				.thenReturn(List.of(news1, news2, news3));

			assertDoesNotThrow(
				() -> newsService.findLastNews(mockPagination)
			);
		}
	}
}
