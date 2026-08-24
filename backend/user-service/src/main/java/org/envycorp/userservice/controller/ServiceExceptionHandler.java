package org.envycorp.userservice.controller;

import org.envycorp.userservice.exception.NoSuchUserWithIdSpecified;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler(NoSuchUserWithIdSpecified.class)
    public ResponseEntity<String> handleUserException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
