package com.knowy.core.domain;

import java.time.LocalDate;

/**
 * Represents the data of a news item.
 */
public interface NewsData {

	/**
	 * Returns the title of the news item.
	 *
	 * @return the news title
	 */
	String title();

	/**
	 * Returns the content of the news item.
	 *
	 * @return the news content
	 */
	String content();

	/**
	 * Returns the publication date of the news item.
	 *
	 * @return the date of the news
	 */
	LocalDate date();
}
