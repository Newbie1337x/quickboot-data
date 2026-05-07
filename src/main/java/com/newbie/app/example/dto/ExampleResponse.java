package com.newbie.app.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO for the Example resource.
 *
 * <p>Controls exactly what the client receives. Only safe, non-sensitive fields are
 * exposed. Internal persistence details (e.g. {@code @Version}) are never included.</p>
 *
 * <p>Date fields are serialized in ISO 8601 format (e.g. {@code 2026-05-07T19:50:55})
 * via the global Jackson configuration.</p>
 *
 * @param id          Unique identifier
 * @param name        Name of the example
 * @param description Optional description
 * @param createdBy   Username that created the record
 * @param updatedBy   Username that last modified the record
 * @param createdAt   Creation timestamp (ISO 8601)
 * @param updatedAt   Last modification timestamp (ISO 8601)
 */
@Schema(description = "Example resource response")
public record ExampleResponse(

        @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Name of the example", example = "My Example")
        String name,

        @Schema(description = "Optional description", example = "A detailed description")
        String description,

        @Schema(description = "User who created this record", example = "admin", accessMode = Schema.AccessMode.READ_ONLY)
        String createdBy,

        @Schema(description = "User who last modified this record", example = "admin", accessMode = Schema.AccessMode.READ_ONLY)
        String updatedBy,

        @Schema(description = "Creation timestamp (ISO 8601)", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt,

        @Schema(description = "Last modification timestamp (ISO 8601)", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime updatedAt
) {
}
