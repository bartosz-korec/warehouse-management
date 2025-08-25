package com.bartoszkorec.warehouse_management.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ProblemDetail handleNotFound(Exception ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Not Found");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getRequestURI());
        pd.setProperty("method", request.getMethod());
        pd.setProperty("exception", ex.getClass().getSimpleName());
        return pd;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponse> handleResponseException(ResponseStatusException ex) {
        return buildResponseEntity(new Exception(ex.getReason()), HttpStatus.valueOf(ex.getStatusCode().value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        return buildResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())
                ));

        String message = errors.isEmpty() ? "Validation failed." : errors.entrySet().stream()
                .map(e -> e.getKey() + ": " + String.join(", ", e.getValue()))
                .collect(Collectors.joining("; "));
        return buildResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ExceptionResponse> buildResponseEntity(Exception ex, HttpStatus status) {
        return buildResponseEntity(ex.getMessage(), status);
    }

    private ResponseEntity<ExceptionResponse> buildResponseEntity(String message, HttpStatus status) {
        ExceptionResponse error = ExceptionResponse.builder()
                .status(status.value())
                .message(message)
                .timeStamp(System.currentTimeMillis())
                .build();
        return new ResponseEntity<>(error, status);
    }
}
