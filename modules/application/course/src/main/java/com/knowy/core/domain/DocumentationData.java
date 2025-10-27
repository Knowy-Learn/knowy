package com.knowy.core.domain;

public interface DocumentationData {
	String title();

	String link();

	record InmutableDocumentationData(String title, String link) implements DocumentationData {
		InmutableDocumentationData(DocumentationData documentationData) {
			this(documentationData.title(), documentationData.link());
		}
	}
}
