package com.knowy.core.port;

public interface BannedWordsRepository {
	boolean isWordBanned(String word);
}
