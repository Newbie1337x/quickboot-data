package com.newbie.app.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or fully updating an Example resource.
 *
 * <p>Decouples the API contract from the persistence model:
 * clients only see and send the fields they are allowed to write.
 * Identity and audit fields (id, createdAt, updatedAt, etc.) are
 * never accepted from the client.</p>
 *
 * @param name        Name of the example (required)
 * @param description Optional description
 */
public record ExampleRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description
) {
}
