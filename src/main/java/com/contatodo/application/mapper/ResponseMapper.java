package com.contatodo.application.mapper;

import java.util.List;

/**
 * Contract for mappers that convert domain entities into response DTOs.
 *
 * @param <T> Domain entity type.
 * @param <R> Response DTO type.
 */
public interface ResponseMapper<T, R> {

    /**
     * Maps a single entity to its response form.
     *
     * @param entity Domain entity.
     * @return Response DTO.
     */
    R toResponse(T entity);

    /**
     * Maps a list of entities to response DTOs using {@link #toResponse}.
     *
     * @param entities Domain entities.
     * @return Response DTOs.
     */
    default List<R> toResponseList(List<T> entities) {
        return entities.stream().map(this::toResponse).toList();
    }
}