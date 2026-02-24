package com.knowy.server.api.mapper;

import com.knowy.core.domain.Documentation;
import com.knowy.server.api.dto.DocumentationDto;

import java.net.URI;
import java.util.Objects;

public class DocumentationMapper {

	public DocumentationDto toDto(Documentation documentation) {
		Objects.requireNonNull(documentation);

		return  new DocumentationDto(
			documentation.id(),
			documentation.title(),
			URI.create(documentation.link())
		);
	}

}
