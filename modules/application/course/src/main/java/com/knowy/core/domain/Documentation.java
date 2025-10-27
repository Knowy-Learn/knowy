package com.knowy.core.domain;

public record Documentation(
	int id,
	String title,
	String link
) implements DocumentationIdentifier, DocumentationData {

}
