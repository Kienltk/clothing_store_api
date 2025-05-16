package com.clothingstore.clothing_store_api.exception;

import com.clothingstore.clothing_store_api.response.ResponseObject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String combinedMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Validation failed");

        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), combinedMessage, null),
                HttpStatus.BAD_REQUEST
        );
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseObject<String>> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseObject<String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseObject<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ResponseObject<String>> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return new ResponseEntity<>(
                new ResponseObject<>(401, ex.getMessage(), null),
                HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<String>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred", null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

