package org.envycorp.itemservice.controller;

import org.envycorp.itemservice.exception.ItemAccessDeniedException;
import org.envycorp.itemservice.exception.NoSuchItemWithSpecifiedIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler({
            ItemAccessDeniedException.class,
            NoSuchItemWithSpecifiedIdException.class
    })
    public ResponseEntity<String> handleItemsExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
