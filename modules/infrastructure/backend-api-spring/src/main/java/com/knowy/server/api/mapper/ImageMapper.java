package com.knowy.server.api.mapper;

import com.knowy.server.api.dto.ImageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Mapper utility to convert image URL strings into ImageDto objects.
 */
public class ImageMapper {
	private static final Logger logger = LoggerFactory.getLogger(ImageMapper.class);

	/**
	 * Maps a string URL to an ImageDto.
	 *
	 * @param imageUrl the URL string to be converted.
	 * @return an ImageDto with the parsed URI, or null URL if the input is invalid or empty.
	 */
	public ImageDto toImageDto(String imageUrl) {
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
