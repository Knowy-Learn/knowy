package com.knowy.server.repository.adapters;

import com.knowy.server.entity.BannedWordsEntity;
import com.knowy.server.repository.ports.BannedWordsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaBannedWordsRepository extends BannedWordsRepository, JpaRepository<BannedWordsEntity, Integer> {

	@Override
	List<BannedWordsEntity> findAll();

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
