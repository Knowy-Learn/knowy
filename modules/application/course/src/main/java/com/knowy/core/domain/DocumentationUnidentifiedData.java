package com.knowy.core.domain;

/**
 * Content Contract for Documentation (Identity-less).
 * <p>
 * Defines the core state of a document, utilized for creation payloads where a database ID has not yet been assigned.
 */
public interface DocumentationUnidentifiedData {
	String title();

	String link();

	/**
	 * Immutable implementation of documentation content.
	 *
	 * @param title The document title.
	 * @param link  The document link.
	 */
	record InmutableDocumentationUnidentifiedData(String title, String link) implements DocumentationUnidentifiedData {

		/**
		 * Transformation constructor.
		 *
		 * @param documentationUnidentifiedData the documentation reference to implement.
		 */
		InmutableDocumentationUnidentifiedData(DocumentationUnidentifiedData documentationUnidentifiedData) {
			this(documentationUnidentifiedData.title(), documentationUnidentifiedData.link());
		}
	}
}
