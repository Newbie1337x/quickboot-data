package com.newbie.app.common.base.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Generic base mapper interface to be implemented by all MapStruct feature
 * mappers.
 *
 * <p>
 * MapStruct generates a concrete implementation per type pair at compile time.
 * This interface establishes the standard contract, ensuring all mappers expose
 * the same operations consistently across features.
 * </p>
 *
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>{@code
 * @Mapper(componentModel = "spring")
 * public interface ExampleMapper extends BaseMapper<Example, ExampleRequest, ExampleResponse> {
 * }
 * }</pre>
 * </p>
 *
 * @param <E>  Entity type
 * @param <RQ> Request DTO type (input from the client)
 * @param <RS> Response DTO type (output to the client)
 */
public interface BaseMapper<E, RQ, RS> {

    /**
     * Converts a request DTO to an entity.
     * Used by POST (create) and PUT (full update) operations.
     *
     * @param request The incoming request DTO
     * @return The mapped entity
     */
    E toEntity(RQ request);

    /**
     * Converts an entity to a response DTO.
     *
     * @param entity The persisted entity
     * @return The mapped response DTO
     */
    RS toResponse(E entity);

    /**
     * Converts a list of entities to a list of response DTOs.
     *
     * @param entities The list of entities
     * @return The mapped response DTOs
     */
    List<RS> toResponseList(List<E> entities);

    /**
     * Applies non-null fields from {@code request} onto an existing entity
     * in-place.
     * Used by PATCH (partial update) operations.
     *
     * <p>
     * Implementors should annotate this method with
     * {@code @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)}
     * to skip null fields automatically.
     * </p>
     *
     * @param request The partial request DTO
     * @param entity  The target entity to update
     */
    void updateEntityFromRequest(RQ request, @MappingTarget E entity);
}
