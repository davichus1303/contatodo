package com.contatodo.adapters.outbound.persistence.mapper;

import java.util.List;

/**
 * Contract for mappers that convert domain entities to persistence documents.
 *
 * @param <D> Persistence document type.
 * @param <E> Domain entity type.
 */
public interface PersistenceMapper<D, E> {

    /**
     * Maps a document to its domain entity form.
     *
     * @param document Persistence document.
     * @return Domain entity.
     */
    E toEntity(D document);

    /**
     * Maps a domain entity to its persistence document form.
     *
     * @param entity Domain entity.
     * @return Persistence document.
     */
    D toDocument(E entity);

    /**
     * Maps a list of documents to entities using {@link #toEntity}.
     *
     * @param documents Persistence documents.
     * @return Domain entities.
     */
    default List<E> toEntityList(List<D> documents) {
        return documents.stream().map(this::toEntity).toList();
    }
}