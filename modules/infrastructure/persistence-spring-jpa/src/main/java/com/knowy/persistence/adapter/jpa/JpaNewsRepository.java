package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;
import com.knowy.core.port.NewsRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaNewsDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaNewsMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class JpaNewsRepository implements NewsRepository {

	private final JpaNewsDao jpaNewsDao;

	public JpaNewsRepository(JpaNewsDao jpaNewsDao) {
		this.jpaNewsDao = jpaNewsDao;
	}

	@Override
	public Iterable<News> findLastNews(Pagination pagination) {
		JpaNewsMapper jpaNewsMapper = new JpaNewsMapper();

		Pageable pageable = PageRequest.of(pagination.page(), pagination.size());

		return jpaNewsDao.findLastNews(pageable)
			.getContent()
			.stream()
			.map(jpaNewsMapper::toDomain)
			.toList();
	}
}
