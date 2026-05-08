package com.newbie.app.example.dto;

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

) {
}
