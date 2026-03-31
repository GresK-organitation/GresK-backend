package com.gresk.modules.event.application.dto;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int page,
        int size,
        int totalPages
) {
    public static <T> PageResponse<T> of(List<T> content, long totalElements, PageRequest pageRequest) {
        int totalPages = pageRequest.getPageSize() == 0 ? 0
                : (int) Math.ceil((double) totalElements / pageRequest.getPageSize());
        return new PageResponse<>(
                content,
                totalElements,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                totalPages
        );
    }
}
