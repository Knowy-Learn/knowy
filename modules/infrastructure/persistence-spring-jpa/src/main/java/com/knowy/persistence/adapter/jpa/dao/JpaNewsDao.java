package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.NewsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaNewsDao extends JpaRepository<NewsEntity, Integer> {
	@Query("SELECT n FROM NewsEntity n ORDER BY n.date DESC")
	Page<NewsEntity> findLastNews(Pageable pageable);
}
