package com.example.transactionstarter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

// Handles errors and returns clean error messages to the client
@RestControllerAdvice
public class GlobalExceptionHandler {

    // When validation fails (e.g., missing customerId, invalid amount)
    // Return a 400 Bad Request with the list of errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, Object> errorBody = Map.of(
                "error", "Validation failed",
                "details", ex.getBindingResult().getFieldErrors().stream()
                        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                        .collect(Collectors.toList())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }
}
