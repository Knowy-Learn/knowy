package com.knowy.server.api.usecase.user;

import com.knowy.server.api.dto.PaginationData;

import java.util.List;

public record FindNotSubscribedCoursesCommand(PaginationData paging, List<String> categories) {
}
