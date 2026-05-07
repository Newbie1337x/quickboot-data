package com.newbie.app.common.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.newbie.app.common.dto.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handling using the Problem Details pattern.
 * Catches various system exceptions and transforms them into standardized Response objects.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException (404).
     *
     * @param ex The exception
     * @return Standardized error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response<Void>> handleNotFound(ResourceNotFoundException ex) {
        Response<Void> response = Response.error(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles Spring MVC 404 when no handler/route matches the request path (404).
     *
     * @param ex The exception
     * @return Standardized error response
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Response<Void>> handleNoHandlerFound(NoResourceFoundException ex) {
        Response<Void> response = Response.error(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "The requested path does not exist: " + ex.getResourcePath()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles HTTP 405 when a route exists but the HTTP method is not supported.
     *
     * @param ex The exception
     * @return Standardized error response
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        Response<Void> response = Response.error(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint"
        );
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handles validation errors (@Valid) (400).
     * Extracts field-level errors and includes them in the response.
     *
     * @param ex The validation exception
     * @return Detailed error response with field mappings
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Response<Void> response = Response.error(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "One or more fields have validation errors",
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles JSON parse errors or malformed requests (400).
     *
     * @param ex The reading exception
     * @return Standardized error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Response<Void> response = Response.error(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON Request",
                "The request body contains invalid data or a wrong format (e.g., date format)"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles database constraint violations (409 Conflict).
     * Covers unique key violations, foreign key errors, and not-null constraint failures
     * that are caught at the persistence layer.
     *
     * @param ex The exception
     * @return Standardized conflict error response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Response<Void> response = Response.error(
                HttpStatus.CONFLICT.value(),
                "Data Conflict",
                "The request conflicts with the current state of the database. " +
                "A unique constraint or data integrity rule was violated."
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles optimistic locking failures (409 Conflict).
     * Triggered when two concurrent requests attempt to update the same entity simultaneously.
     * The second writer loses and receives a conflict response, preventing lost updates.
     *
     * @param ex The exception
     * @return Standardized conflict error response
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Response<Void>> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex) {
        Response<Void> response = Response.error(
                HttpStatus.CONFLICT.value(),
                "Concurrent Modification",
                "The resource was modified by another request. Please reload and try again."
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Fallback handler for all other unexpected exceptions (500).
     *
     * @param ex The exception
     * @return Generic error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGeneralException(Exception ex) {
        Response<Void> response = Response.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

