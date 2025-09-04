package com.library.library.exception;

import com.library.library.exception.Dto.ErrorResponseDto;
import com.library.library.exception.Validation.InvalidFileExtensionException;
import com.library.library.exception.Validation.MissingFileExtensionException;
import com.library.library.exception.Validation.RateLimitExceededException;
import com.library.library.exception.infrastructure.AccountLockedException;
import com.library.library.exception.infrastructure.DriveException;
import com.library.library.security.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

import javax.naming.ServiceUnavailableException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String xRequestedWith = request.getHeader("X-Requested-With");
        return (accept != null && accept.contains("application/json")) || "XMLHttpRequest".equals(xRequestedWith);
    }

    private boolean isHtmlRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("text/html");
    }

    private ResponseEntity<ErrorResponseDto> buildJsonResponse(HttpStatus status, HttpServletRequest request, String message, Map<String, Object> details) {
        ErrorResponseDto error = ErrorResponseDto.create(status, request.getRequestURI(), message);
        if (details != null) details.forEach(error::addDetail);
        return ResponseEntity.status(status).body(error);
    }

    // 400 - Validation Errors
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(Exception ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = null;

        if (ex instanceof MethodArgumentNotValidException manv) {
            fieldErrors = manv.getBindingResult().getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid value"
                    ));
        } else if (ex instanceof BindException be) {
            fieldErrors = be.getBindingResult().getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid value"
                    ));
        } else if (ex instanceof HttpMessageNotReadableException) {
            fieldErrors = Map.of("body", "Malformed or unreadable request body");
        } else if (ex instanceof ConstraintViolationException cve) {
            fieldErrors = cve.getConstraintViolations()
                    .stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    ));
        } else if (ex instanceof MethodArgumentTypeMismatchException me) {
            fieldErrors = Map.of(me.getName(), "Type mismatch. Expected " + me.getRequiredType());
        }

        ErrorResponseDto error = ErrorResponseDto.create(HttpStatus.BAD_REQUEST, request.getRequestURI(), "Validation failed");
        if (fieldErrors != null) error.setFieldErrors(fieldErrors);
        return ResponseEntity.badRequest().body(error);
    }

    // 401 - Unauthorized
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class, ExpiredJwtException.class, SignatureException.class})
    public ResponseEntity<ErrorResponseDto> handleAuthErrors(Exception ex, HttpServletRequest request) {
        String message = switch (ex.getClass().getSimpleName()) {
            case "BadCredentialsException" -> "Invalid credentials";
            case "ExpiredJwtException" -> "Session expired. Please log in again";
            case "SignatureException" -> "Invalid authentication token";
            default -> "Authentication failed";
        };

        Integer remainingAttempts = (Integer) request.getAttribute("remainingAttempts");
        Map<String, Object> details = remainingAttempts != null ?
                Map.of("remaining_attempts", remainingAttempts, "max_attempts", LoginAttemptService.MAX_ATTEMPTS) : null;

        return buildJsonResponse(HttpStatus.UNAUTHORIZED, request, message, details);
    }

    // 403 - Forbidden
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.FORBIDDEN, request, "Access denied: " + ex.getMessage(), null);
    }

    // 404 - Not Found
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.NOT_FOUND, request, "Resource not found", null);
    }

    // 405 - Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.METHOD_NOT_ALLOWED, request, "HTTP method not supported for this endpoint", Map.of("supportedMethods", ex.getSupportedHttpMethods()));
    }

    // 408 - Request Timeout
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<ErrorResponseDto> handleRequestTimeout(AsyncRequestTimeoutException ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.REQUEST_TIMEOUT, request, "Request timed out", null);
    }

    // 413 - Payload Too Large
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleFileSizeExceeded(HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.PAYLOAD_TOO_LARGE, request, "File size exceeds maximum allowed limit", null);
    }

    // 415 - Unsupported Media Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, request, "Unsupported media type: " + ex.getContentType(), null);
    }

    // 406 - Not Acceptable
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.NOT_ACCEPTABLE, request, "Requested media type not acceptable", Map.of("supportedTypes", ex.getSupportedMediaTypes()));
    }

    // 422 - Unprocessable Entity
    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    public ResponseEntity<ErrorResponseDto> handleUnprocessableEntity(HttpClientErrorException.UnprocessableEntity ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, "Unprocessable Entity: " + ex.getMessage(), null);
    }

    // 423 - Account Locked
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountLocked(AccountLockedException ex, HttpServletRequest request) {
        Map<String, Object> details = Map.of(
                "locked_until", ex.getLockedUntil().toString(),
                "remaining_time_minutes", Duration.between(Instant.now(), ex.getLockedUntil()).toMinutes(),
                "action", "Please try again later or contact support"
        );
        return buildJsonResponse(HttpStatus.LOCKED, request, "Account temporarily locked due to too many failed attempts", details);
    }

    // 429 - Rate Limit Exceeded
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleRateLimitExceeded(RateLimitExceededException ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.TOO_MANY_REQUESTS, request, ex.getMessage(), null);
    }

    // 409 - Conflict / Data Integrity
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(DataIntegrityViolationException ex, HttpServletRequest request) {
        String msg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "Data integrity violation";
        return buildJsonResponse(HttpStatus.CONFLICT, request, msg, null);
    }

    // File Extension Errors
    @ExceptionHandler({InvalidFileExtensionException.class, MissingFileExtensionException.class})
    public ResponseEntity<ErrorResponseDto> handleFileExtensionErrors(RuntimeException ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, request, "File validation error: " + ex.getMessage(), null);
    }

    // 500 / Internal Server Error
    @ExceptionHandler({Exception.class, DriveException.class, SQLException.class, DataAccessException.class})
    public ResponseEntity<ErrorResponseDto> handleServerErrors(Exception ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR, request,
                ex.getMessage() != null ? ex.getMessage() : "Internal server error", null);
    }

    // 503 - Service Unavailable
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponseDto> handleServiceUnavailable(ServiceUnavailableException ex, HttpServletRequest request) {
        return buildJsonResponse(HttpStatus.SERVICE_UNAVAILABLE, request, "Service Unavailable: " + ex.getMessage(), null);
    }
}
