package com.knowy.server.api.mapper;

import com.knowy.server.api.dto.ImageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

// JAVADOC
public class ImageDtoMapper {
	private static final Logger logger = LoggerFactory.getLogger(ImageDtoMapper.class);

	// JAVADOC
	public ImageDto toDto(String imageUrl) {
		ImageDto dto = new ImageDto().alt("Course thumbnail");

		if (imageUrl == null || imageUrl.isBlank()) {
			logger.info("Course image is missing or empty");
			return dto.url(null);
		}

		try {
			return dto.url(new URI(imageUrl));
		} catch (URISyntaxException e) {
			logger.warn("Invalid URI format: '{}'. Error: {}", imageUrl, e.getMessage());
			return dto.url(null);
		}
	}
}
