package com.newbie.app.common.dto;

import lombok.Builder;

import org.springframework.data.domain.Page;
import java.util.List;

/**
 * Data Transfer Object for paginated results.
 * Wraps the list of items along with pagination metadata.
 *
 * @param <T> Type of the items in the list
 */
@Builder
public record PaginatedResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
    /**
     * Maps a Spring Data Page object to a PaginatedResponse DTO.
     *
     * @param page The Spring Data Page object
     * @param <T>  The item type
     * @return A PaginatedResponse containing items and metadata
     */
    public static <T> PaginatedResponse<T> fromPage(Page<T> page) {
        return PaginatedResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
