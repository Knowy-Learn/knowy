package com.knowy.server.api.mapper;

import com.knowy.core.domain.PagedResult;
import com.knowy.server.api.dto.PaginationMetadata;

public class PagedResultMapper {

	public PaginationMetadata toPaginationMetaData(PagedResult pagination) {
		return new PaginationMetadata()
			.total(pagination.totalItems())
			.pages(pagination.pages())
			.size(pagination.page().size())
			.page(pagination.page().number());
	}
}
