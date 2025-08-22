package com.knowy.persistence.adapter.jpa;

import com.knowy.core.port.BannedWordsRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaBannedWordsDao;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnMissingBean(BannedWordsRepository.class)
public class JpaBannedWordsRepository implements BannedWordsRepository {

	private final JpaBannedWordsDao jpaBannedWordsDao;

	public JpaBannedWordsRepository(JpaBannedWordsDao jpaBannedWordsDao) {
		this.jpaBannedWordsDao = jpaBannedWordsDao;
	}

	@Override
	public boolean isWordBanned(String word) {
		return jpaBannedWordsDao.isWordBanned(word);
	}
}
