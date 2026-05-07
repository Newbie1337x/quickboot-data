package com.newbie.app.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized API response wrapper following the Problem Details pattern (RFC 7807).
 * Used for both successful data payloads and structured error responses.
 *
 * @param timestamp Time of the response
 * @param status    HTTP status code
 * @param message   Descriptive message
 * @param data      The actual payload (null for errors)
 * @param type      Error type URI (RFC 7807)
 * @param title     Error title (RFC 7807)
 * @param errors    Map of field-level validation errors
 * @param <T>       Type of the data payload
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API Response Wrapper")
public record Response<T>(
        @Schema(example = "2026-05-07T19:00:00", description = "Response timestamp (ISO 8601)")
        LocalDateTime timestamp,

        @Schema(example = "200", description = "HTTP Status Code")
        int status,

        @Schema(example = "Operation successful", description = "Human-readable message")
        String message,

        @Schema(description = "Generic data payload")
        T data,

        @Schema(example = "about:blank", description = "RFC 7807 problem type")
        String type,

        @Schema(example = "Success", description = "RFC 7807 problem title")
        String title,

        @Schema(description = "Map of validation errors (if any)")
        Map<String, Object> errors
) {

    /**
     * Factory method for a successful response.
     *
     * @param data    Payload
     * @param message Success message
     * @return Success Response
     */
    public static <T> Response<T> success(T data, String message) {
        return new Response<>(LocalDateTime.now(), 200, message, data, null, null, null);
    }

    /**
     * Factory method for an error response.
     *
     * @param status HTTP Status
     * @param title  Short error title
     * @param detail Detailed error message
     * @return Error Response
     */
    public static <T> Response<T> error(int status, String title, String detail) {
        return new Response<>(LocalDateTime.now(), status, detail, null, "about:blank", title, null);
    }

    /**
     * Factory method for an error response with multiple details (e.g., validation errors).
     *
     * @param status HTTP Status
     * @param title  Short error title
     * @param detail Detailed error message
     * @param errors Map of specific errors
     * @return Detailed Error Response
     */
    public static <T> Response<T> error(int status, String title, String detail, Map<String, Object> errors) {
        return new Response<>(LocalDateTime.now(), status, detail, null, "about:blank", title, errors);
    }
}
