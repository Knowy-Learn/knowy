package com.knowy.core.domain;

public interface DocumentationUnidentifiedData {
	String title();

	String link();

	record InmutableDocumentationUnidentifiedData(String title, String link) implements DocumentationUnidentifiedData {
		InmutableDocumentationUnidentifiedData(DocumentationUnidentifiedData documentationUnidentifiedData) {
			this(documentationUnidentifiedData.title(), documentationUnidentifiedData.link());
		}
	}
}
