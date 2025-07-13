package com.library.library.Exception;

import com.library.library.Exception.Dto.ErrorResponseDto;
import com.library.library.Exception.Validation.InvalidFileExtensionException;
import com.library.library.Exception.Validation.MissingFileExtensionException;
import com.library.library.Exception.infrastructure.AccountLockedException;
import com.library.library.Exception.Validation.RateLimitExceededException;
import com.library.library.Exception.infrastructure.DriveException;
import com.library.library.Security.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.ui.Model;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

@SuppressWarnings("unused")
@ControllerAdvice
public class GlobalExceptionHandler {

    // check if its postman or not
    private boolean isHtmlRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("text/html");
    }

    private Object buildResponse(ErrorResponseDto errorDto, HttpServletRequest request, Model model, String template) {
        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return template;
        }
        return ResponseEntity.status(errorDto.getStatus()).body(errorDto);
    }

    // 400 - Bad Request
    @ExceptionHandler({MethodArgumentNotValidException.class,MissingServletRequestParameterException.class})
    public Object handleValidationErrors(MethodArgumentNotValidException ex,
                                         HttpServletRequest request,
                                         Model model) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ?
                                error.getDefaultMessage() : "Invalid value"
                ));

        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                "Validation failed"
        );
        errorDto.setFieldErrors(fieldErrors);

        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/400";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // 401 - Unauthorized
    @ExceptionHandler({
            AuthenticationException.class,
            ExpiredJwtException.class,
            SignatureException.class,
            BadCredentialsException.class,
            HttpClientErrorException.Unauthorized.class
    })
    public Object handleAuthenticationException(Exception ex,
                                                HttpServletRequest request,
                                                Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI(),
                getAppropriateMessage(ex)
        );

        // Special handling for BadCredentialsException
        if (ex instanceof BadCredentialsException) {
            Integer remainingAttempts = (Integer) request.getAttribute("remainingAttempts");
            if (remainingAttempts != null) {
                errorDto.addDetail("remaining_attempts", remainingAttempts)
                        .addDetail("max_attempts", LoginAttemptService.MAX_ATTEMPTS);
            }
        }
        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/401";
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
    }

    private String getAppropriateMessage(Exception ex) {
        if (ex instanceof ExpiredJwtException) {
            return "Session expired. Please log in again";
        } else if (ex instanceof SignatureException) {
            return "Invalid authentication token";
        } else if (ex instanceof BadCredentialsException) {
            return "Invalid credentials";
        }
        return "Authentication failed: " + ex.getMessage();
    }
    // 403 - Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex,
                                     HttpServletRequest request,
                                     Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.FORBIDDEN,
                request.getRequestURI(),
                "Access denied: " + ex.getMessage()
        );
        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/403";
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
    }

    // 404 - Not Found
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNotFound(NoHandlerFoundException ex,
                                 HttpServletRequest request,
                                 Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                "Resource not found"
        );
        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/404";
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    // 405 - Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                           HttpServletRequest request,
                                           Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.METHOD_NOT_ALLOWED,
                request.getRequestURI(),
                "HTTP method not supported for this endpoint"
        );
        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/405";
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorDto);
    }

    // 409
    // Convert a predefined exception to an HTTP Status code
    @ResponseStatus(value=HttpStatus.CONFLICT,
            reason="Data integrity violation")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void conflict() {
        // Nothing to do
    }

    // 415 - Unsupported Media Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Object handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                              HttpServletRequest request,
                                              Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                request.getRequestURI(),
                "Unsupported media type"
        );
        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/415";
        }

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorDto);
    }
    // 423 - Locked Account handler
    // 423 - Locked Account handler
    @ExceptionHandler(AccountLockedException.class)
    public Object handleLockedAccount(AccountLockedException ex,
                                      HttpServletRequest request,
                                      Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                        HttpStatus.LOCKED,
                        request.getRequestURI(),
                        "Account temporarily locked due to too many failed attempts"
                )
                .addDetail("locked_until", ex.getLockedUntil().toString())
                .addDetail("remaining_time_minutes",
                        Duration.between(Instant.now(), ex.getLockedUntil()).toMinutes())
                .addDetail("action", "Please try again later or contact support");

        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/423";
        }
        return ResponseEntity.status(HttpStatus.LOCKED).body(errorDto);
    }

    // 429 - Rate Limiting (Custom Exception)
    @ExceptionHandler(RateLimitExceededException.class)
    public Object handleRateLimitExceeded(RateLimitExceededException ex,
                                          HttpServletRequest request,
                                          Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.TOO_MANY_REQUESTS,
                request.getRequestURI(),
                ex.getMessage()
        );

        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/429";
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorDto);
    }

    // File Upload Errors
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleFileSizeLimitExceeded(MaxUploadSizeExceededException ex,
                                              HttpServletRequest request,
                                              Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.PAYLOAD_TOO_LARGE,
                request.getRequestURI(),
                "File size exceeds maximum allowed limit"
        );
        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/413";
        }
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorDto);
    }



    // Form Validation Errors
    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException ex,
                                      HttpServletRequest request,
                                      Model model) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ?
                                error.getDefaultMessage() : "Invalid value"
                ));

        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                "Form validation failed"
        );
        errorDto.setFieldErrors(errors);

        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/400";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex,
                                      HttpServletRequest request,
                                      Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                "An unexpected error occurred"
        );
        errorDto.addDetail("operation", "file_upload")
                .addDetail("reason", ex.getCause() != null ?
                        ex.getCause().getMessage() : ex.getMessage());

        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/500";
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }
    // 500 - Server Side Error Google Drive Cause
    @ExceptionHandler(DriveException.class)
    public Object handleDriveException(DriveException ex,
                                       HttpServletRequest request,
                                       Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                ex.getMessage()
        );


        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/500";
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);

    }


    // Specify name of a specific view that will be used to display the error:
    @ExceptionHandler({SQLException.class,DataAccessException.class})
    public Object handleDatabaseErrors(  RuntimeException ex,
                                                    HttpServletRequest request,
                                                    Model model) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                "Database operation failed"
        );
        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/databaseError";
        }
        return ResponseEntity.badRequest().body(errorDto);
    }

    // File Upload Error
    @ExceptionHandler({
            InvalidFileExtensionException.class,
            MissingFileExtensionException.class
    })
    public Object handleFileExtensionErrors(
            RuntimeException ex,
            HttpServletRequest request,
            Model model
    ) {
        ErrorResponseDto errorDto = ErrorResponseDto.create(
                HttpStatus.METHOD_NOT_ALLOWED,
                request.getRequestURI(),
                "File Validation Error"
        );

        if (isHtmlRequest(request)) {
            errorDto.addToModel(model);
            return "error/405";
        }
        return ResponseEntity.badRequest().body(errorDto);
    }
}