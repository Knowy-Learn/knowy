package com.knowy.core.domain;

/**
 * Root domain record for Documentation content.
 * <p>
 * This record bridges the gap between a persistent entity and its raw content by implementing both identity and data
 * contracts.
 *
 * @param id    The unique identifier for this documentation.
 * @param title The descriptive title of the document.
 * @param link  The URL or resource path to the documentation material.
 */
public record Documentation(
	int id,
	String title,
	String link
) implements DocumentationIdentifier, DocumentationUnidentifiedData {

}
