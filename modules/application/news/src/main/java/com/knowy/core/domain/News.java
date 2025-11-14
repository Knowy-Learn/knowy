package com.knowy.core.domain;

import java.time.LocalDate;

/**
 * Represents a news item with its identifier, title, content, and publication date.
 * <p>
 * Implements {@link NewsIdentifier} and {@link NewsData}.
 *
 * @param id      the unique identifier of the news item
 * @param title   the title of the news item
 * @param content the content of the news item
 * @param date    the publication date of the news item
 */
public record News(int id, String title, String content, LocalDate date) implements NewsIdentifier, NewsData {
}