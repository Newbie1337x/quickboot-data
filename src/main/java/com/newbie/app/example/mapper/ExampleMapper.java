package com.newbie.app.example.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.newbie.app.common.base.mapper.BaseMapper;
import com.newbie.app.example.dto.ExampleRequest;
import com.newbie.app.example.dto.ExampleResponse;
import com.newbie.app.example.entity.Example;

/**
 * MapStruct mapper for the Example feature.
 *
 * <p>All field names in {@link ExampleRequest} / {@link ExampleResponse} match the entity
 * directly, so no explicit {@code @Mapping} annotations are needed for {@code toEntity}
 * and {@code toResponse}.</p>
 *
 * <p>{@code unmappedTargetPolicy = IGNORE} suppresses warnings for identity and audit
 * fields that are intentionally absent from the request DTO.</p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExampleMapper extends BaseMapper<Example, ExampleRequest, ExampleResponse> {

    /**
     * Applies only non-null fields from the request onto the existing entity.
     * Null fields in the request are skipped, preserving existing values.
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ExampleRequest request, @MappingTarget Example entity);
}
