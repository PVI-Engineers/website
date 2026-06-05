package com.pviengineers.careersapi.exception;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.exception.SdkException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        details
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        LOGGER.warn("Malformed JSON request payload.", ex);
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Malformed JSON",
                        List.of("Request body is not valid JSON.")
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        LOGGER.warn("Bad request error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad request",
                        List.of(ex.getMessage())
                )
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        HttpStatus resolvedStatus = status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
        String message = ex.getReason() == null ? resolvedStatus.getReasonPhrase() : ex.getReason();

        return ResponseEntity.status(resolvedStatus).body(
                new ApiErrorResponse(
                        Instant.now(),
                        resolvedStatus.value(),
                        resolvedStatus.getReasonPhrase(),
                        List.of(message)
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        LOGGER.error("Database integrity validation failed.", ex);
        String rootCause = compactRootCause(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid application data",
                        List.of(
                                "Provided data could not be saved. Please verify inputs and retry.",
                                "Debug: " + rootCause
                        )
                )
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccess(DataAccessException ex) {
        LOGGER.error("Database operation failed.", ex);
        String rootCause = compactRootCause(ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Database unavailable",
                        List.of(
                                "Temporary database issue. Please try again shortly.",
                                "Debug: " + rootCause
                        )
                )
        );
    }

    @ExceptionHandler(SdkException.class)
    public ResponseEntity<ApiErrorResponse> handleSdkException(SdkException ex) {
        LOGGER.error("AWS SDK operation failed.", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Storage service unavailable",
                        List.of("Temporary issue while preparing file upload. Please retry in a moment.")
                )
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntime(RuntimeException ex) {
        LOGGER.error("Unhandled runtime exception.", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal server error",
                        List.of("Unexpected server error. Please try again later.")
                )
        );
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private String compactRootCause(Exception ex) {
        Throwable cause = ex.getCause();
        Throwable mostSpecific = ex;
        while (cause != null && cause != mostSpecific) {
            mostSpecific = cause;
            cause = cause.getCause();
        }

        String message = mostSpecific.getMessage();
        if (message == null || message.isBlank()) {
            message = mostSpecific.getClass().getSimpleName();
        }

        String normalized = message.replaceAll("[\\r\\n\\t]+", " ").trim();
        if (normalized.length() > 240) {
            return normalized.substring(0, 240) + "...";
        }
        return normalized;
    }
}
