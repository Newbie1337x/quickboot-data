package com.newbie.app.common.base.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import com.newbie.app.common.base.entity.Identifiable;
import com.newbie.app.common.base.mapper.BaseMapper;
import com.newbie.app.common.base.service.BaseService;
import com.newbie.app.common.dto.PaginatedResponse;
import com.newbie.app.common.dto.Response;
import com.newbie.app.common.exception.ResourceNotFoundException;

import java.io.Serializable;

/**
 * Base Controller providing generic CRUD endpoints with OpenAPI documentation.
 *
 * <p>Separates API concerns (request/response DTOs) from persistence concerns (entities)
 * via a {@link BaseMapper}. The generic parameters are:</p>
 * <ul>
 *   <li>{@code T}  — Entity (persisted model)</li>
 *   <li>{@code RQ} — Request DTO (what the client sends; validated with {@code @Valid})</li>
 *   <li>{@code RS} — Response DTO (what the client receives)</li>
 *   <li>{@code ID} — Identifier type</li>
 * </ul>
 *
 * @param <T>  Entity type (must implement {@link Identifiable})
 * @param <RQ> Request DTO type
 * @param <RS> Response DTO type
 * @param <ID> Identifier type
 */
public abstract class BaseController<T extends Identifiable<ID>, RQ, RS, ID extends Serializable> {

    protected final BaseService<T, ID> service;
    protected final BaseMapper<T, RQ, RS> mapper;

    protected BaseController(BaseService<T, ID> service, BaseMapper<T, RQ, RS> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Retrieves a paginated list of all records.
     */
    @Operation(summary = "Get all records", description = "Retrieves a paginated list of all available records.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-05-07T19:00:00\",\"status\":400,\"message\":\"Invalid pagination parameters\",\"type\":\"about:blank\",\"title\":\"Bad Request\"}")))
    })
    @GetMapping
    public ResponseEntity<Response<PaginatedResponse<RS>>> getAll(@ParameterObject @NonNull Pageable pageable) {
        Page<RS> data = service.findAll(pageable).map(mapper::toResponse);
        return ResponseEntity.ok(Response.success(PaginatedResponse.fromPage(data), "Records retrieved successfully"));
    }

    /**
     * Retrieves a single record by its unique identifier.
     */
    @Operation(summary = "Get record by ID", description = "Retrieves a single record by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record found"),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-05-07T19:00:00\",\"status\":404,\"message\":\"Record not found with ID: 1\",\"type\":\"about:blank\",\"title\":\"Not Found\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response<RS>> getById(
            @Parameter(description = "ID of the record to retrieve") @PathVariable @NonNull ID id) {
        T entity = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));
        return ResponseEntity.ok(Response.success(mapper.toResponse(entity), "Found"));
    }

    /**
     * Creates a new record. The request body is fully validated before processing.
     */
    @Operation(summary = "Create a new record", description = "Creates a new record with the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-05-07T19:00:00\",\"status\":400,\"message\":\"Validation Failed\",\"type\":\"about:blank\",\"title\":\"Bad Request\",\"errors\":{\"name\":\"must not be blank\"}}")))
    })
    @PostMapping
    public ResponseEntity<Response<RS>> save(@Valid @RequestBody @NonNull RQ request) {
        T saved = service.save(mapper.toEntity(request));
        return ResponseEntity.ok(Response.success(mapper.toResponse(saved), "Created successfully"));
    }

    /**
     * Deletes a record by its identifier.
     */
    @Operation(summary = "Delete record", description = "Permanently removes a record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-05-07T19:00:00\",\"status\":404,\"message\":\"Record not found with ID: 1\",\"type\":\"about:blank\",\"title\":\"Not Found\"}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> delete(
            @Parameter(description = "ID of the record to delete") @PathVariable @NonNull ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(Response.success(null, "Deleted successfully"));
    }

    /**
     * Fully replaces an existing record. The request body is fully validated before processing.
     */
    @Operation(summary = "Update record (Full)", description = "Performs a full replacement of an existing record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-05-07T19:00:00\",\"status\":404,\"message\":\"Record not found with ID: 1\",\"type\":\"about:blank\",\"title\":\"Not Found\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-05-07T19:00:00\",\"status\":400,\"message\":\"Validation Failed\",\"type\":\"about:blank\",\"title\":\"Bad Request\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response<RS>> update(
            @Parameter(description = "ID of the record to update") @PathVariable @NonNull ID id,
            @Valid @RequestBody @NonNull RQ request) {
        T updated = service.update(id, mapper.toEntity(request));
        return ResponseEntity.ok(Response.success(mapper.toResponse(updated), "Updated successfully"));
    }

    /**
     * Partially updates an existing record.
     * Only non-null fields in the request body are applied via the mapper's
     * {@code updateEntityFromRequest} method (MapStruct IGNORE strategy).
     *
     * <p>Bean validation is intentionally NOT enforced here: a partial payload
     * would fail required-field constraints that are legitimate for POST/PUT.</p>
     *
     * <p>Delegates to {@link BaseService#update(Serializable, Identifiable)} to ensure
     * lifecycle hooks ({@code beforeUpdate}, {@code afterUpdate}) are properly triggered.</p>
     */
    @Operation(summary = "Patch record (Partial)", description = "Updates only the provided fields of an existing record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patched successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-05-07T19:00:00\",\"status\":404,\"message\":\"Record not found with ID: 1\",\"type\":\"about:blank\",\"title\":\"Not Found\"}")))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Response<RS>> patch(
            @Parameter(description = "ID of the record to patch") @PathVariable @NonNull ID id,
            @RequestBody @NonNull RQ request) {
        T existing = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));
        mapper.updateEntityFromRequest(request, existing);
        // Delegate to service.update to ensure beforeUpdate/afterUpdate hooks are triggered.
        T updated = service.update(id, existing);
        return ResponseEntity.ok(Response.success(mapper.toResponse(updated), "Updated successfully"));
    }
}