package com.knowy.server.infrastructure.controller.dto;

import com.knowy.core.domain.Documentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinksLessonDto {
	private String linkTitle;
	private String linkUrl;
	private LinkType type;
	private String fileName;

	public static List<LinksLessonDto> fromDomains(Collection<Documentation> docs) {
		return docs.stream()
			.map(LinksLessonDto::fromDomain)
			.distinct()
			.toList();
	}

	public static LinksLessonDto fromDomain(Documentation doc) {
		boolean isExternal = doc.link().startsWith("http");
		String fileName = (!isExternal && doc.link().contains("/"))
			? doc.link().substring(doc.link().lastIndexOf("/") + 1)
			: null;

		return new LinksLessonDto(
			doc.title(),
			doc.link(),
			isExternal ? LinksLessonDto.LinkType.EXTERNAL : LinksLessonDto.LinkType.DOCUMENT,
			fileName
		);
	}

	public enum LinkType {
		EXTERNAL,
		DOCUMENT
	}
}




