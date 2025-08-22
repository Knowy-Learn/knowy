package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.BannedWordsEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBannedWordsDao extends JpaRepository<BannedWordsEntity, Integer> {

	@Query(value = """
		SELECT
		    CASE
		        WHEN EXISTS (
		            SELECT bw.word
		            FROM banned_word bw
		            WHERE :word ILIKE CONCAT('%', bw.word, '%')
		        )
		        THEN true
		        ELSE false
		    END AS is_banned
		""", nativeQuery = true)
	boolean isWordBanned(@Param("word") String word);
}
